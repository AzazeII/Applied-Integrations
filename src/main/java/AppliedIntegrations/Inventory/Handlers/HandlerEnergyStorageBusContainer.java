package AppliedIntegrations.Inventory.Handlers;


import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnumCapabilityType;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import net.minecraft.tileentity.TileEntity;

import java.util.List;
import java.util.Objects;

/**
 * @Author Azazell
 */
public class HandlerEnergyStorageBusContainer implements IMEInventoryHandler<IAEEnergyStack> {

	private TileEntity storage;

	private EnumCapabilityType type;

	private PartEnergyStorage owner;

	public HandlerEnergyStorageBusContainer(PartEnergyStorage owner, TileEntity operand, EnumCapabilityType type) {
		this.storage = operand;
		this.type = type;
		this.owner = owner;
	}

	public static boolean listContainsNonNullValues(List<LiquidAIEnergy> filteredEnergies) {
		return filteredEnergies.stream().allMatch(Objects::isNull);
	}

	/**
	 * Store new items, or simulate the addition of new items into the ME Inventory.
	 *
	 * @param input item to add.
	 * @param type  action type
	 * @param src   action source
	 * @return returns the number of items not added.
	 */
	@Override
	public IAEEnergyStack injectItems(IAEEnergyStack input, Actionable type, IActionSource src) {
		if (getAccess() == AccessRestriction.READ) {
			return input;
		}

		if (input == null) {
			return null;
		}

		if (!input.isMeaningful()) {
			return input;
		}

		if (!listContainsNonNullValues(owner.filteredEnergies)) {
			if (!owner.filteredEnergies.contains(input.getEnergy())) {
				return input;
			}
		}

		CapabilityHelper helper = new CapabilityHelper(storage, owner.getHostSide().getOpposite());
		int added = helper.receiveEnergy(input.getStackSize(), type == Actionable.SIMULATE, this.type.energy);
		int notAdded = (int) input.getStackSize() - added;

		if (notAdded > 0) {
			return input.copy().setStackSize(notAdded);
		}

		return null;
	}

	/**
	 * Extract the specified item from the ME Inventory
	 *
	 * @param request item to request ( with stack size. )
	 * @param mode    simulate, or perform action?
	 * @return returns the number of items extracted, null
	 */
	@Override
	public IAEEnergyStack extractItems(IAEEnergyStack request, Actionable mode, IActionSource src) {
		if (getAccess() == AccessRestriction.WRITE) {
			return null;
		}

		if (request == null) {
			return null;
		}

		if (!request.isMeaningful()) {
			return null;
		}

		if (!listContainsNonNullValues(owner.filteredEnergies)){
			if (!owner.filteredEnergies.contains(request.getEnergy())) {
				return null;
			}
		}

		CapabilityHelper helper = new CapabilityHelper(storage, owner.getHostSide().getOpposite());
		int extracted = helper.extractEnergy(request.getStackSize(), mode == Actionable.SIMULATE, this.type.energy);

		if (extracted > 0) {
			return request.copy().setStackSize(extracted);
		}
		return null;
	}

	/**
	 * request a full report of all available items, storage.
	 *
	 * @param out the IItemList the results will be written too
	 * @return returns same list that was passed in, is passed out
	 */
	@Override
	public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> out) {
		if (getAccess() == AccessRestriction.WRITE) {
			return null;
		}

		CapabilityHelper helper = new CapabilityHelper(storage, owner.getHostSide().getOpposite());
		int stored = helper.getStored(type.energy);
		out.add(AEEnergyStack.fromStack(new EnergyStack(type.energy, stored)));
		return out;
	}

	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}

	@Override
	public AccessRestriction getAccess() {
		// TODO: 2019-03-27 Sync with gui
		return owner.access;
	}

	@Override
	public boolean isPrioritized(IAEEnergyStack input) {
		return false;
	}

	@Override
	public boolean canAccept(IAEEnergyStack input) {
		if (this.storage == null) {
			return false;
		}
		return true;
	}

	@Override
	public int getPriority() {
		// TODO: 2019-02-27 Priority
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
