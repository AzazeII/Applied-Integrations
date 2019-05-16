package AppliedIntegrations.Inventory;


import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class MEPassThrough<T extends IAEStack<T>> implements IMEInventoryHandler<T> {

	private final IStorageChannel<T> wrappedChannel;

	private IMEInventory<T> internal;

	public MEPassThrough(final IMEInventory<T> i, final IStorageChannel<T> channel) {

		this.wrappedChannel = channel;
		this.setInternal(i);
	}

	protected IMEInventory<T> getInternal() {

		return this.internal;
	}

	public void setInternal(final IMEInventory<T> i) {

		this.internal = i;
	}

	@Override
	public T injectItems(final T input, final Actionable type, final IActionSource src) {

		return this.internal.injectItems(input, type, src);
	}

	@Override
	public T extractItems(final T request, final Actionable type, final IActionSource src) {

		return this.internal.extractItems(request, type, src);
	}

	@Override
	public IItemList<T> getAvailableItems(final IItemList out) {

		return this.internal.getAvailableItems(out);
	}

	@Override
	public IStorageChannel<T> getChannel() {

		return this.internal.getChannel();
	}

	@Override
	public AccessRestriction getAccess() {

		return AccessRestriction.READ_WRITE;
	}

	@Override
	public boolean isPrioritized(final T input) {

		return false;
	}

	@Override
	public boolean canAccept(final T input) {

		return true;
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
	public boolean validForPass(final int i) {

		return true;
	}

	IStorageChannel<T> getWrappedChannel() {

		return this.wrappedChannel;
	}
}
