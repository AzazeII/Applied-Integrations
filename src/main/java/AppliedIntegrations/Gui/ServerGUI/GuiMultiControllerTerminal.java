package AppliedIntegrations.Gui.ServerGUI;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.Server.ContainerMultiControllerTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.ServerGUI.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiListTypeButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketContainerWidgetSync;
import AppliedIntegrations.Network.Packets.Server.PacketServerFeedback;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.container.slot.SlotFake;
import appeng.core.localization.GuiText;
import appeng.fluids.util.AEFluidInventory;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
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


// TODO Rewrite this GUI, now it will be similar to normal security terminal.
// Instead of biometric cards it will accept networks cards.

/**
 * @Author Azazell
 */
public class GuiMultiControllerTerminal extends AIBaseGui implements IWidgetHost {

	private static final int GUI_WIDTH = 192;

	private static final int GUI_HEIGH = 256;

	public EntityPlayer player;

	private GuiSecurityPermissionsButton securityPermissionButton;

	private GuiStorageChannelButton storageChannelButton;

	private GuiListTypeButton listTypeButton;

	private TileMultiControllerTerminal terminal;

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

	private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server_terminal.png");

	public GuiMultiControllerTerminal(ContainerMultiControllerTerminal container, EntityPlayer player) {

		super(container, player);

		this.player = player;

		// Iterate until i = size
		for (int i = 0; i < GuiSecurityPermissionsButton.getPermissionList().size(); i++)
			// Put new inv in tanks
			tanks.put(GuiSecurityPermissionsButton.getPermissionList().get(i), new AEFluidInventory(null, 27));
	}

