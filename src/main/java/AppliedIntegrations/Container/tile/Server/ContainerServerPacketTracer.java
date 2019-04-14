package AppliedIntegrations.Container.tile.Server;



import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerServerPacketTracer extends AIContainer {

    public TileServerCore tile;

    public ContainerServerPacketTracer(TileServerCore instance, EntityPlayer player) {
        super(player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
    @Override
    public void onContainerClosed( @Nonnull final EntityPlayer player )
    {
       super.onContainerClosed(player);
    }
}
