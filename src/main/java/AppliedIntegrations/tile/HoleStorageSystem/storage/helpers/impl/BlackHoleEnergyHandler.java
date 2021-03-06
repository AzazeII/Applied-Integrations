package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;


import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;

import static appeng.api.config.Actionable.MODULATE;

/**
 * @Author Azazell
 */
public class BlackHoleEnergyHandler extends BlackHoleSingularityInventoryHandler<IAEEnergyStack> {

	@Override
	public IAEEnergyStack injectItems(IAEEnergyStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		if (singularity != null) {
			if (actionable == MODULATE) {
				singularity.addStack(iaeItemStack, actionable);
			}
			return null;
		}
		return iaeItemStack;
	}

	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}
}
