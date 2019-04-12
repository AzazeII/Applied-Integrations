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

    public final IEnergyInterface EnergyInterface;
    public PartEnergyInterface part;

    public EntityPlayer player;


    public ContainerEnergyInterface(final EntityPlayer player, final IEnergyInterface energyInterface) {
        super(energyInterface, player);

        // Set interface
        this.EnergyInterface = energyInterface;

        // Set player
        this.player = player;

        // Bind player's inventory
        super.bindPlayerInventory(player.inventory,149,207);

        // check if interface part or Tile?
        if (energyInterface instanceof PartEnergyInterface) {
            // Get part
            PartEnergyInterface part = (PartEnergyInterface) this.EnergyInterface;

            // register listener
            part.addListener(this);

            // Assign part
            this.part = (PartEnergyInterface)EnergyInterface;// add slots

            // Get upgrade inventory
            AIGridNodeInventory inventory = part.getUpgradeInventory();

            // Add slots to inventory
            this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS,
                    UPGRADE_X_POS, UPGRADE_Y_POS);
        } else if (energyInterface instanceof TileEnergyInterface) {
            // Get tile from interface
            TileEnergyInterface tile = (TileEnergyInterface) this.EnergyInterface;

            // register listener
            tile.addListener(this);

            // Get upgrade inventory
            AIGridNodeInventory inventory = (AIGridNodeInventory)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null /* Internal facing ;) */);

            // add slots
            this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS,
                    UPGRADE_X_POS, UPGRADE_Y_POS);
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
    }
}


