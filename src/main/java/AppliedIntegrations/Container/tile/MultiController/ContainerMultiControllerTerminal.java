package AppliedIntegrations.Container.tile.MultiController;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Gui.MultiController.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

import static AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal.*;
import static appeng.util.Platform.isServer;

/**
 * @Author Azazell
 */
public class ContainerMultiControllerTerminal extends ContainerWithPlayerInventory {
	public static ArrayList<IStorageChannel<? extends IAEStack<?>>> channelList
			= new ArrayList<>(AEApi.instance().storage().storageChannels());

	private final SlotRestrictive cardSlot;

	public TileMultiControllerTerminal terminal;

	private LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelContainerWidget<?>>>> permissionChannelWidgetMap = new LinkedHashMap<>();

	public ContainerMultiControllerTerminal(TileMultiControllerTerminal terminal, EntityPlayer player) {
		super(player);

		super.bindPlayerInventory(player.inventory, 119, 177);

		super.addSlotToContainer(this.cardSlot = new SlotRestrictive(terminal.editorInv, 0, 37, 86) {
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {

				return AppliedIntegrations.modid + ":gui/slots/network_card_slot";
			}
		});

		this.terminal = terminal;
		this.terminal.listeners.add(this);
		if (isServer()) {
			return;
		}

		this.initWidgets(terminal);
	}

	public void initWidgets(TileMultiControllerTerminal terminal) {
		// Here we initialize widget for each operation, for each AE2 storage channel
		GuiSecurityPermissionsButton.getPermissionList().forEach((permissions -> {
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelContainerWidget<?>>> tempMap = new LinkedHashMap<>();
			AEApi.instance().storage().storageChannels().forEach((chan) -> {
				Constructor<? extends IChannelWidget> channelWidgetConstructor = Objects.requireNonNull(AIApi.instance()).getWidgetFromChannel(chan);
				if (channelWidgetConstructor != null) {
					List<IChannelContainerWidget<?>> widgetList = new LinkedList<>();

					for (int x = 0; x < SLOT_COLUMNS; x++) {
						for (int y = 0; y < SLOT_ROWS; y++) {
							try {
								// Try to construct with item slot constructor:
								// I.E: public WidgetItemSlot(IWidgetHost host, int x, int y)
								IChannelWidget widget = channelWidgetConstructor.newInstance(SLOT_X + 18 * x, SLOT_Y + 18 * y);
								if (!(widget instanceof IChannelContainerWidget)) {
									continue;
								}

								widgetList.add((IChannelContainerWidget<?>) widget);
								addSlotToContainer(((IChannelContainerWidget<?>) widget).getSlotWrapper());
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

							}
						}
					}

					tempMap.put(chan, widgetList);
				}
			});

			permissionChannelWidgetMap.put(permissions, tempMap);
		}));

		// Add slot link for packets. Now packets are able to sync
		// Slots using sync host as identifier
		forEachWidget(terminal::addWidgetSlotLink);
	}

	public void forEachWidget(Consumer<IChannelContainerWidget<?>> function) {
		// Iterate for each bi consumer with permissions, map pair in inner map, then iterate for each
		// Bi consumer with channel, list pair, than iterate for each widget and call {@code function}
		permissionChannelWidgetMap.forEach((perm, map) -> map.forEach((chan, list) -> list.forEach(function)));
	}

	public boolean hasCard() {
		return !cardSlot.getStack().isEmpty();
	}

	public ItemStack getCard() {
		return cardSlot.getStack();
	}

	public LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelContainerWidget<?>>>> getOuterMap() {
		return this.permissionChannelWidgetMap;
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		super.onContainerClosed(player);
		this.terminal.listeners.remove(this);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
		return ItemStack.EMPTY;
	}

	@Override
	public ISyncHost getSyncHost() {
		return terminal;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if (host instanceof TileMultiControllerTerminal) {
			this.terminal = (TileMultiControllerTerminal) host;
		}
	}
}
