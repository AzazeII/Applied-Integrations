package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileLogicBusCore extends TileEntity implements IMaster, IAIMultiBlock {
    private boolean hasMaster;

    @Override
    public void tryConstruct(EntityPlayer p) {

    }

    @Override
    public void notifyBlock(){ }
    @Override
    public boolean hasMaster() {
        return true;
    }

    @Override
    public IMaster getMaster() {
        return this;
    }

    @Override
    public void setMaster(IMaster tileServerCore) {

    }
}
