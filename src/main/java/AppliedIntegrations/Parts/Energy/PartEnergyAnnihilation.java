package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.Helpers.Energy.StackCapabilityHelper;
import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;

/**
 * @Author Azazell
 */
public class PartEnergyAnnihilation extends AIPlanePart {

    public PartEnergyAnnihilation() {
        super(PartEnum.EnergyAnnihilation, SecurityPermissions.INJECT);
    }

    @Override
    public IPartModel getStaticModels() {
        if(isPowered()){
            if(isActive()) {
                return PartModelEnum.ANNIHILATION_HAS_CHANNEL;
            }else {
                return PartModelEnum.ANNIHILATION_ON;
            }
        }
        return PartModelEnum.ANNIHILATION_OFF;
    }

    @Override
    protected void doWork(int ticksSinceLastCall) {
        // Iterate over all entities
        currentEntities.forEach((workingEntity) -> {
            // Check if entity is item
            if(workingEntity instanceof EntityItem){
                // Check if stack belongs to one of capabilities
                StackCapabilityHelper helper = new StackCapabilityHelper(((EntityItem) workingEntity).getItem());

                // Iterate over all energy types
                LiquidAIEnergy.energies.values().forEach((LiquidAIEnergy energy) -> {
                    // Check if stack has capability
                    if(helper.hasCapability(energy)){
                        // Simulate extraction
                        int extracted = helper.extractEnergy(energy, ENERGY_TRANSFER, SIMULATE);

                        // Simulate injection
                        int injected = InjectEnergy(new EnergyStack(energy, extracted), SIMULATE);

                        // Modulate injection
                        if(InjectEnergy(new EnergyStack(energy, helper.extractEnergy(energy, injected, MODULATE)), MODULATE) > 0) {
                            // Spawn lightning
                            super.spawnLightning(workingEntity);
                        }
                    }
                });
            }else if(workingEntity instanceof EntityPlayer){
                // Get player from working entity
                EntityPlayer player = (EntityPlayer)workingEntity;

                // Scan player's inventory
                player.inventory.mainInventory.iterator().forEachRemaining((ItemStack stack) ->{
                    // Check if stack belongs to one of capabilities
                    StackCapabilityHelper helper = new StackCapabilityHelper(stack);

                    // Iterate over all energy types
                    LiquidAIEnergy.energies.values().forEach((LiquidAIEnergy energy) -> {
                        // Check if stack has capability
                        if(helper.hasCapability(energy)){
                            // Simulate extraction
                            int extracted = helper.extractEnergy(energy, ENERGY_TRANSFER, SIMULATE);

                            // Simulate injection
                            int injected = InjectEnergy(new EnergyStack(energy, extracted), SIMULATE);

                            // Modulate injection
                            if(InjectEnergy(new EnergyStack(energy, helper.extractEnergy(energy, injected, MODULATE)), MODULATE) > 0) {
                                // Spawn lightning
                                super.spawnLightning(workingEntity);
                            }
                        }
                    });
                });
            }
        });
    }
}
