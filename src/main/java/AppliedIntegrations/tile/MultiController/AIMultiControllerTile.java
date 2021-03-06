package AppliedIntegrations.tile.MultiController;


import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.MultiController.PacketMasterSync;
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
		for (int i = 1; i < 5; i++) {
			for (EnumFacing side : EnumFacing.values()) {
				TileEntity tile = world.getTileEntity(new BlockPos(
						getPos().getX() + side.getFrontOffsetX() * i,
						getPos().getY() + side.getFrontOffsetY() * i,
						getPos().getZ() + side.getFrontOffsetZ() * i));

				if (tile instanceof TileMultiControllerCore) {
					TileMultiControllerCore core = (TileMultiControllerCore) tile;
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
		this.master = (TileMultiControllerCore) tileServerCore;
	}

	@Override
	public void createProxyNode() {
		if (hasMaster()) {
			super.createProxyNode();

			this.getProxy().setFlags();
			this.getProxy().setValidSides(getValidSides());
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
		masterChangeHandler.onChange(master, (master) -> {
			NetworkHandler.sendToDimension(new PacketMasterSync(this, master), world.provider.getDimension());
		});
	}
}
