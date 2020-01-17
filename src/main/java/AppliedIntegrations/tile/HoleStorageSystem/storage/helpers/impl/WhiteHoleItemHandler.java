package AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl;


import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class WhiteHoleItemHandler extends WhiteHoleSingularityInventoryHandler<IAEItemStack> {
	@Override
	public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
		if (singularity != null) {
			return (IAEItemStack) singularity.addStack(iaeItemStack, actionable);
		}
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
		if (singularity != null) {
			for (IAEStack<?> stack : singularity.getList(getChannel())) {
				iItemList.add((IAEItemStack) stack);
			}
		}
		return iItemList;
	}

	@Override
	public IStorageChannel<IAEItemStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}
}
