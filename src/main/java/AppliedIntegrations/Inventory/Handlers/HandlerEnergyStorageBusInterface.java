package AppliedIntegrations.Inventory.Handlers;


import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class HandlerEnergyStorageBusInterface implements IMEInventoryHandler<IAEEnergyStack> {

	private final IEnergyInterface iEnergyInterface;

	private final PartEnergyStorage owner;

	public HandlerEnergyStorageBusInterface(IEnergyInterface iEnergyInterface, PartEnergyStorage owner) {

		this.iEnergyInterface = iEnergyInterface;
		this.owner = owner;
	}

	@Override
	public IAEEnergyStack injectItems(IAEEnergyStack iaeEnergyStack, Actionable actionable, IActionSource iActionSource) {
		// Check for permission to read data
		if (getAccess() == AccessRestriction.READ) {
			return null;
		}

		return iEnergyInterface.getOuterGridInventory().injectItems(iaeEnergyStack, actionable, iActionSource);
	}

	@Override
	public AccessRestriction getAccess() {

		return owner.access;
	}

	@Override
	public boolean isPrioritized(IAEEnergyStack iaeEnergyStack) {

		return false;
	}

	@Override
	public boolean canAccept(IAEEnergyStack iaeEnergyStack) {

		return true;
	}

	@Override
	public int getPriority() {
		// TODO: 2019-03-27 priority
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
	public IAEEnergyStack extractItems(IAEEnergyStack iaeEnergyStack, Actionable actionable, IActionSource iActionSource) {
		// Check for permission to read data
		if (getAccess() == AccessRestriction.WRITE) {
			return null;
		}

		return iEnergyInterface.getOuterGridInventory().extractItems(iaeEnergyStack, actionable, iActionSource);
	}

	@Override
	public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> iItemList) {
		// Check for permission to read data
		if (getAccess() == AccessRestriction.WRITE) {
			return null;
		}

		return iEnergyInterface.getOuterGridInventory().getAvailableItems(iItemList);
	}

	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {

		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}
}