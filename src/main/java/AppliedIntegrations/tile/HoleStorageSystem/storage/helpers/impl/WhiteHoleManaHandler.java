package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;

import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
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
public class WhiteHoleManaHandler extends WhiteHoleSingularityInventoryHandler<IAEManaStack> {
	@Override
	public IAEManaStack extractItems(IAEManaStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		// Check if there is singularity
		if (singularity != null) {
			// Remove data from storage list, and return extracted amount
			return (IAEManaStack) singularity.addStack(iaeItemStack, actionable);
		}
		return null;
	}

	@Override
	public IItemList<IAEManaStack> getAvailableItems(IItemList<IAEManaStack> iItemList) {
		// Check if there is singularity
		if (singularity != null) {
			// Iterate over all items in stack
			for (IAEStack<?> stack : singularity.getList(getChannel())) {
				// Add stack to already existed
				iItemList.add((IAEManaStack) stack);
			}
		}
		return iItemList;
	}

	@Override
	public IStorageChannel<IAEManaStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
	}
}
