package AppliedIntegrations.Inventory;

import appeng.api.storage.data.IAEStack;

/**
 * @Author Azazell
 */
public interface IPartitionList<T extends IAEStack<T>> {
	boolean isListed(T input);

	boolean isEmpty();

	Iterable<T> getItems();
}
