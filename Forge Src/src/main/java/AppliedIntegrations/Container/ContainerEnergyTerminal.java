package AppliedIntegrations.Container;

import AppliedIntegrations.API.Grid.ICraftingIssuerHost;
import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.LiquidAIEnergy;

import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Gui.SortMode;
import AppliedIntegrations.Parts.PartEnergyTerminal;
import AppliedIntegrations.Utils.EffectiveSide;
import appeng.api.AEApi;
import appeng.api.config.ViewItems;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.util.IConfigManagerHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

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
    
    private AIInternalInventory privateInventory = new AIInternalInventory( AppliedIntegrations.modid + ".item.energy.cell.inventory", 2, 64 )
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

        this.addSlotToContainer(new SlotFurnace( this.player, privateInventory, OUTPUT_INV_INDEX,
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

