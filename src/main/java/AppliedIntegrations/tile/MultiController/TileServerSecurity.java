package AppliedIntegrations.tile.MultiController;


import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.api.Storage.IChannelWidget;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEColor;
import appeng.api.util.IOrientable;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Azazell
 */
public class TileServerSecurity extends AIServerMultiBlockTile implements IOrientable {
	// Used by both container and gui
	public static final int SLOT_Y = 18; // (1)

	public static final int SLOT_X = 9; // (2)

	public static final int SLOT_ROWS = 3; // (3)

	public static final int SLOT_COLUMNS = 9; // (4)

	public List<ContainerServerTerminal> listeners = new LinkedList<>();

	public AIGridNodeInventory editorInv = new AIGridNodeInventory("Network Card Editor", 1, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {

			return itemstack.getItem() instanceof NetworkCard;
		}
	};

	public boolean updateRequested;

	private EnumFacing forward = EnumFacing.UP;

	private List<IChannelWidget<?>> filterSlots = new LinkedList<>();

	public TileServerSecurity() {
		super();

		// Update proxy setting
		getProxy().setValidSides(EnumSet.allOf(EnumFacing.class));
		getProxy().getConnectableSides().remove(forward);
	}

	@Override
	protected EnumSet<EnumFacing> getValidSides() {
		// Constant sides
		return getProxy().getConnectableSides();
	}

	public void updateCardData(NBTTagCompound tag) {
		// Get inventory
		AIGridNodeInventory inv = this.editorInv;

		// Get stack
		ItemStack stack = inv.getStackInSlot(0);

		// Check if stack has network card item
		if (stack != null && stack.getItem() instanceof NetworkCard) {
			// Change NBT tag
			stack.setTagCompound(tag);
		}
	}

	public void rotateForward(EnumFacing facing) {
		// Get facing axis
		EnumFacing.Axis axis = facing.getAxis();

		// Rotate current direction around given axis
		forward.rotateAround(axis);
	}

	@Override
	public void createProxyNode() {
		// Configure proxy states
		this.getProxy().setColor(AEColor.TRANSPARENT); // (1) Color
		this.getProxy().setIdlePowerUsage(1); // (2) Power usage
		this.getProxy().onReady(); // (3) Make node ready
		this.getProxy().setFlags(); // (4) Flags
		this.getProxy().setValidSides(getValidSides()); // (5) Sides

		// Notify node
		this.getProxy().getNode().updateState();
	}

	@Override
	public void invalidate() {
		if (world != null && !world.isRemote) {
			destroyProxyNode();
		}

		if (hasMaster()) {
			// Remove this as slave from master
			((TileServerCore) getMaster()).slaves.remove(this);
		}

		// Drop items from editor inventory
		Platform.spawnDrops(world, pos, Arrays.asList(editorInv.slots));
	}

	@Override
	public void update() {
		super.update();

		// Check if tile has master
		if (!hasMaster()) {
			// Check not null
			if (getProxy().getNode() == null) {
				return;
			}

			try {
				// Get our grid
				IGrid grid = getProxy().getGrid();


				// Iterate for each node of this grid
				for (IGridNode node : grid.getNodes()) {
					// Check if node is server part
					if (node.getMachine() instanceof AIServerMultiBlockTile) {
						// Cast this node to server tile
						AIServerMultiBlockTile tile = (AIServerMultiBlockTile) node.getMachine();

						// Check if tile has master
						if (!tile.hasMaster())
						// Skip this tile
						{
							continue;
						}

						// Get tile master
						TileServerCore master = (TileServerCore) tile.getMaster();

						// Add this to slave list
						master.addSlave(this);

						// Set master
						setMaster(master);

						return;
					}
				}
			} catch (GridAccessException e) {
				e.printStackTrace();
			}
		}

		// Check if update requested
		if (updateRequested) {
			// Check if we have gui to update
			if (Minecraft.getMinecraft().currentScreen instanceof GuiServerTerminal) {
				// Init gui coordinate set
				initGuiCoordinates();
			}
		}
	}

	private void initGuiCoordinates() {
		// Iterate for each listener
		for (ContainerServerTerminal listener : listeners) {
			// Send update packet
			NetworkHandler.sendTo(new PacketCoordinateInit(this), (EntityPlayerMP) listener.player);

			// Trigger request
			updateRequested = false;
		}
	}

	@Override
	public void notifyBlock() {

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		// Read inventory
		editorInv.readFromNBT(tag.getTagList("#upgradeInventory", 10));

		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		// Write inventory
		tag.setTag("#upgradeInventory", editorInv.writeToNBT());

		return super.writeToNBT(tag);
	}

	@Override
	public boolean canBeRotated() {

		return true;
	}

	@Override
	public EnumFacing getForward() {

		return forward;
	}

	@Override
	public EnumFacing getUp() {

		return null;
	}

	@Override
	public void setOrientation(EnumFacing Forward, EnumFacing Up) {

	}

	// ------# Used by packets to sync slots by identifier #------ //
	public void addWidgetSlotLink(IChannelWidget<?> widget) {

		this.filterSlots.add(widget);
	}

	public void updateWidgetSlotLink(int x, int y, ItemStack stack) {
		// Iterate for each widget
		this.filterSlots.forEach((widget -> {
			// Check if widget is under mouse
			if (widget.isMouseOverWidget(x, y)) {
				// Update stack
				widget.setAEStack(AEItemStack.fromItemStack(stack));
			}
		}));
	}


	// ------# Used by packets to sync slots by identifier #------ //
}
