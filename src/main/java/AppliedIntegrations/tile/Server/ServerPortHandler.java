package AppliedIntegrations.tile.Server;


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
public class ServerPortHandler<T extends IAEStack<T>> {
	protected final TileServerCore host;

	protected final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter;
	private final LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode;

	public ServerPortHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> filteredMatter, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> filterMode, TileServerCore tileServerCore) {

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
		if (input == null) {
			// Can't interact
			return canInteract.get();
		}

		// Get channel of stack
		IStorageChannel<? extends IAEStack<?>> channel = input.getChannel();

		// Get mode
		IncludeExclude mode = filterMode.get(permissions).get(channel);

		// Set by default
		canInteract.set(mode == BLACKLIST);

		// For blacklist: make result true if list doesn't contain this stack
		// For whitelist: make result true if list contain this stack

		// Convert list into stream and check if any stack matches given lambda
		return (mode == WHITELIST) == filteredMatter.get(permissions).get(channel).stream().anyMatch(input::equals);
	}
}
