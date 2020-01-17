package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;


import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;

import static appeng.api.config.Actionable.MODULATE;

/**
 * @Author Azazell
 */
public class BlackHoleManaHandler extends BlackHoleSingularityInventoryHandler<IAEManaStack> {

	@Override
	public IAEManaStack injectItems(IAEManaStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		if (singularity != null) {
			if (actionable == MODULATE) {
				singularity.addStack(iaeItemStack, actionable);
			}
			return null;
		}
		return iaeItemStack;
	}

	@Override
	public IStorageChannel<IAEManaStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
	}
}
