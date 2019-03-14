package AppliedIntegrations.tile;

import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.entity.player.EntityPlayer;

public interface IAIMultiBlock {

    void tryConstruct(EntityPlayer player);

    boolean hasMaster();

    IMaster getMaster();

    void setMaster(IMaster tileServerCore);


    void notifyBlock();
}
