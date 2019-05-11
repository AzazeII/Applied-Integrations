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
                // Iterate for each stack
                filteredMatter.get(securityPermissions).get(channel).forEach(iaeStack -> {
                    consumer.accept(iaeStack, securityPermissions);
                });
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

    public boolean canInteract(T input, SecurityPermissions permissions) {
        // Atomic result
        AtomicBoolean canInteract = new AtomicBoolean(false);

        // Iterate for each channel
        List<IStorageChannel<? extends IAEStack<?>>> channelList = getChannelList();

        // Iterate for each channel
        for (IStorageChannel<? extends IAEStack<?>> channel : channelList) {
            // Iterate for each stack
            for (IAEStack<? extends IAEStack> stack : filteredMatter.get(permissions).get(channel)) {
                // Check if stack is equal (by stored matter type) to input
                if (stack.equals(input)) {
                    // Add result depending on filter type
                    canInteract.set(filterMode.get(permissions).get(channel) == IncludeExclude.WHITELIST);

                    // Return value
                    return canInteract.get();
                }
            }
        }

        return canInteract.get();
    }

    @Override
    public boolean isPrioritized(T input) {
        return true;
    }

    @Override
    public boolean canAccept(T input) {
        return canInteract(input, INJECT);
    }

    private boolean canExtract(T input) {
        return canInteract(input, EXTRACT);
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

        // Pass to outer handler
        return outerInventory.injectItems(input, type, src);
    }

    @Override
    public T extractItems(T request, Actionable mode, IActionSource src) {
        // Check if stack can be accepted
        if (!canExtract(request))
            return null;

        // Pass to outer handler
        return outerInventory.extractItems(request, mode, src);
    }

    @Override
    public IItemList<T> getAvailableItems(IItemList<T> out) {
        // Check if stack can be accepted
        if (getAccess() == AccessRestriction.READ || getAccess() == AccessRestriction.READ_WRITE)
            return null;

        // Pass to outer handler
        return outerInventory.getAvailableItems(out);
    }
}
