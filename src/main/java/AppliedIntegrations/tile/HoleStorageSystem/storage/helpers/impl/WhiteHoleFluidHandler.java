package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;

import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class WhiteHoleFluidHandler extends WhiteHoleSingularityInventoryHandler<IAEFluidStack> {
	@Override
	public IAEFluidStack extractItems(IAEFluidStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		// Check if there is singularity
		if (singularity != null) {
			// Remove data from storage list, and return extracted amount
			return (IAEFluidStack) singularity.addStack(iaeItemStack, actionable);
		}
		return null;
	}

	@Override
	public IItemList<IAEFluidStack> getAvailableItems(IItemList<IAEFluidStack> iItemList) {
		// Check if there is singularity
		if (singularity != null) {
			// Iterate over all items in stack
			for (IAEStack<?> stack : singularity.getList(getChannel())) {
				// Add stack to already existed
				iItemList.add((IAEFluidStack) stack);
			}
		}
		return iItemList;
	}

	@Override
	public IStorageChannel<IAEFluidStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
	}
}
