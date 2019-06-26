package AppliedIntegrations.Helpers.Energy;


import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.IEnergizedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux", striprefs = true), @Optional.Interface(iface = "mekanism.api.energy.IEnergizedItem", modid = "mekanism", striprefs = true), @Optional.Interface(iface = "ic2.api.item.IElectricItem", modid = "ic2", striprefs = true)})

/**
 * @Author Azazell
 */
public class StackCapabilityHelper {

	private ItemStack operatedStack;

	public StackCapabilityHelper(ItemStack stack) {
		// Set operated stack
		operatedStack = stack;
	}

	public boolean hasCapability(LiquidAIEnergy energy) {
		// Get item
		Item item = operatedStack.getItem();

		if (!IntegrationsHelper.instance.isLoaded(energy, true)) {
			return false;
		}

		if (energy == RF && item instanceof IEnergyContainerItem) {
			return true;
		}

		if (energy == EU && item instanceof IElectricItem) {
			return true;
		}
		if (energy == J && item instanceof IEnergyContainerItem) {
			return true;
		}
		return false;
	}

	/**
	 * Extract energy from operated stack
	 *
	 * @param energy         type
	 * @param energyTransfer energy to extract
	 * @param action
	 * @return How many energy was extracted
	 */
	public int extractEnergy(LiquidAIEnergy energy, int energyTransfer, Actionable action) {
		// Get item
		Item item = operatedStack.getItem();

		if (!IntegrationsHelper.instance.isLoaded(energy, true)) {
			return 0;
		}

		// RF Capability
		if (item instanceof IEnergyContainerItem && energy == RF) {
			IEnergyContainerItem rfContainer = (IEnergyContainerItem) item;

			return rfContainer.extractEnergy(operatedStack, energyTransfer, action == Actionable.SIMULATE);
		}

		// EU Capability
		if (item instanceof IElectricItem && energy == EU) {
			IElectricItem euContainer = (IElectricItem) item;

			;
		}

		// Joule Capability
		if (item instanceof IEnergizedItem && energy == J) {
			IEnergizedItem jouleContainer = (IEnergizedItem) item;

			int before = (int) jouleContainer.getEnergy(operatedStack);

			jouleContainer.setEnergy(operatedStack, jouleContainer.getEnergy(operatedStack) + energyTransfer);

			int current = (int) jouleContainer.getEnergy(operatedStack);

			if (action == Actionable.SIMULATE) {
				jouleContainer.setEnergy(operatedStack, before);
			}

			return (int) jouleContainer.getEnergy(operatedStack) - current;
		}

		// Nothing extracted
		return 0;
	}

	public int injectEnergy(LiquidAIEnergy energy, int energyTransfer, Actionable action) {
		// Get item
		Item item = operatedStack.getItem();

		if (!IntegrationsHelper.instance.isLoaded(energy, true)) {
			return 0;
		}

		// RF Capability
		if (item instanceof IEnergyContainerItem && energy == RF) {
			IEnergyContainerItem rfContainer = (IEnergyContainerItem) item;

			return rfContainer.receiveEnergy(operatedStack, energyTransfer, action == Actionable.SIMULATE);
		}

		// EU Capability
		if (item instanceof IElectricItem && energy == EU) {
			IElectricItem euContainer = (IElectricItem) item;

			;
		}

		// Joule Capability
		if (item instanceof IEnergizedItem && energy == J) {
			IEnergizedItem jouleContainer = (IEnergizedItem) item;

			int before = (int) jouleContainer.getEnergy(operatedStack);

			jouleContainer.setEnergy(operatedStack, jouleContainer.getEnergy(operatedStack) - energyTransfer);

			int current = (int) jouleContainer.getEnergy(operatedStack);

			if (action == Actionable.SIMULATE) {
				jouleContainer.setEnergy(operatedStack, before);
			}

			return (int) jouleContainer.getEnergy(operatedStack) - current;
		}

		// Nothing injected
		return 0;
	}
}
