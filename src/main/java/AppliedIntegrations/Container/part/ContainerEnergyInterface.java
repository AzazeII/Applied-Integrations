package AppliedIntegrations.Container.part;

import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Tile.TileEnergyInterface;
import AppliedIntegrations.Gui.Part.GuiEnergyInterface;

import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import appeng.api.util.AEPartLocation;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.Map;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;

/**
 * @Author Azazell
 */
public class ContainerEnergyInterface extends ContainerWithUpgradeSlots {
    // Number of upgrades
    private static int NUMBER_OF_UPGRADE_SLOTS = 1;

    // X of upgrades
    private static int UPGRADE_X_POS = 186;

    // Y of upgrades
    private static int UPGRADE_Y_POS = 8;

    public String realContainer;

    public boolean firstUpdate = true;

    public final IEnergyInterface EnergyInterface;
    public PartEnergyInterface part;
    private TileEnergyInterface tile;
    public int LinkedRFStorage;
    public EntityPlayer player;

    private ContainerFurnace cont;
    private GuiEnergyInterface linkedGUI;

    public final Map<LiquidAIEnergy, Integer> LinkedStorageMap = Maps.newHashMap();
    public final Map<AEPartLocation,Map<LiquidAIEnergy, Integer>> LinkedTileStorageMap = Maps.newHashMap();
    public ContainerEnergyInterface(final EntityPlayer player, final IEnergyInterface energyInterface) {
        super(energyInterface, player);
        this.EnergyInterface = energyInterface;
        this.player = player;
        super.bindPlayerInventory(player.inventory,149,207);

        // check if interface part or Tile?
        if (energyInterface instanceof PartEnergyInterface) {
            LinkedStorageMap.put(RF,LinkedRFStorage);
            for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS){
                LinkedTileStorageMap.put(side, LinkedStorageMap);
            }

            this.linkedGUI = new GuiEnergyInterface(this,this.EnergyInterface,player);
            PartEnergyInterface part = (PartEnergyInterface) this.EnergyInterface;
            // register listener
            part.addListener(this);

            this.part = (PartEnergyInterface)EnergyInterface;// add slots

            AIGridNodeInventory inventory = part.getUpgradeInventory();
            this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS,
                    UPGRADE_X_POS, UPGRADE_Y_POS);
        } else if (energyInterface instanceof TileEnergyInterface) {
            TileEnergyInterface tile = (TileEnergyInterface) this.EnergyInterface;
            this.tile = (TileEnergyInterface)this.EnergyInterface;
            tile.addListener(this);

            AIGridNodeInventory inventory = (AIGridNodeInventory)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null /* Internal facing ;) */);
            // add slots
            this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS,
                    UPGRADE_X_POS + 1, UPGRADE_Y_POS);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void onContainerClosed( @Nonnull final EntityPlayer player ) {
        super.onContainerClosed(player);
        if(part != null) {
            ((PartEnergyInterface) this.EnergyInterface).removeListener(this);
            this.realContainer = null;
            this.part.setRealContainer(realContainer);
        }
            //FlowMode = 0;
            //this.part.flowMode = Gui;
    }


    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber )
    {
        // Get the slot
        Slot slot = this.getSlotOrNull( slotNumber );

        // Do we have a valid slot with an item?
        if( ( slot != null ) && ( slot.getHasStack() ) )
        {
            // Pass to super
            return super.transferStackInSlot( player, slotNumber );
        }

        return null;
    }

    public boolean onStorageReceive(PartEnergyInterface sender) {
        if(sender.getX() == this.part.getX() && sender.getY() == this.part.getY() && this.part.getZ() == sender.getZ() && this.part.getSide() == sender.getSide()){
            return true;
        }
        return false;
    }

}


