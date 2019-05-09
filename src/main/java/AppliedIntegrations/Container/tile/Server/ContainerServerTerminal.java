package AppliedIntegrations.Container.tile.Server;



import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerServerTerminal extends ContainerWithPlayerInventory {

    private final SlotRestrictive cardSlot;
    public TileServerCore tile;

    public ContainerServerTerminal(TileServerCore instance, TileServerSecurity terminal, EntityPlayer player) {
        super(player);

        // Bind inventory of player
        super.bindPlayerInventory(player.inventory,119,177);

        // Add network card editor slot
        super.addSlotToContainer(this.cardSlot = new SlotRestrictive(terminal.editorInv,0, 37, 86){
            // Override icon getter for this slot
            @SideOnly(Side.CLIENT)
            public String getSlotTexture() {
                return AppliedIntegrations.modid + ":gui/slots/network_card_slot";
            }
        });

        // Write instance
        this.tile = instance;
    }

    public boolean hasCard() {
        return !cardSlot.getStack().isEmpty();
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
