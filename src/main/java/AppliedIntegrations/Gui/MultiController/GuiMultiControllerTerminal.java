package AppliedIntegrations.Gui.MultiController;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerTerminal;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.MultiController.SubGui.Buttons.GuiListTypeButton;
import AppliedIntegrations.Gui.MultiController.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.MultiController.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.MultiController.PacketContainerWidgetSync;
import AppliedIntegrations.Network.Packets.MultiController.PacketServerFeedback;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.container.slot.SlotFake;
import appeng.core.localization.GuiText;
import appeng.fluids.util.AEFluidInventory;
import appeng.util.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal.*;

/**
 * @Author Azazell
 */
public class GuiMultiControllerTerminal extends AIGui implements IWidgetHost {
	private static final int GUI_WIDTH = 192;
	private static final int GUI_HEIGH = 256;
	public EntityPlayer player;
	private GuiSecurityPermissionsButton securityPermissionButton;
	private GuiStorageChannelButton storageChannelButton;
	private GuiListTypeButton listTypeButton;
	private ChangeHandler<ItemStack> cardChangeUpdateHandler = new ChangeHandler<>();

	/**
	 * Contains maps of lists of 27 widgets linked to given storage channel from given security permission.
	 * Each widget represent filter for material in given storage channel.
	 */
	private LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>>> permissionChannelWidgetMap = new LinkedHashMap<>();

	/**
	 * Contains maps of modes linked to given storage channel from given security permissions
	 */
	private LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> permissionChannelModeMap = new LinkedHashMap<>();
	private LinkedHashMap<SecurityPermissions, AEFluidInventory> tanks = new LinkedHashMap<>();
	private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/multi_controller_terminal.png");

	public GuiMultiControllerTerminal(ContainerMultiControllerTerminal container, EntityPlayer player) {
		super(container, player);

		this.player = player;
		for (int i = 0; i < GuiSecurityPermissionsButton.getPermissionList().size(); i++) {
			tanks.put(GuiSecurityPermissionsButton.getPermissionList().get(i), new AEFluidInventory(null, 27));
		}
	}

