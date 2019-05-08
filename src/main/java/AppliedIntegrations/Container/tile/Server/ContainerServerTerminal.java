package AppliedIntegrations.Container.tile.Server;



import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerServerTerminal extends ContainerWithPlayerInventory {

    public TileServerCore tile;

    public ContainerServerTerminal(TileServerCore instance, EntityPlayer player) {
        super(player);

        // Bind inventory of player
        super.bindPlayerInventory(player.inventory,149,207);

        // Write instance
        this.tile = instance;
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
