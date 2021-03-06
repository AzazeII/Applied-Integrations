package AppliedIntegrations.api.Storage.helpers;


import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.data.IAEStack;

/**
 * @Author Azazell
 */
abstract class SingularityInventoryHandler<T extends IAEStack<T>> implements IMEInventoryHandler<T> {
	public ISingularity singularity;

	public SingularityInventoryHandler() {}

	@Override
	public boolean isPrioritized(T t) {
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

	public final void setSingularity(ISingularity singularity) {
		this.singularity = singularity;
	}
}
