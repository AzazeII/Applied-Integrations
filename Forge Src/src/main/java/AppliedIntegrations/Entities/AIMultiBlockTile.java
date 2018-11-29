package AppliedIntegrations.Entities;

import AppliedIntegrations.Entities.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class AIMultiBlockTile extends AITile implements IAIMultiBlock {

    protected TileServerCore master;

    @Override
    public void tryConstruct(EntityPlayer p) {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (worldObj.getTileEntity(xCoord + side.offsetX * 2, yCoord + side.offsetY * 2, zCoord + side.offsetZ * 2) instanceof TileServerCore) {
                TileServerCore tile = (TileServerCore) worldObj.getTileEntity(xCoord + side.offsetX * 2, yCoord + side.offsetY * 2, zCoord + side.offsetZ * 2);
                tile.tryConstruct(p);
                break;
            }
        }
    }
    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public void createAELink() {
        if (!worldObj.isRemote && hasMaster()) {
            if (theGridNode == null)
                theGridNode = AEApi.instance().createGridNode(this);
            theGridNode.updateState();
        }
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.noneOf(GridFlags.class);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (worldObj != null && !worldObj.isRemote) {
            destroyAELink();
        }
        if (hasMaster())
            master.DestroyMultiBlock();
    }

    @Override
    public void onChunkUnload() {
        if (worldObj != null && !worldObj.isRemote) {
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
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
}