	public IncludeExclude getIncludeExcludeMode() {
		return permissionChannelModeMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel());
	}

	public void setIncludeExcludeMode(IncludeExclude mode) {
		permissionChannelModeMap.get(securityPermissionButton.getCurrentPermissions()).put(storageChannelButton.getChannel(), mode);
	}

	@Override
	public ISyncHost getSyncHost() {
		return getContainer().terminal;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if (host instanceof TileMultiControllerTerminal) {
			getContainer().terminal = (TileMultiControllerTerminal) host;
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(securityPermissionButton = new GuiSecurityPermissionsButton(this, 0, this.guiLeft + 72 + 32, this.guiTop + 86, 16, 16, ""));
		buttonList.add(storageChannelButton = new GuiStorageChannelButton(this, 0, this.guiLeft + 72 + 18 + 32, this.guiTop + 86, 16, 16, ""));
		buttonList.add(listTypeButton = new GuiListTypeButton(this, 0, this.guiLeft + 72 + 36 + 32, this.guiTop + 86, 16, 16, ""));

		// Adding filter slots here.
		// Each action&channel pair has 27 filter slots when they are selected
		GuiSecurityPermissionsButton.getPermissionList().forEach((permissions -> {
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>> tempMap = new LinkedHashMap<>();
			ContainerMultiControllerTerminal.channelList.forEach((chan) -> {
				Constructor<? extends IChannelWidget> channelWidgetConstructor = Objects.requireNonNull(AIApi.instance()).getWidgetFromChannel(chan);
				if (channelWidgetConstructor != null) {
					List<IChannelWidget<?>> widgetList = new LinkedList<>();
					for (int x = 0; x < SLOT_COLUMNS; x++) {
						for (int y = 0; y < SLOT_ROWS; y++) {
							int slotID = y * SLOT_COLUMNS + x;
							if (channelWidgetConstructor.getDeclaringClass() == IChannelContainerWidget.class) {
								continue;
							}

							try {
								// Try to construct with old item slot constructor:
								// I.E: IWidgetHost host, int x, int y
								widgetList.add(channelWidgetConstructor.newInstance(this, SLOT_X + 18 * x, SLOT_Y + 18 * y));
								continue;
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}

							try {
								// Try to construct with fluid slot constructor:
								// I.E: public WidgetFluidSlot(IAEFluidTank fluids, int slot, int id, int x, int y, IWidgetHost host)
								widgetList.add(channelWidgetConstructor.newInstance(tanks.get(permissions), slotID, x + y, SLOT_X + 18 * x, SLOT_Y + 18 * y, this));
								continue;
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}

							try {
								// Try to construct with AI constructor:
								// I.E: public WidgetEnergySlot(final IWidgetHost hostGui,final int id, final int posX, final int posY, final boolean shouldRender)
								IChannelWidget<?> widget = channelWidgetConstructor.newInstance(this, slotID, SLOT_X + 18 * x, SLOT_Y + 18 * y, true);

								if (widget instanceof WidgetEnergySlot) {
									widgetList.add(channelWidgetConstructor.newInstance(this, slotID, SLOT_X - 1 + 18 * x, SLOT_Y - 1 + 18 * y, true));
								} else {
									widgetList.add(widget);
								}
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}
						}
					}

					tempMap.put(chan, widgetList);
				}
			});

			permissionChannelWidgetMap.put(permissions, tempMap);
		}));

		initWidgetLinkage((ContainerMultiControllerTerminal) inventorySlots);

		// Iterate for each security permissions
		GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> tempMap = new LinkedHashMap<>();
			ContainerMultiControllerTerminal.channelList.forEach((chan -> {
				tempMap.put(chan, AIConfig.defaultListMode);
			}));

			permissionChannelModeMap.put(securityPermissions, tempMap);
		}));
	}

	/*
		This method concatenates our map with map from client-sided container.
	 */
	public void initWidgetLinkage(ContainerMultiControllerTerminal containerMultiControllerTerminal) {
		containerMultiControllerTerminal.getOuterMap().forEach((securityPermissions, innerMap) -> {
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>> chanListMap = permissionChannelWidgetMap.get(securityPermissions);
			innerMap.forEach((chan, list) -> {
				chanListMap.get(chan).addAll(list);
			});

			permissionChannelWidgetMap.put(securityPermissions, chanListMap);
		});
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);
	}

	@Override
	protected void handleMouseClick(final Slot slot, final int slotIdx, final int mouseButton, final ClickType action) {
		if (slot instanceof SlotFake) {
			ContainerMultiControllerTerminal containerMultiControllerTerminal = (ContainerMultiControllerTerminal) inventorySlots;
			EntityPlayer player = mc.player;

			containerMultiControllerTerminal.forEachWidget((widget) -> {
				if (slot == widget.getSlotWrapper()) {
					widget.setAEStack(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(player.inventory.getItemStack()));
					NetworkHandler.sendToServer(new PacketContainerWidgetSync(player.inventory.getItemStack(), getContainer().terminal,
							slot.xPos, slot.yPos));
					encodeCardTag();
				}
			});
		}

		super.handleMouseClick(slot, slotIdx, mouseButton, action);
	}

	private void encodeCardTag() {
		NBTTagCompound tag = NetworkCard.encodeDataInTag(permissionChannelWidgetMap, permissionChannelModeMap, getCardStack());
		syncWithServer(tag);
		getCardStack().setTagCompound(tag);
	}

	private ContainerMultiControllerTerminal getContainer() {
		return (ContainerMultiControllerTerminal) inventorySlots;
	}

	private ItemStack getCardStack() {
		if (getContainer().hasCard()) {
			return getContainer().getCard();
		}

		return null;
	}

	private void syncWithServer(NBTTagCompound tag) {
		NetworkHandler.sendToServer(new PacketServerFeedback(tag, getContainer().terminal));
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		if (!isCardValid()) {
			return;
		}

		if (btn == securityPermissionButton) {
			securityPermissionButton.cycleMode();
		} else if (btn == storageChannelButton) {
			storageChannelButton.cycleChannel();
		} else if (btn == listTypeButton) {
			listTypeButton.toggleMode();
			encodeCardTag();
		}
	}

	public boolean isCardValid() {
		if (((ContainerMultiControllerTerminal) inventorySlots).hasCard()) {
			ItemStack stack = getCardStack();
			return Platform.openNbtData(stack).getBoolean(NetworkCard.NBT_KEY_HAS_NET);
		}

		return false;
	}

	@Override
	public void drawScreen(int mX, int mY, float pOpacity) {
		super.drawScreen(mX, mY, pOpacity);
		ContainerMultiControllerTerminal containerMultiControllerTerminal = (ContainerMultiControllerTerminal) inventorySlots;
		containerMultiControllerTerminal.getOuterMap().forEach((perm, map) -> map.forEach((chan, list) -> list.forEach((widget) -> {
			widget.setVisible(isCardValid() && chan == storageChannelButton.getChannel() && perm == securityPermissionButton.getCurrentPermissions());
		})));

		if (!isCardValid()) {
			return;
		}

		cardChangeUpdateHandler.onChange(getCardStack(), (stack) -> onCardChanged());
		permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach((slot) -> {
			if (slot.isMouseOverWidget(mX, mY)) {
				List<String> tip = new ArrayList<>();

				if (slot.getAEStack() != null && !slot.getStackTip().equals("")) {
					tip.add(slot.getStackTip());
					drawHoveringText(tip, mX, mY, fontRenderer);
				}
			}
		});
	}

	private void onCardChanged() {
		// Refresh GUI when network card is changed
		if (getCardStack() != null) {
			NBTTagCompound tag = Platform.openNbtData(getCardStack());

			if (tag.hasKey(NetworkCard.KEY_SUB + "_INJECT")) {
				Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>,
						List<IAEStack<? extends IAEStack>>>>,
						LinkedHashMap<SecurityPermissions,
						LinkedHashMap<IStorageChannel<? extends IAEStack<?>>,
						IncludeExclude>>> data = NetworkCard.decodeDataFromTag(tag);

				permissionChannelModeMap = data.getRight();

				GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
					ContainerMultiControllerTerminal.channelList.forEach((channel -> {
						AtomicInteger counter = new AtomicInteger();

						permissionChannelWidgetMap.get(securityPermissions).get(channel).forEach((iChannelWidget -> {
							List<IAEStack<? extends IAEStack>> stackList = data.getLeft().get(securityPermissions).get(channel);

							if (stackList != null && stackList.size() >= counter.get() + 1) {
								// Update stack in channel widget
							}

							counter.incrementAndGet();
						}));
					}));
				}));
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.fontRenderer.drawString("Multi-Controller Terminal", 8, 6, 4210752);
		this.fontRenderer.drawString("Network Card Editor", 8, this.ySize - 96 + 3, 4210752);
		this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 36, 4210752);

		if (!isCardValid()) {
			return;
		}

		permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach(IChannelWidget::drawWidget);
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach((widget) -> {
			if (widget.isMouseOverWidget(mouseX, mouseY)) {
				widget.setAEStack(Objects.requireNonNull(AIApi.instance()).getAEStackFromItemStack(storageChannelButton.getChannel(),
						player.inventory.getItemStack(), getContainer().terminal.getHostWorld()));

				encodeCardTag();
			}
		});
	}
}