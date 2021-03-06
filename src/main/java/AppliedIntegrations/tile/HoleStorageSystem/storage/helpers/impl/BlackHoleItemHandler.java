package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;


import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;

import static appeng.api.config.Actionable.MODULATE;

/**
 * @Author Azazell
 */
public class BlackHoleItemHandler extends BlackHoleSingularityInventoryHandler<IAEItemStack> {

	@Override
	public IAEItemStack injectItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		if (singularity != null) {
			if (actionable == MODULATE) {
				singularity.addStack(iaeItemStack, actionable);
			}
			return null;
		}
		return iaeItemStack;
	}

	@Override
	public IStorageChannel<IAEItemStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}
}
