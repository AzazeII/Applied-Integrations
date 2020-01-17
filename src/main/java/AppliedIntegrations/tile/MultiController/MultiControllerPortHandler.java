package AppliedIntegrations.tile.MultiController;


import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static appeng.api.config.IncludeExclude.BLACKLIST;
import static appeng.api.config.IncludeExclude.WHITELIST;

/**
 * @Author Azazell
 */
public class MultiControllerPortHandler<T extends IAEStack<T>> {
	protected final TileMultiControllerCore host;

	protected final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter;
	private final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode;

	public MultiControllerPortHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileMultiControllerCore tileMultiControllerCore) {

		this.filteredMatter = filteredMatter;
		this.filterMode = filterMode;
		this.host = tileMultiControllerCore;
	}

	/**
	 * @param input stack to check
	 * @param permissions interaction type
	 * @return can requester interact with given stack?
	 */
	protected boolean canInteract(T input, SecurityPermissions permissions) {
		AtomicBoolean canInteract = new AtomicBoolean(false);
		if (input == null) {
			return canInteract.get();
		}

		IStorageChannel<? extends IAEStack<?>> channel = input.getChannel();
		IncludeExclude mode = filterMode.get(permissions).get(channel);
		canInteract.set(mode == BLACKLIST);

		// For blacklist: make result true if list doesn't contain this stack
		// For whitelist: make result true if list contain this stack
		return (mode == WHITELIST) == filteredMatter.get(permissions).get(channel).stream().anyMatch(input::equals);
	}
}
