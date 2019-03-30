package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.StackCapabilityHelper;
import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartModel;
import appeng.client.EffectType;
import appeng.core.AppEng;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;

public class PartEnergyFormation extends AIPlanePart {
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
    protected void doWork(int ticksSinceLastCall) {
        // Iterate over all entities
        currentEntities.forEach((workingEntity) -> {
            // Check if entity is item
            if (workingEntity instanceof EntityItem) {
                // Check if stack belongs to one of capabilities
                StackCapabilityHelper helper = new StackCapabilityHelper(((EntityItem) workingEntity).getItem());

                // Iterate over all energy types
                LiquidAIEnergy.energies.values().forEach((LiquidAIEnergy energy) -> {
                    // Check if stack has capability
                    if (helper.hasCapability(energy)) {
                        // Simulate extraction
                        int injected = helper.injectEnergy(energy, ENERGY_TRANSFER, SIMULATE);

                        // Simulate injection
                        int extracted = ExtractEnergy(new EnergyStack(energy, injected), SIMULATE);

                        // Modulate injection
                        if (ExtractEnergy(new EnergyStack(energy, helper.injectEnergy(energy, extracted, MODULATE)), MODULATE) > 0) {
                            // Spawn lightning
                            super.spawnLightning(workingEntity);
                        }
                    }
                });
            } else if (workingEntity instanceof EntityPlayer) {
                // Get player from working entity
                EntityPlayer player = (EntityPlayer) workingEntity;

                // Scan player's inventory
                player.inventory.mainInventory.iterator().forEachRemaining((ItemStack stack) -> {
                    // Check if stack belongs to one of capabilities
                    StackCapabilityHelper helper = new StackCapabilityHelper(stack);

                    // Iterate over all energy types
                    LiquidAIEnergy.energies.values().forEach((LiquidAIEnergy energy) -> {
                        // Check if stack has capability
                        if (helper.hasCapability(energy)) {
                            // Simulate extraction
                            int injected = helper.injectEnergy(energy, ENERGY_TRANSFER, SIMULATE);

                            // Simulate injection
                            int extracted = ExtractEnergy(new EnergyStack(energy, injected), SIMULATE);

                            // Modulate injection
                            if (ExtractEnergy(new EnergyStack(energy, helper.injectEnergy(energy, extracted, MODULATE)), MODULATE) > 0) {
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
