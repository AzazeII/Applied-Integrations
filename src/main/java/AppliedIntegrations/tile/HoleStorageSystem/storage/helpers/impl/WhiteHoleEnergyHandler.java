package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;


import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class WhiteHoleEnergyHandler extends WhiteHoleSingularityInventoryHandler<IAEEnergyStack> {
	@Override
	public IAEEnergyStack extractItems(IAEEnergyStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		// Check if there is singularity
		if (singularity != null) {
			// Remove data from storage list, and return extracted amount
			return (IAEEnergyStack) singularity.addStack(iaeItemStack, actionable);
		}
		return null;
	}

	@Override
	public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> iItemList) {
		// Check if there is singularity
		if (singularity != null) {
			// Iterate over all items in stack
			for (IAEStack<?> stack : singularity.getList(getChannel())) {
				// Add stack to already existed
				iItemList.add((IAEEnergyStack) stack);
			}
		}
		return iItemList;
	}

	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {

		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}
}
