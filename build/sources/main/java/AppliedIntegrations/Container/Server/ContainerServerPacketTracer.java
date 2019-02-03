package AppliedIntegrations.Container.Server;



import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Parts.AIPart;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

public class ContainerServerPacketTracer extends AIContainer {

    public TileServerCore tile;

    public ContainerServerPacketTracer(TileServerCore instance, EntityPlayer player) {
        super(player);
    }

    @Override
    public boolean onFilterReceive(AIPart part) {
        return false;
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
