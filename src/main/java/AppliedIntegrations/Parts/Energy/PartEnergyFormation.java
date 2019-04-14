package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.*;
import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.util.item.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static appeng.api.config.Actionable.SIMULATE;
import static java.util.Collections.singletonList;

/**
 * @Author Azazell
 */
public class PartEnergyFormation extends AIPlanePart implements ICellContainer {
    public PartEnergyFormation() {
        super(PartEnum.EnergyFormation, SecurityPermissions.EXTRACT);
    }

    @Override
    public IPartModel getStaticModels() {
        if(isPowered()){
            if(isActive()) {
                return PartModelEnum.FORMATION_HAS_CHANNEL;
            }else {
                return PartModelEnum.FORMATION_ON;
            }
        }
        return PartModelEnum.FORMATION_OFF;
    }

    @Override
    protected void doWork(int ticksSinceLastCall) { }

    @Override
    public void blinkCell(int slot) {
        // Ignored (this part not operating any cells)
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
        // Check if channel present working channel, and handler not null
        if (channel != this.getChannel() || currentEntities.isEmpty())
            return new LinkedList<>();

        // Return only one handler for tile
        return singletonList(new IMEInventoryHandler() {
            @Override
            public AccessRestriction getAccess() {
                return AccessRestriction.WRITE;
            }

            @Override
            public boolean isPrioritized(IAEStack input) {
                // TODO: 2019-03-31 Priority
                return false;
            }

            @Override
            public boolean canAccept(IAEStack input) {
                return input instanceof IAEEnergyStack;
            }

            @Override
            public int getPriority() {
                // TODO: 2019-03-31 Priority
                return 0;
            }

            @Override
            public int getSlot() {
                // Ignored
                return 0;
            }

            @Override
            public boolean validForPass(int i) {
                return true;
            }

            @Override
            public IAEStack injectItems(IAEStack input, Actionable type, IActionSource src) {
                // Check not null
                if(input == null)
                    return input;

                // Check has list
                if(currentEntities.isEmpty())
                    return input;

                // Check smaller or equal to Integer.MAX_VALUE
                if(input.getStackSize() <= Integer.MAX_VALUE)
                    return input;

                // Summary injected
                int amountInjected = 0;

                // input in integer form
                int request = (int)input.getStackSize();

                // Iterate over all entities
                for (Entity workingEntity : currentEntities) {// Check if entity is item
                    if (workingEntity instanceof EntityItem) {
                        // Check if stack belongs to one of capabilities
                        StackCapabilityHelper helper = new StackCapabilityHelper(((EntityItem) workingEntity).getItem());

                        // Iterate over all energy types
                        for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
                            // Check if stack has capability
                            if (helper.hasCapability(energy)) {
                                // Simulate extraction
                                int injected = helper.injectEnergy(energy, request, SIMULATE);

                                // Modulate injection
                                if (injected > 0) {
                                    // Spawn lightning
                                    spawnLightning(workingEntity);
                                }

                                // Add to summary
                                amountInjected += injected;
                            }
                        }
                    } else if (workingEntity instanceof EntityPlayer) {
                        // Get player from working entity
                        EntityPlayer player = (EntityPlayer) workingEntity;

                        // Scan player's inventory
                        for(ItemStack stack : player.inventory.mainInventory) {
                            // Check if stack belongs to one of capabilities
                            StackCapabilityHelper helper = new StackCapabilityHelper(stack);

                            // Iterate over all energy types
                            for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
                                // Check if stack has capability
                                if (helper.hasCapability(energy)) {
                                    // Simulate extraction
                                    int injected = helper.injectEnergy(energy, request, SIMULATE);

                                    // Modulate injection
                                    if (injected > 0) {
                                        // Spawn lightning
                                        spawnLightning(workingEntity);
                                    }

                                    // Add to summary
                                    amountInjected += injected;
                                }
                            }
                        }
                    }
                }

                // Check if all energy was injected
                if(amountInjected == input.getStackSize()) {
                    // Return null, as everything was injected
                    return null;
                }else{
                    // Return input stack size - all amount injected
                    return input.copy().setStackSize(input.getStackSize() - amountInjected);
                }
            }

            @Override
            public IAEStack extractItems(IAEStack request, Actionable mode, IActionSource src) {
                // No items can be extracted
                return null;
            }

            @Override
            public IItemList getAvailableItems(IItemList out) {
                // Return empty item list
                return new ItemList();
            }

            @Override
            public IStorageChannel getChannel() {
                return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
            }
        });
    }

    @Override
    public int getPriority() {
        // TODO: 2019-03-31 Add priority gui tab
        return 0;
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
        // Check if inventory not null
        if (iCellInventory != null)
            // Persist inventory
            iCellInventory.persist();
        // Mark dirty
        getHostTile().getWorld().markChunkDirty(getHostTile().getPos(), getHostTile());
    }
}
