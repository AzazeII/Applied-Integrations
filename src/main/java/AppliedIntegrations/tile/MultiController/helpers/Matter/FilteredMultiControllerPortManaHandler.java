package AppliedIntegrations.tile.MultiController.helpers.Matter;


import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
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
public class FilteredMultiControllerPortManaHandler extends FilteredMultiControllerPortHandler<IAEManaStack> {
	public FilteredMultiControllerPortManaHandler(
			LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter,
			LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode,
			TileMultiControllerCore host) {
		super(filteredMatter, filterMode, host);
	}

	@Override
	public IStorageChannel<IAEManaStack> getChannel() {

		return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
	}
}
