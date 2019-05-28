package AppliedIntegrations.Inventory.Handlers;


import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
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

		// Check for permission to write data
		if (getAccess() == AccessRestriction.READ) {
			return input;
		}

		// Check not null
		if (input == null) {
			return input;
		}

		// Check meaningful
		if (!input.isMeaningful()) {
			return input;
		}

		// Check if filtered energies has any energies
		if (!owner.filteredEnergies.isEmpty()) {
			// Check if one of filtered energies is equal to input energy
			if (!owner.filteredEnergies.contains(input.getEnergy()))
			// Ignore
			{
				return input;
			}
		}

		// Create helper
		CapabilityHelper helper = new CapabilityHelper(storage, owner.getHostSide().getOpposite());

		// Get number injected
		int added = helper.receiveEnergy(input.getStackSize(), type == Actionable.SIMULATE, this.type.energy);

		// Calculate not added
		int notAdded = (int) input.getStackSize() - added;

		// Check greater than 0
		if (notAdded > 0)
		// Return value not added
		{
			return input.copy().setStackSize(notAdded);
		}

		// Don't return value at all
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
		// Check for permission to read data
		if (getAccess() == AccessRestriction.WRITE) {
			return null;
		}

		// Check not null
		if (request == null) {
			return null;
		}

		// Check meaningful
		if (!request.isMeaningful()) {
			return null;
		}

		// Check if filtered energies has any energies
		if (!owner.filteredEnergies.isEmpty()) {
			// Check if one of filtered energies is equal to input energy
			if (!owner.filteredEnergies.contains(request.getEnergy()))
			// Ignore
			{
				return null;
			}
		}

		// Create capability helper
		CapabilityHelper helper = new CapabilityHelper(storage, owner.getHostSide());

		// Get extracted value
		int extracted = helper.extractEnergy(request.getStackSize(), mode == Actionable.SIMULATE, this.type.energy);

		// Check greater than 0
		if (extracted > 0)
		// Return extracted amount
		{
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
		// Check for permission to read data
		if (getAccess() == AccessRestriction.WRITE) {
			return null;
		}

		// Create capability helper
		CapabilityHelper helper = new CapabilityHelper(storage, owner.getHostSide());

		// Get stored energy
		int stored = helper.getStored(type.energy);

		// Add stored energy to output
		out.add(AEEnergyStack.fromStack(new EnergyStack(type.energy, stored)));

		// Return given list
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
		// TODO: check line 147
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
