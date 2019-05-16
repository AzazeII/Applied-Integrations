package AppliedIntegrations.Inventory;

import appeng.api.storage.data.IAEStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class DefaultPriorityList<T extends IAEStack<T>> implements IPartitionList<T> {

	private static final List NULL_LIST = new ArrayList();

	@Override
	public boolean isListed(final T input) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public Iterable<T> getItems() {
		return NULL_LIST;
	}
}
