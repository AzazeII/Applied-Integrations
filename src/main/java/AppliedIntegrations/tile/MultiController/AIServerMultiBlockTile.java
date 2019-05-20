package AppliedIntegrations.tile.MultiController;


import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketMasterSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
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
		this.master = (TileServerCore) tileServerCore;
	}

	@Override
	public void createProxyNode() {
		// Configure proxy only if host has master
		if (hasMaster()) {
			// Configure parent
			super.createProxyNode();

			// Change proxy settings
			this.getProxy().setFlags(); // (1) Flags
			this.getProxy().setValidSides(getValidSides()); // (2) Sides

			// Notify node
			this.getProxy().getNode().updateState();
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
	public void notifyBlock() {}
}
