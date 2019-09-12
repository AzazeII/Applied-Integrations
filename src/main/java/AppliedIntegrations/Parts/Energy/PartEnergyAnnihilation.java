package AppliedIntegrations.Parts.Energy;


import AppliedIntegrations.Helpers.Energy.StackCapabilityHelper;
import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.parts.IPartModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;

/**
 * @Author Azazell
 */
public class PartEnergyAnnihilation extends AIPlanePart {
	public PartEnergyAnnihilation() {

		super(PartEnum.EnergyAnnihilation);
	}

	private void voidStack(ItemStack stack, Entity workingEntity) {
		// Check if stack belongs to one of capabilities
		StackCapabilityHelper helper = new StackCapabilityHelper(stack);

		// Iterate over all energy types
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Check if stack has capability
			if (helper.hasCapability(energy)) {
				// Simulate extraction
				int extracted = helper.extractEnergy(energy, ENERGY_TRANSFER, SIMULATE);

				// Simulate injection
				int injected = injectEnergy(new EnergyStack(energy, extracted), SIMULATE);

				// Modulate injection
				if (injectEnergy(new EnergyStack(energy, helper.extractEnergy(energy, injected, MODULATE)), MODULATE) > 0) {
					// Spawn lightning
					super.spawnLightning(workingEntity);
				}
			}
		}
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (isPowered()) {
			if (isActive()) {
				return PartModelEnum.ANNIHILATION_HAS_CHANNEL;
			} else {
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
			if (workingEntity instanceof EntityItem) {
				// Check if stack belongs to one of capabilities
				voidStack(((EntityItem) workingEntity).getItem(), workingEntity);
			} else if (workingEntity instanceof EntityPlayer) {
				// Get player from working entity
				EntityPlayer player = (EntityPlayer) workingEntity;

				// Scan player's inventory
				player.inventory.mainInventory.iterator().forEachRemaining(stack -> voidStack(stack, workingEntity));
			}
		});
	}
}
