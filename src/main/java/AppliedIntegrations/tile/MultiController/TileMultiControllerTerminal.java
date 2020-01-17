package AppliedIntegrations.tile.MultiController;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerTerminal;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.AITile;
import appeng.api.AEApi;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.util.AEColor;
import appeng.api.util.IOrientable;
import appeng.util.Platform;
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
public class TileMultiControllerTerminal extends AITile implements IOrientable {
	public static final int SLOT_Y = 18;
	public static final int SLOT_X = 9;

	public static final int SLOT_ROWS = 3;
	public static final int SLOT_COLUMNS = 9;

	public List<ContainerMultiControllerTerminal> listeners = new LinkedList<>();

	public AIGridNodeInventory editorInv = new AIGridNodeInventory("Network Card Editor", 1, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return itemstack.getItem() instanceof NetworkCard;
		}
	};

	public boolean updateRequested;

	private EnumFacing forward = EnumFacing.UP;

	private List<IChannelWidget<?>> filterSlots = new LinkedList<>();

	public TileMultiControllerTerminal() {
		super();
	}

	public void updateCardData(NBTTagCompound tag) {
		AIGridNodeInventory inv = this.editorInv;
		ItemStack stack = inv.getStackInSlot(0);
		if (stack != null && stack.getItem() instanceof NetworkCard) {
			stack.setTagCompound(tag);
		}
	}

	public void rotateForward(EnumFacing facing) {
		EnumFacing.Axis axis = facing.getAxis();
		forward.rotateAround(axis);
	}

	@Override
	public void createProxyNode() {
		this.getProxy().setColor(AEColor.TRANSPARENT);
		this.getProxy().setIdlePowerUsage(1);
		this.getProxy().onReady();
		this.getProxy().setFlags();
		this.getProxy().setValidSides(EnumSet.allOf(EnumFacing.class));
		this.getProxy().getConnectableSides().remove(forward);
		this.getProxy().getNode().updateState();
	}

	@Override
	public void invalidate() {
		if (world != null && Platform.isServer()) {
			destroyProxyNode();
		}

		Platform.spawnDrops(world, pos, Arrays.asList(editorInv.slots));
	}

	private void initGuiCoordinates() {
		for (ContainerMultiControllerTerminal listener : listeners) {
			NetworkHandler.sendTo(new PacketCoordinateInit(this), (EntityPlayerMP) listener.player);
			updateRequested = false;
		}
	}

	@Override
	public void notifyBlock() {

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		editorInv.readFromNBT(tag.getTagList("#upgradeInventory", 10));
		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
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

	public void addWidgetSlotLink(IChannelWidget<?> widget) {

		this.filterSlots.add(widget);
	}

	public void updateWidgetSlotLink(int x, int y, ItemStack stack) {
		// Iterate for each widget
		this.filterSlots.forEach((widget -> {
			// Check if widget is under mouse
			if (widget.isMouseOverWidget(x, y)) {
				// Update stack
				widget.setAEStack(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(stack));
			}
		}));
	}
}
