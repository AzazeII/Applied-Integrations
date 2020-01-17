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
		if (singularity != null) {
			return (IAEEnergyStack) singularity.addStack(iaeItemStack, actionable);
		}
		return null;
	}

	@Override
	public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> iItemList) {
		if (singularity != null) {
			for (IAEStack<?> stack : singularity.getList(getChannel())) {
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
