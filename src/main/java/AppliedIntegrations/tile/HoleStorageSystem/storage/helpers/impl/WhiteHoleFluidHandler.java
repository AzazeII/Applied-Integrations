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
		if (singularity != null) {
			return (IAEFluidStack) singularity.addStack(iaeItemStack, actionable);
		}
		return null;
	}

	@Override
	public IItemList<IAEFluidStack> getAvailableItems(IItemList<IAEFluidStack> iItemList) {
		if (singularity != null) {
			for (IAEStack<?> stack : singularity.getList(getChannel())) {
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
