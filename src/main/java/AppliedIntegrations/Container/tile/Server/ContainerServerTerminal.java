package AppliedIntegrations.Container.tile.Server;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.MultiController.TileServerCore;
import AppliedIntegrations.tile.MultiController.TileServerSecurity;
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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static AppliedIntegrations.tile.MultiController.TileServerSecurity.*;

/**
 * @Author Azazell
 */
public class ContainerServerTerminal extends ContainerWithPlayerInventory {

	public final TileServerCore core;

	private final SlotRestrictive cardSlot;

	private final TileServerSecurity terminal;

	private LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelContainerWidget<?>>>> permissionChannelWidgetMap = new LinkedHashMap<>();

	public ContainerServerTerminal(TileServerCore instance, TileServerSecurity terminal, EntityPlayer player) {

		super(player);

		// Bind inventory of player
		super.bindPlayerInventory(player.inventory, 119, 177);

		// Add network card editor slot
		super.addSlotToContainer(this.cardSlot = new SlotRestrictive(terminal.editorInv, 0, 37, 86) {
			// Override icon getter for this slot
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {

				return AppliedIntegrations.modid + ":gui/slots/network_card_slot";
			}
		});

		// Add widgets
		this.initWidgets(terminal);

		// Write instance
		this.core = instance;

		// Write terminal
		this.terminal = terminal;

		// Add listener
		this.terminal.listeners.add(this);
	}

	public void initWidgets(TileServerSecurity terminal) {
		// ************# Add Filter Slots #************ //
		// Iterate for each security permission
		GuiSecurityPermissionsButton.getPermissionList().forEach((permissions -> {
			// Temp map
			LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelContainerWidget<?>>> tempMap = new LinkedHashMap<>();

			// Iterate for each storage channel is list
			GuiStorageChannelButton.getChannelList().forEach((chan) -> {
				// Get widget from API
				Constructor<? extends IChannelWidget> channelWidgetConstructor = Objects.requireNonNull(AIApi.instance()).getWidgetFromChannel(chan);

				// Check not null
				if (channelWidgetConstructor != null) {
					// List of widget for this channel
					List<IChannelContainerWidget<?>> widgetList = new LinkedList<>();

					// Iterate for each row as X
					for (int x = 0; x < SLOT_COLUMNS; x++) {
						// Iterate for each column as Y
						for (int y = 0; y < SLOT_ROWS; y++) {
							try {
								// Try to construct with item slot constructor:
								// I.E: public WidgetItemSlot(IWidgetHost host, int x, int y)
								IChannelWidget widget = channelWidgetConstructor.newInstance(SLOT_X + 18 * x, SLOT_Y + 18 * y);

								// Check if widget is slot
								if (!(widget instanceof IChannelContainerWidget)) {
									continue;
								}

								// Add to widget list
								widgetList.add((IChannelContainerWidget<?>) widget);

								// Add to slot list
								addSlotToContainer(((IChannelContainerWidget<?>) widget).getSlotWrapper());
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

		// Iterate for each widget
		// Add slot link for packets. Now packets are able to sync
		// Slots using sync host as identifier
		forEachWidget(terminal::addWidgetSlotLink);
		// ************# Add Filter Slots #************ //
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

		// Remove listener
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
}
