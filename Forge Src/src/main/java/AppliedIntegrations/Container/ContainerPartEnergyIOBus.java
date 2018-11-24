package AppliedIntegrations.Container;

import AppliedIntegrations.API.LiquidAIEnergy;

import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.Parts.AIOPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerPartEnergyIOBus extends ContainerWithNetworkTool
{
    /**
     * The number of upgrade slots we have
     */
    private static int NUMBER_OF_UPGRADE_SLOTS = 4;

    /**
     * The x position of the upgrade slots
     */
    private static int UPGRADE_X_POS = 187;

    /**
     * The Y position for the upgrade slots
     */
    private static int UPGRADE_Y_POS = 8;

    private final AIOPart part;
    public ContainerPartEnergyIOBus(final AIOPart part, final EntityPlayer player )
    {
        super(part,player);
        // Set the part
        this.part = part;

        this.addUpgradeSlots( part.getUpgradeInventory(), this.NUMBER_OF_UPGRADE_SLOTS,
                this.UPGRADE_X_POS, this.UPGRADE_Y_POS );

        // Bind to the player's inventory
        this.bindPlayerInventory(player.inventory);

        this.part.addListener(this);
    }
    protected void bindPlayerInventory(IInventory inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                        8 + j * 18, (i * 18 + 149)-47));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, (207)-47));// 173
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
    public void setFilterSize( final byte filterSize )
    {

    }
    public void setFilteredEnergy( final List<LiquidAIEnergy> filteredEnergies )
    {
    }
    @Override
    public ItemStack transferStackInSlot( final EntityPlayer player, final int slotNumber )
    {
        // Get the slot
        Slot slot = this.getSlotOrNull( slotNumber );

        // Do we have a valid slot with an item?
        if( ( slot != null ) && ( slot.getHasStack() ) )
        {
            if( ( this.part != null ) && ( this.part.addFilteredEnergyFromItemstack( player, slot.getStack() ) ) )
            {
                return null;
            }

            // Pass to super
            return super.transferStackInSlot( player, slotNumber );
        }

        return null;
    }



    @Override
    public boolean onFilterReceive(AIPart part) {
        return part == this.part;

    }
}
