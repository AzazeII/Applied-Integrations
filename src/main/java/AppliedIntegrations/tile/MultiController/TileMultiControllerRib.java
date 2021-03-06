package AppliedIntegrations.tile.MultiController;


import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.MultiController.PacketRibSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.EnumSet;


/**
 * @Author Azazell
 */
public class TileMultiControllerRib extends AIMultiControllerTile implements IAIMultiBlock, ITickable {
	public boolean isActive;
	private ChangeHandler<Boolean> activityChangeHandler = new ChangeHandler<>();

	public IGrid getMainNetwork() {
		if (getGridNode() == null) {
			return null;
		}

		return getNetwork();
	}

	private void notifyListeners() {
		NetworkHandler.sendToDimension(new PacketRibSync(this, getGridNode().isActive()), world.provider.getDimension());
	}

	@Override
	public void createProxyNode() {
		if (hasMaster()) {
			super.createProxyNode();
			this.getProxy().setFlags(GridFlags.DENSE_CAPACITY);
			this.getProxy().getNode().updateState();
		}
	}

	@Override
	protected EnumSet<EnumFacing> getValidSides() {
		return EnumSet.allOf(EnumFacing.class);
	}

	@Override
	public void update() {
		super.update();

		if (getGridNode() != null) {
			activityChangeHandler.onChange(getGridNode().isActive(), (activity -> {
				notifyListeners();
			}));
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && hasMaster()) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		// Check if capability is item handler capability and rib has master
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && hasMaster())
		{
			// Wrapper for card inventory of server core. Now subnetwork(ad-hoc network) with storage bus and
			// ME terminal can access card storage of server core
			return (T) new InvWrapper(((TileMultiControllerCore) getMaster()).cardInv);
		}
		return super.getCapability(capability, facing);
	}
}
