package AppliedIntegrations.Entities.LogicBus;

import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileLogicBusRib extends TileEntity implements IAIMultiBlock {
    private boolean hasMaster;
    @Override
    public void tryConstruct(EntityPlayer p) {

    }

    @Override
    public boolean hasMaster() {
        return false;
    }

    @Override
    public TileServerCore getMaster() {
        return null;
    }

    @Override
    public void setMaster(TileServerCore tileServerCore) {

    }
    @Override
    public void notifyBlock(){

    }
}
