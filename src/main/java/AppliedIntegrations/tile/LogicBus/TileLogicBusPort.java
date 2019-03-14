package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.networking.*;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.IAEMultiBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.util.*;

public class TileLogicBusPort extends TileLogicBusSlave implements IAEMultiBlock, IAIMultiBlock {
    private boolean hasMaster;

    private boolean isCorner;

    public TileLogicBusPort(){

    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL,GridFlags.CANNOT_CARRY);
    }
    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote) {
            destroyAELink();
        }
    }
    @Override
    public void onChunkUnload() {
        if (world != null && !world.isRemote) {
            destroyAELink();
        }

    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        if(hasMaster())
            return EnumSet.of(EnumFacing.SOUTH,EnumFacing.DOWN,EnumFacing.EAST,EnumFacing.UP,EnumFacing.NORTH,EnumFacing.WEST);
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public void notifyBlock(){

    }
    @Override
    public void disconnect(boolean b) {

    }

    @Override
    public IAECluster getCluster() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void tryConstruct(EntityPlayer p) {

    }

    @Override
    public boolean hasMaster() {
        return false;
    }

    @Override
    public IMaster getMaster() {
        return null;
    }

    @Override
    public void setMaster(IMaster tileServerCore) {

    }
}
