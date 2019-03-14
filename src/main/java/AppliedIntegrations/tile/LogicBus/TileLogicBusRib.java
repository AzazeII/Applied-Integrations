package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileLogicBusRib extends TileLogicBusSlave implements IAIMultiBlock {
    private boolean hasMaster;

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
    @Override
    public void notifyBlock(){

    }
}
