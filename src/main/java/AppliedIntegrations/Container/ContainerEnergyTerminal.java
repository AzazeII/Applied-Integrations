package AppliedIntegrations.Container;

import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class ContainerEnergyTerminal extends ContainerWithPlayerInventory implements IEnergySelectorContainer
{
    private PartEnergyTerminal terminal;
    public EntityPlayer player;

    private static int OUTPUT_INV_INDEX = 1, INPUT_INV_INDEX = 0;

    /**
     * X position for the output slot
     */
    private static final int OUTPUT_POSITION_X = 26;

    /**
     * Y position for the output slot
     */
    private static final int OUTPUT_POSITION_Y = 92;

    /**
     * X position for the input slot
     */
    private static final int INPUT_POSITION_X = 8;

    /**
     * Y position for the input slot
     */
    private static final int INPUT_POSITION_Y = OUTPUT_POSITION_Y;
    
    private AIGridNodeInventory privateInventory = new AIGridNodeInventory( AppliedIntegrations.modid + ".item.energy.cell.inventory", 2, 64 )
    {
        @Override
        public boolean isItemValidForSlot( final int slotID, final ItemStack itemStack )
        {
            return Utils.getEnergyFromItemStack(itemStack) != null;
        }
    };
    
    public ContainerEnergyTerminal(EntityPlayer player, PartEnergyTerminal terminal) {
        super(player);
        this.bindPlayerInventory(player.inventory,122,180);
        this.terminal = terminal;
        this.player = player;

        this.addSlotToContainer( new SlotRestrictive( privateInventory, INPUT_INV_INDEX,
                INPUT_POSITION_X, INPUT_POSITION_Y ));

        this.addSlotToContainer(new SlotFurnaceOutput( this.player, privateInventory, OUTPUT_INV_INDEX,
                OUTPUT_POSITION_X, OUTPUT_POSITION_Y));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void setSelectedEnergy(LiquidAIEnergy _energy) {

    }



    @Override
    public boolean onFilterReceive(AIPart part) {
        return false;
    }
}

