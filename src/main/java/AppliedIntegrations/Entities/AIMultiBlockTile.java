package AppliedIntegrations.Entities;

import AppliedIntegrations.Entities.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class AIMultiBlockTile extends AITile implements IAIMultiBlock {

    protected TileServerCore master;

    @Override
    public void tryConstruct(EntityPlayer p) {
        for (EnumFacing side : EnumFacing.values()) {
            if (world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2,
                    getPos().getZ() + side.getFrontOffsetZ() * 2)) instanceof TileServerCore) {
                TileServerCore tile = (TileServerCore) world.getTileEntity(
                        new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2, getPos().getZ() + side.getFrontOffsetZ() * 2));
                tile.tryConstruct(p);
                break;
            }
        }
    }
    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        if(hasMaster())
            return EnumSet.allOf(EnumFacing.class);
        return null;
    }

    @Override
    public void createAELink() {
        if (!world.isRemote && hasMaster()) {
            if (gridNode == null)
                gridNode = AEApi.instance().grid().createGridNode(this);
            gridNode.updateState();
        }
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.noneOf(GridFlags.class);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote) {
            destroyAELink();
        }
        if (hasMaster())
            master.DestroyMultiBlock();
    }

    @Override
    public void onChunkUnload() {
        if (world != null && !world.isRemote) {
            destroyAELink();
        }

    }
    @Override
    public boolean hasMaster() {
        return master!=null;
    }

    @Override
    public TileServerCore getMaster() {
        return master;
    }

    @Override
    public void setMaster(TileServerCore tileServerCore) {
        master = tileServerCore;
    }

    @Override
    public void notifyBlock(){

    }

}
