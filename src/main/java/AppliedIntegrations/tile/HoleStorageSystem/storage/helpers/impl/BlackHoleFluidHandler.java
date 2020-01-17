package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;


import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;

import static appeng.api.config.Actionable.MODULATE;

/**
 * @Author Azazell
 */
public class BlackHoleFluidHandler extends BlackHoleSingularityInventoryHandler<IAEFluidStack> {

	@Override
	public IAEFluidStack injectItems(IAEFluidStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		if (singularity != null) {
			if (actionable == MODULATE) {
				singularity.addStack(iaeItemStack, actionable);
			}
			return null;
		}
		return iaeItemStack;
	}

	@Override
	public IStorageChannel<IAEFluidStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
	}
}
