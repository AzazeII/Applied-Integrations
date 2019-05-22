package AppliedIntegrations.tile.MultiController;


import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketMasterSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

/**
 * @Author Azazell
 */
public abstract class AIMultiControllerTile extends AITile implements IAIMultiBlock {
	protected TileMultiControllerCore master;

	private ChangeHandler<TileMultiControllerCore> masterChangeHandler = new ChangeHandler<>();

	@Override
	public void tryConstruct(EntityPlayer p) {
		// Iterate until i = 5
		for (int i = 1; i < 5; i++) {
			// Iterate for each side
			for (EnumFacing side : EnumFacing.values()) {
				// Get tile with i blocks offset to side
				TileEntity tile = world.getTileEntity(new BlockPos(
						getPos().getX() + side.getFrontOffsetX() * i,
						getPos().getY() + side.getFrontOffsetY() * i,
						getPos().getZ() + side.getFrontOffsetZ() * i));

				// Check if tile is core
				if (tile instanceof TileMultiControllerCore) {
					// Cast tile
					TileMultiControllerCore core = (TileMultiControllerCore) tile;

					// Pass call to core
					core.tryConstruct(p);

					break;
				}
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
		this.master = (TileMultiControllerCore) tileServerCore;
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

	protected abstract EnumSet<EnumFacing> getValidSides();

	@Override
	public void notifyBlock() {
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
}
