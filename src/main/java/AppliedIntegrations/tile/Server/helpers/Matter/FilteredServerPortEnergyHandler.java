package AppliedIntegrations.tile.Server.helpers.Matter;


import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.tile.Server.TileServerCore;
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
public class FilteredServerPortEnergyHandler extends FilteredServerPortHandler<IAEEnergyStack> {
	public FilteredServerPortEnergyHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileServerCore host) {

		super(filteredMatter, filterMode, host);
	}

	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {

		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}
}
