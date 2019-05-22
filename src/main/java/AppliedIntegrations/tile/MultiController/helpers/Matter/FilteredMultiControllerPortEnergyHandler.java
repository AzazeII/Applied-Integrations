package AppliedIntegrations.tile.MultiController.helpers.Matter;


import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author Azazell
 */
public class FilteredMultiControllerPortEnergyHandler extends FilteredMultiControllerPortHandler<IAEEnergyStack> {
	public FilteredMultiControllerPortEnergyHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileMultiControllerCore host) {

		super(filteredMatter, filterMode, host);
	}

	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {

		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}
}
