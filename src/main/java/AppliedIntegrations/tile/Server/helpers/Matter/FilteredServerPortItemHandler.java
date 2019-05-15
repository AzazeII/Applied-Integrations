package AppliedIntegrations.tile.Server.helpers.Matter;

import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author Azazell
 */
public class FilteredServerPortItemHandler extends FilteredServerPortHandler<IAEItemStack> {
    public FilteredServerPortItemHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter,
                                         LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode,
                                         TileServerCore host) {
        super(filteredMatter, filterMode, host);
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }
}
