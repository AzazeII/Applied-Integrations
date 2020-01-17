package AppliedIntegrations.tile.MultiController.helpers.Matter;


import AppliedIntegrations.tile.MultiController.MultiControllerPortHandler;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import appeng.api.AEApi;
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

import static appeng.api.config.SecurityPermissions.EXTRACT;
import static appeng.api.config.SecurityPermissions.INJECT;

/**
 * @Author Azazell
 */
public abstract class FilteredMultiControllerPortHandler<T extends IAEStack<T>> extends MultiControllerPortHandler<T> implements IMEInventoryHandler<T> {
	public FilteredMultiControllerPortHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileMultiControllerCore host) {
		super(filteredMatter, filterMode, host);
	}

	@Override
	public T injectItems(T input, Actionable type, IActionSource src) {
		if (!canAccept(input)) {
			return input;
		}

		if (getOuterInventory() == null)
			return input;

		if (!getChannel().equals(getOuterInventory().getChannel())) {
			return input;
		}

		return getOuterInventory().injectItems(input, type, src);
	}

	private IMEInventory<T> getOuterInventory() {
		return host.getMainNetworkInventory(getChannel());
	}

	@Override
	public T extractItems(T request, Actionable mode, IActionSource src) {
		if (!canExtract(request)) {
			return null;
		}

		if (getOuterInventory() == null)
			return null;

		if (!getChannel().equals(getOuterInventory().getChannel())) {
			return null;
		}

		return getOuterInventory().extractItems(request, mode, src);
	}

	private boolean canExtract(T input) {
		return canInteract(input, EXTRACT);
	}

	@Override
	public IItemList<T> getAvailableItems(IItemList<T> out) {
		if (getAccess() == AccessRestriction.READ || getAccess() == AccessRestriction.READ_WRITE) {
			return out;
		}

		if (getOuterInventory() == null)
			return out;

		if (!getChannel().equals(getOuterInventory().getChannel())) {
			return out;
		}

		// Next iteration will fill up our list with available and
		// accessible(from this network) stacks from storage grid of main server network.
		getOuterInventory().getAvailableItems(getChannel().createList()).forEach((stack) -> {
			if (canAccept(stack) || canExtract(stack)) {
				out.add(stack);
			}
		});

		return out;
	}

	@Override
	public AccessRestriction getAccess() {
		AtomicReference<AccessRestriction> rule = new AtomicReference<>();
		rule.set(AccessRestriction.NO_ACCESS);
		forStackInList((stack, permissions) -> {
			if (stack != null && stack.getStackSize() == 0) {
				if (permissions == INJECT) {
					if (rule.get() == AccessRestriction.NO_ACCESS) {
						rule.set(AccessRestriction.WRITE);
					}

					else if (rule.get() == AccessRestriction.READ) {
						rule.set(AccessRestriction.READ_WRITE);
					}

				} else if (permissions == EXTRACT) {
					if (rule.get() == AccessRestriction.NO_ACCESS) {
						rule.set(AccessRestriction.READ);
					}

					else if (rule.get() == AccessRestriction.WRITE) {
						rule.set(AccessRestriction.READ_WRITE);
					}
				}
			}
		});


		return rule.get();
	}

	protected void forStackInList(BiConsumer<IAEStack<? extends IAEStack>, SecurityPermissions> consumer) {
		for (SecurityPermissions securityPermissions : new SecurityPermissions[]{INJECT, EXTRACT}) {
			AEApi.instance().storage().storageChannels().forEach((channel -> {
				if (filteredMatter.get(securityPermissions) != null && filteredMatter.get(securityPermissions).get(channel) != null) {
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
