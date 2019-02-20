package AppliedIntegrations.tile;

import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.entity.player.EntityPlayer;

public interface IAIMultiBlock {

    void tryConstruct(EntityPlayer player);

    boolean hasMaster();

    TileServerCore getMaster();

    void setMaster(TileServerCore tileServerCore);


    void notifyBlock();
}
