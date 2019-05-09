package AppliedIntegrations.Container.tile.Server;



import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerServerTerminal extends ContainerWithPlayerInventory {

    public TileServerCore tile;

    public ContainerServerTerminal(TileServerCore instance, TileServerSecurity terminal, EntityPlayer player) {
        super(player);

        // Bind inventory of player
        super.bindPlayerInventory(player.inventory,119,177);

        // Add network card editor slot
        super.addSlotToContainer(new SlotRestrictive(terminal.editorInv, 0, 42, 119 + 66));

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
