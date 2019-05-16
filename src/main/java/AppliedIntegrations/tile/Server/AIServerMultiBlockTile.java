package AppliedIntegrations.tile.Server;


import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketMasterSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import appeng.api.AEApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

/**
 * @Author Azazell
 */
public abstract class AIServerMultiBlockTile extends AITile implements IAIMultiBlock {
	protected TileServerCore master;

	private ChangeHandler<TileServerCore> masterChangeHandler = new ChangeHandler<>();

	public AIServerMultiBlockTile(){
		super();

		// Change proxy settings
		this.getProxy().setFlags();
		this.getProxy().setValidSides(getValidSides());
	}

	protected abstract EnumSet<EnumFacing> getValidSides();

	@Override
	public void tryConstruct(EntityPlayer p) {
		for (EnumFacing side : EnumFacing.values()) {
			// Check if tile from two block from this block to direction of side
			if (world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2, getPos().getZ() + side.getFrontOffsetZ() * 2)) instanceof TileServerCore) {
				// Get tile
				TileServerCore tile = (TileServerCore) world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2, getPos().getZ() + side.getFrontOffsetZ() * 2));

				// Check not null
				if (tile != null) {
					// Pass call to core
					tile.tryConstruct(p);
				}
				break;
			}
		}
	}

	@Override
	public boolean hasMaster() {
		return master != null;
	}

	@Override
	public IMaster getMaster() {
		return master;
	}

	@Override
	public void setMaster(IMaster tileServerCore) {
		// Update master
		master = (TileServerCore) tileServerCore;

		// Update proxy settings
		this.getProxy().setValidSides(getValidSides());
	}

	@Override
	public void createAENode() {
		// Run code only on server and check if tile has master
		if (!world.isRemote && hasMaster()) {
			// Check if node is null
			if (gridNode == null) {
				// Initialized node
				gridNode = AEApi.instance().grid().createGridNode(getProxy());
			}

			// Update node status
			gridNode.updateState();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (hasMaster()) {
			master.destroyMultiBlock();
		}
	}

	@Override
	public void update() {
		super.update();

		// Call master change handler
		masterChangeHandler.onChange(master, (master) -> {
			// Notify server
			NetworkHandler.sendToDimension(new PacketMasterSync(this, master), world.provider.getDimension());
		});
	}

	@Override
	public void notifyBlock() {

	}
}
