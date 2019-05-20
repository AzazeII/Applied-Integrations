package AppliedIntegrations.tile.MultiController.helpers.Matter;


import AppliedIntegrations.tile.MultiController.ServerPortHandler;
import AppliedIntegrations.tile.MultiController.TileServerCore;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton.getChannelList;
import static appeng.api.config.SecurityPermissions.EXTRACT;
import static appeng.api.config.SecurityPermissions.INJECT;

/**
 * @Author Azazell
 */
public abstract class FilteredServerPortHandler<T extends IAEStack<T>> extends ServerPortHandler<T> implements IMEInventoryHandler<T> {
	public FilteredServerPortHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileServerCore host) {

		super(filteredMatter, filterMode, host);
	}

	@Override
	public T injectItems(T input, Actionable type, IActionSource src) {
		// Check if stack can be accepted
		if (!canAccept(input)) {
			return input;
		}

		// Check if channel of inventory equal to channel of this handler
		if (!getChannel().equals(getOuterInventory().getChannel())) {
			return input;
		}

		// Pass to outer handler
		return getOuterInventory().injectItems(input, type, src);
	}

	private IMEInventory<T> getOuterInventory() {

		return host.getMainNetworkInventory(getChannel());
	}

	@Override
	public T extractItems(T request, Actionable mode, IActionSource src) {
		// Check if stack can be accepted
		if (!canExtract(request)) {
			return null;
		}

		// Check if channel of inventory equal to channel of this handler
		if (!getChannel().equals(getOuterInventory().getChannel())) {
			return null;
		}

		// Pass to outer handler
		return getOuterInventory().extractItems(request, mode, src);
	}

	private boolean canExtract(T input) {

		return canInteract(input, EXTRACT);
	}

	@Override
	public IItemList<T> getAvailableItems(IItemList<T> out) {
		// Check if handler has at least read access
		if (getAccess() == AccessRestriction.READ || getAccess() == AccessRestriction.READ_WRITE) {
			return out;
		}

		// Check if channel of inventory equal to channel of this handler
		if (!getChannel().equals(getOuterInventory().getChannel())) {
			return out;
		}

		// Next iteration will fill up our list with available and
		// accessible(from this network) stacks from storage grid of main server network.
		// Iterate for each stack of list from outer inventory(inventory of main network)
		getOuterInventory().getAvailableItems(getChannel().createList()).forEach((stack) -> {
			// Check if stack can be operated
			if (canAccept(stack) || canExtract(stack)) {
				// Add stack since it can be operated
				out.add(stack);
			}
		});

		// Return filled list
		return out;
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
					{
						rule.set(AccessRestriction.WRITE);
					}

					// Check if current rule has read access
					else if (rule.get() == AccessRestriction.READ)
					// Set read-write access
					{
						rule.set(AccessRestriction.READ_WRITE);
					}

					// Check if current permissions is extract
				} else if (permissions == EXTRACT) {
					// Check if current rule has no access
					if (rule.get() == AccessRestriction.NO_ACCESS)
					// Set write permissions
					{
						rule.set(AccessRestriction.READ);
					}

					// Check if current rule has read access
					else if (rule.get() == AccessRestriction.WRITE)
					// Set read-write access
					{
						rule.set(AccessRestriction.READ_WRITE);
					}
				}
			}
		});


		return rule.get();
	}

	protected void forStackInList(BiConsumer<IAEStack<? extends IAEStack>, SecurityPermissions> consumer) {
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
	public boolean isPrioritized(T input) {

		return false;
	}

	@Override
	public boolean canAccept(T input) {

		return canInteract(input, INJECT);
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
}
