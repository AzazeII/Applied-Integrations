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
		// Consumes energy inside stack and puts into storage channel
		StackCapabilityHelper helper = new StackCapabilityHelper(stack);

		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (helper.hasCapability(energy)) {
				int extracted = helper.extractEnergy(energy, ENERGY_TRANSFER, SIMULATE);

				int injected = injectEnergy(new EnergyStack(energy, extracted), SIMULATE);
				if (injectEnergy(new EnergyStack(energy, helper.extractEnergy(energy, injected, MODULATE)), MODULATE) > 0) {
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
		currentEntities.forEach((workingEntity) -> {
			if (workingEntity instanceof EntityItem) {
				voidStack(((EntityItem) workingEntity).getItem(), workingEntity);
			} else if (workingEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) workingEntity;

				player.inventory.mainInventory.iterator().forEachRemaining(stack -> voidStack(stack, workingEntity));
			}
		});
	}
}
