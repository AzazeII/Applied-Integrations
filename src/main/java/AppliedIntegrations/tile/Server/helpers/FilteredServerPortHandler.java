package AppliedIntegrations.tile.Server.helpers;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton.getChannelList;
import static appeng.api.config.IncludeExclude.BLACKLIST;
import static appeng.api.config.SecurityPermissions.EXTRACT;
import static appeng.api.config.SecurityPermissions.INJECT;

/**
 * @Author Azazell
 */
public abstract class FilteredServerPortHandler<T extends IAEStack<T>> implements IMEInventoryHandler<T> {

    private final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter;
    private final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode;
    private final IMEInventory<T> outerInventory;

    public FilteredServerPortHandler(
            LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter,
            LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode,
            IMEInventory<T> outerInventory) {
        this.filteredMatter = filteredMatter;
        this.filterMode = filterMode;
        this.outerInventory = outerInventory;
    }

    private void forStackInList(BiConsumer<IAEStack<? extends IAEStack>, SecurityPermissions> consumer) {
        // Iterate for each permission
        for (SecurityPermissions securityPermissions : new SecurityPermissions[]{INJECT, EXTRACT}) {
            // Iterate for each channel
            getChannelList().forEach((channel -> {
                // Check not null
                if (filteredMatter.get(securityPermissions) != null && filteredMatter.get(securityPermissions).get(channel) != null) {
                    // Iterate for each stack
                    filteredMatter.get(securityPermissions).get(channel).forEach(iaeStack -> consumer.accept(iaeStack, securityPermissions));
                }
            }));
        }
    }

    @Override
    public AccessRestriction getAccess() {
        // Create atomic rule
        AtomicReference<AccessRestriction> rule = new AtomicReference<>();

        // Initial rule
        rule.set(AccessRestriction.NO_ACCESS);

        // Iterate for each AE stack in inner list
        forStackInList((stack, permissions) -> {
            // Check if stack not null and not empty
            if (stack != null && stack.getStackSize() == 0) {
                // Check if current permissions is inject
                if (permissions == INJECT) {
                    // Check if current rule has no access
                    if (rule.get() == AccessRestriction.NO_ACCESS)
                        // Set write permissions
                        rule.set(AccessRestriction.WRITE);

                        // Check if current rule has read access
                    else if (rule.get() == AccessRestriction.READ)
                        // Set read-write access
                        rule.set(AccessRestriction.READ_WRITE);

                    // Check if current permissions is extract
                } else if (permissions == EXTRACT) {
                    // Check if current rule has no access
                    if (rule.get() == AccessRestriction.NO_ACCESS)
                        // Set write permissions
                        rule.set(AccessRestriction.READ);

                        // Check if current rule has read access
                    else if (rule.get() == AccessRestriction.WRITE)
                        // Set read-write access
                        rule.set(AccessRestriction.READ_WRITE);
                }
            }
        });


        return rule.get();
    }

    /**
     * @param input stack to check
     * @param permissions interaction type
     * @return can requester interact with given stack?
     */
    public boolean canInteract(T input, SecurityPermissions permissions) {
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
            // Make result true if list doesn't contain this stack
            // For whitelist:
            // Make result true if list contain this tack
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

    private boolean canExtract(T input) {
        return canInteract(input, EXTRACT);
    }

    @Override
    public boolean canAccept(T input) {
        return canInteract(input, INJECT);
    }

    @Override
    public boolean isPrioritized(T input) {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return true;
    }

    @Override
    public T injectItems(T input, Actionable type, IActionSource src) {
        // Check if stack can be accepted
        if (!canAccept(input))
            return input;

        // Check if channel of inventory equal to channel of this handler
        if (!getChannel().equals(outerInventory.getChannel()))
            return input;

        // Pass to outer handler
        return outerInventory.injectItems(input, type, src);
    }

    @Override
    public T extractItems(T request, Actionable mode, IActionSource src) {
        // Check if stack can be accepted
        if (!canExtract(request))
            return null;

        // Check if channel of inventory equal to channel of this handler
        if (!getChannel().equals(outerInventory.getChannel()))
            return null;

            // Pass to outer handler
        return outerInventory.extractItems(request, mode, src);
    }

    @Override
    public IItemList<T> getAvailableItems(IItemList<T> out) {
        // Check if handler has at least read access
        if (getAccess() == AccessRestriction.READ || getAccess() == AccessRestriction.READ_WRITE)
            return out;

        // Check if channel of inventory equal to channel of this handler
        if (!getChannel().equals(outerInventory.getChannel()))
            return out;

        // Pass to outer handler
        return outerInventory.getAvailableItems(out);
    }
}
