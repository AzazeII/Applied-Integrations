package AppliedIntegrations.tile;

import AppliedIntegrations.api.ISyncHost;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @Author Azazell
 */
public interface IAIMultiBlock extends ISyncHost {

    void tryConstruct(EntityPlayer player);

    boolean hasMaster();

    IMaster getMaster();

    void setMaster(IMaster tileServerCore);


    void notifyBlock();
}
