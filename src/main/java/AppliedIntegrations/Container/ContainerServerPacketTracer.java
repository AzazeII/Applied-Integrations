package AppliedIntegrations.Container;

import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.Entities.Server.TileServerSecurity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class ContainerServerPacketTracer extends AIContainer{

    public TileServerSecurity tile;

    public ContainerServerPacketTracer(TileServerSecurity instance,EntityPlayer player) {
        super(player);

        tile = instance;
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
       tile.Listeners.remove(this);
    }
}
