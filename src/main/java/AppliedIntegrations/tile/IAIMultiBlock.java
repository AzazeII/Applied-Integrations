package AppliedIntegrations.tile;

import net.minecraft.entity.player.EntityPlayer;

/**
 * @Author Azazell
 */
public interface IAIMultiBlock {

    void tryConstruct(EntityPlayer player);

    boolean hasMaster();

    IMaster getMaster();

    void setMaster(IMaster tileServerCore);


    void notifyBlock();
}
