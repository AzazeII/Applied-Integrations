package AppliedIntegrations.tile.Server;

import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton.getChannelList;
import static appeng.api.config.IncludeExclude.BLACKLIST;

/**
 * @Author Azazell
 */
public class ServerPortHandler<T extends IAEStack<T>> {
    protected final TileServerCore host;
    protected final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter;
    protected final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode;

    public ServerPortHandler(
            LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter,
            LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileServerCore tileServerCore) {
        this.filteredMatter = filteredMatter;
        this.filterMode = filterMode;
        this.host = tileServerCore;
    }

    /**
     * @param input stack to check
     * @param permissions interaction type
     * @return can requester interact with given stack?
     */
    protected boolean canInteract(T input, SecurityPermissions permissions) {
        // Atomic result
        AtomicBoolean canInteract = new AtomicBoolean(false);

        // Check not null
        if (input == null)
            // Can't interact
            return canInteract.get();

        // Iterate for each channel
        List<IStorageChannel<? extends IAEStack<?>>> channelList = getChannelList();

        // Iterate for each channel
        for (IStorageChannel<? extends IAEStack<?>> channel : channelList) {
            // Get mode
            IncludeExclude mode = filterMode.get(permissions).get(channel);

            // For blacklist:
            //  Make result true if list doesn't contain this stack
            // For whitelist:
            //  Make result true if list contain this tack
            // Set by default
            canInteract.set(mode == BLACKLIST);

            // Iterate for each stack in filter
            for (IAEStack<? extends IAEStack> stack : filteredMatter.get(permissions).get(channel)) {
                // Check if stack equal to input
                if (input.equals(stack)){
                    // Change value to opposite
                    canInteract.set(!(mode == BLACKLIST));

                    // Force-return
                    return canInteract.get();
                }
            }
        }

        return canInteract.get();
    }
}