	public IncludeExclude getIncludeExcludeMode() {

		return permissionChannelModeMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel());
	}

	public void setIncludeExcludeMode(IncludeExclude mode) {

		permissionChannelModeMap.get(securityPermissionButton.getCurrentPermissions()).put(storageChannelButton.getChannel(), mode);
	}

	@Override
	public ISyncHost getSyncHost() {

		return terminal;
	}

	@Override
	public void setSyncHost(ISyncHost host) {

		if (host instanceof TileMultiControllerTerminal) {
			terminal = (TileMultiControllerTerminal) host;
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		// Add new security permissions button to button list
		buttonList.add(securityPermissionButton = new GuiSecurityPermissionsButton(this, 0, this.guiLeft + 72 + 32, this.guiTop + 86, 16, 16, ""));

		// Add new storage channel button to button list
		buttonList.add(storageChannelButton = new GuiStorageChannelButton(this, 0, this.guiLeft + 72 + 18 + 32, this.guiTop + 86, 16, 16, ""));

		// Add new black/white list button to button list
		buttonList.add(listTypeButton = new GuiListTypeButton(this, 0, this.guiLeft + 72 + 36 + 32, this.guiTop + 86, 16, 16, ""));

		// ************# Add Filter Slots #************ //
		// Iterate for each security permission
		GuiSecurityPermissionsButton.getPermissionList().forEach((permissions -> {
			// Temp map
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>> tempMap = new LinkedHashMap<>();

			// Iterate for each storage channel is list
			GuiStorageChannelButton.getChannelList().forEach((chan) -> {
				// Get widget from API
				Constructor<? extends IChannelWidget> channelWidgetConstructor = Objects.requireNonNull(AIApi.instance()).getWidgetFromChannel(chan);

				// Check not null
				if (channelWidgetConstructor != null) {
					// List of widget for this channel
					List<IChannelWidget<?>> widgetList = new LinkedList<>();

					// Iterate for each row as X
					for (int x = 0; x < SLOT_COLUMNS; x++) {
						// Iterate for each column as Y
						for (int y = 0; y < SLOT_ROWS; y++) {
							// Unique id of slot
							int slotID = y * SLOT_COLUMNS + x;

							// Check if slots is container-sided
							if (channelWidgetConstructor.getDeclaringClass() == IChannelContainerWidget.class) {
								continue;
							}

							try {
								// Try to construct with old item slot constructor:
								// I.E: IWidgetHost host, int x, int y
								widgetList.add(channelWidgetConstructor.newInstance(this, SLOT_X + 18 * x, SLOT_Y + 18 * y));

								// Skip
								continue;
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}

							try {
								// Try to construct with fluid slot constructor:
								// I.E: public WidgetFluidSlot(IAEFluidTank fluids, int slot, int id, int x, int y, IWidgetHost host)
								widgetList.add(channelWidgetConstructor.newInstance(tanks.get(permissions), slotID, x + y, SLOT_X + 18 * x, SLOT_Y + 18 * y, this));

								// Skip
								continue;
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}

							try {
								// Try to construct with AI constructor:
								// I.E: public WidgetEnergySlot(final IWidgetHost hostGui,final int id, final int posX, final int posY, final boolean shouldRender)
								IChannelWidget<?> widget = channelWidgetConstructor.newInstance(this, slotID, SLOT_X + 18 * x, SLOT_Y + 18 * y, true);

								// Check if widget is WidgetEnergySlot
								if (widget instanceof WidgetEnergySlot) {
									// Add shifted widget
									widgetList.add(channelWidgetConstructor.newInstance(this, slotID, SLOT_X - 1 + 18 * x, SLOT_Y - 1 + 18 * y, true));
								} else {
									// Add original widget
									widgetList.add(widget);
								}
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}
						}
					}

					// Map list to permissionChannelWidgetMap
					tempMap.put(chan, widgetList);
				}
			});

			// Add temp map to main map
			permissionChannelWidgetMap.put(permissions, tempMap);
		}));

		// Link widgets from container to widgets in GUI
		initWidgetLinkage((ContainerMultiControllerTerminal) inventorySlots);

		// ************# Add Filter Slots #************ //

		// Iterate for each security permissions
		GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
			// Temp map
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> tempMap = new LinkedHashMap<>();

			// Iterate for each storage channel
			GuiStorageChannelButton.getChannelList().forEach((chan -> {
				// Add default list mode to map
				tempMap.put(chan, AIConfig.defaultListMode);
			}));

			// Put temp map in permission channel map
			permissionChannelModeMap.put(securityPermissions, tempMap);
		}));
	}

	/*
	This method concatenates our map with map from client-sided container.
	Simply: This method do this:
	in.    out.
	1 0    1
	0 1 -> 1
	1 0    1
	 */
	public void initWidgetLinkage(ContainerMultiControllerTerminal containerMultiControllerTerminal) {
		// Iterate for each map in outer map
		containerMultiControllerTerminal.getOuterMap().forEach((securityPermissions, innerMap) -> {
			// Get inner gui map
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>> chanListMap = permissionChannelWidgetMap.get(securityPermissions);

			// Iterate for each list in inner map
			innerMap.forEach((chan, list) -> {
				// Add all elements of given list to already existing list
				chanListMap.get(chan).addAll(list);
			});

			// Put map in existing map
			permissionChannelWidgetMap.put(securityPermissions, chanListMap);
		});
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY) {
		// Pass call to default function
		drawDefaultBackground();

		// Set color
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Bind our texture
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		// Draw bottom(below cell bar) texture
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);
	}

	@Override
	protected void handleMouseClick(final Slot slot, final int slotIdx, final int mouseButton, final ClickType action) {
		// Check if is fake slot
		if (slot instanceof SlotFake) {
			// Cast container to higher class
			ContainerMultiControllerTerminal containerMultiControllerTerminal = (ContainerMultiControllerTerminal) inventorySlots;

			// Get client player
			EntityPlayer player = mc.player;

			// Iterate for each widget in inner-inner list
			containerMultiControllerTerminal.forEachWidget((widget) -> {
				// Check if slot wrapper of widget is given slot
				if (slot == widget.getSlotWrapper()) {
					// Get stack in player hand and update widget stack
					widget.setAEStack(AEItemStack.fromItemStack(player.inventory.getItemStack()));

					// Sync with server
					NetworkHandler.sendToServer(new PacketContainerWidgetSync(player.inventory.getItemStack(), terminal, slot.xPos, slot.yPos));

					// Encode data
					encodeCardTag();
				}
			});
		}

		// Call super
		super.handleMouseClick(slot, slotIdx, mouseButton, action);
	}

	private void encodeCardTag() {
		// Encode all GUI data into one tag
		NBTTagCompound tag = NetworkCard.encodeDataInTag(permissionChannelWidgetMap, permissionChannelModeMap, getCardStack());

		// Notify server
		syncWithServer(tag);

		// Change NBT tag on client
		getCardStack().setTagCompound(tag);
	}

	private ItemStack getCardStack() {
		// Check if container has card
		if (((ContainerMultiControllerTerminal) inventorySlots).hasCard()) {
			// Get card stack from container
			return ((ContainerMultiControllerTerminal) inventorySlots).getCard();
		}

		return null;
	}

	private void syncWithServer(NBTTagCompound tag) {

		NetworkHandler.sendToServer(new PacketServerFeedback(tag, terminal));
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		// Check if container has no network tool in slot
		if (!isCardValid()) {
			return;
		}

		// Check if button is security permissions button
		if (btn == securityPermissionButton) {
			// Cycle mode of button
			securityPermissionButton.cycleMode();

			// Check if button is storage channel button
		} else if (btn == storageChannelButton) {
			// Cycle channel of button
			storageChannelButton.cycleChannel();

			// Check if button is black/white list button
		} else if (btn == listTypeButton) {
			// Toggle mode of button
			listTypeButton.toggleMode();

			// Encode card tag
			encodeCardTag();
		}
	}

	public boolean isCardValid() {
		// Check if container has card
		if (((ContainerMultiControllerTerminal) inventorySlots).hasCard()) {
			// Get card stack from container
			ItemStack stack = getCardStack();

			// Return true if stack has NBT tag with record of network
			return Platform.openNbtData(stack).getBoolean(NetworkCard.NBT_KEY_HAS_NET);
		}

		return false;
	}

	@Override
	public void drawScreen(int mX, int mY, float pOpacity) {
		// Call parent class
		super.drawScreen(mX, mY, pOpacity);

		// Cast container to server terminal container
		ContainerMultiControllerTerminal containerMultiControllerTerminal = (ContainerMultiControllerTerminal) inventorySlots;

		// Iterate for each list in each inner map in outer map of container
		containerMultiControllerTerminal.getOuterMap().forEach((perm, map) -> map.forEach((chan, list) -> list.forEach((widget) -> {
			// Make slot (in)visible depending on card state
			widget.setVisible(isCardValid() && chan == storageChannelButton.getChannel() && perm == securityPermissionButton.getCurrentPermissions());
		})));

		// Check if container has no network card in slot
		if (!isCardValid()) {
			return;
		}

		// Call card update change handler
		cardChangeUpdateHandler.onChange(getCardStack(), (stack) -> onCardChanged());

		// Iterate for each element of list from current channel from map from current permission
		// Draw widget
		permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach((slot) -> {
			// Check if mouse over widget
			if (slot.isMouseOverWidget(mX, mY)) {
				// Create tooltip list
				List<String> tip = new ArrayList<>();

				// Check if slot has energy stack
				if (slot.getAEStack() != null) {
					// Add entry in list
					tip.add(slot.getStackTip());

					// Draw tooltip
					drawHoveringText(tip, mX, mY, fontRenderer);
				}
			}
		});
	}

	private void onCardChanged() {
		// Check not null
		if (getCardStack() != null) {
			// Get stack tag
			NBTTagCompound tag = Platform.openNbtData(getCardStack());

			// Check if stack has valid tags
			if (tag.hasKey(NetworkCard.KEY_SUB + "_INJECT")) {
				// Decode map pair
				Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>>, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>>> data = NetworkCard.decodeDataFromTag(tag);

				// Update mode map
				permissionChannelModeMap = data.getRight();

				// Iterate for each security permissions
				GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
					// Iterate for each channel
					GuiStorageChannelButton.getChannelList().forEach((channel -> {
						// Create atomic integer
						AtomicInteger counter = new AtomicInteger();

						// Iterate for each widget in list
						permissionChannelWidgetMap.get(securityPermissions).get(channel).forEach((iChannelWidget -> {
							List<IAEStack<? extends IAEStack>> stackList = data.getLeft().get(securityPermissions).get(channel);

							// Check if list has enough elements
							if (stackList.size() >= counter.get() + 1)
							// Update stack in channel widget
							{
								iChannelWidget.setAEStack(stackList.get(counter.get()));
							}

							// Add to counter
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

		// Draw gui strings
		this.fontRenderer.drawString("Multi-Controller Terminal", 8, 6, 4210752); // (Name)
		this.fontRenderer.drawString("Network Card Editor", 8, this.ySize - 96 + 3, 4210752); // (Editor)
		this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 36, 4210752); // (Player inv.)

		// Check if container has no network tool in slot
		if (!isCardValid()) {
			return;
		}

		// Iterate for each element of list from current channel from map from current permission
		// Draw widget
		permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach(IChannelWidget::drawWidget);
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Call super
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// Iterate for each widget of current storage channel
		permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach((widget) -> {
			// Check if mouse is over widget
			if (widget.isMouseOverWidget(mouseX, mouseY)) {
				// Update stack
				widget.setAEStack(Objects.requireNonNull(AIApi.instance()).getAEStackFromItemStack(storageChannelButton.getChannel(), player.inventory.getItemStack()));

				// Encode card tag
				encodeCardTag();
			}
		});
	}
}