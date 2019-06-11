package AppliedIntegrations.Helpers;

import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketBarChange;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import AppliedIntegrations.Network.Packets.PartGUI.PacketProgressBar;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.IEnergyInterfaceDuality;
import AppliedIntegrations.api.IInterfaceStorageDuality;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.EnumCapabilityType;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;
import appeng.capabilities.Capabilities;
import appeng.me.GridAccessException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.LinkedList;
import java.util.List;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;
import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * @Author Azazell
 * Class handler for both tile interface, and host interface
 */
public class EnergyInterfaceDuality implements IEnergyInterfaceDuality {

	public boolean debug;

	private IEnergyInterface owner;

	private List<LiquidAIEnergy> initializedStorages = new LinkedList<>();

	public EnergyInterfaceDuality(IEnergyInterface owner) {

		this.owner = owner;

		// RF always Initialized, as FE
		initializedStorages.add(RF);
		if (IntegrationsHelper.instance.isLoaded(EU)) {
			initializedStorages.add(EU);
		}
		if (IntegrationsHelper.instance.isLoaded(J)) {
			initializedStorages.add(J);
		}
	}

	public <T> T getCapability(Capability<T> capability, AEPartLocation side) {

		if (capability == Capabilities.FORGE_ENERGY) {
			// FE (RF) Capability
			return (T) this.getEnergyStorage(RF, side);
			// Ember capability
		} else if (IntegrationsHelper.instance.isLoaded(J) && capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY) {
			return (T) this.getEnergyStorage(J, side);
			// EU capability
		}
		return null;
	}

	public boolean hasCapability(Capability<?> capability) {
		// Register FE capability
		if (capability == Capabilities.FORGE_ENERGY) {
			return true;
		} else if (IntegrationsHelper.instance.isLoaded(J)) {
			if (capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY) {
				return true;
			}
		}
		return false;
	}

	public void initStorage(AEPartLocation side) {
		// Iterate for each energy
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Check if storage is initialized
			if (IntegrationsHelper.instance.isLoaded(energy))
			// Update energy
			{
				owner.initEnergyStorage(energy, side);
			}
		}
	}

	public void notifyListenersOfFilterEnergyChange(LiquidAIEnergy energy) {

		for (ContainerEnergyInterface listener : owner.getListeners()) {
			if (listener != null) {
				NetworkHandler.sendTo(new PacketFilterServerToClient(energy, 0, owner),
						(EntityPlayerMP) listener.player);
			}
		}
	}

	// Synchronize data with all listeners
	public void notifyListenersOfEnergyBarChange(LiquidAIEnergy energy, AEPartLocation energySide) {
		// Iterate for each container listener of host
		for (ContainerEnergyInterface listener : owner.getListeners()) {
			// Check not null
			if (listener != null) {
				// Get host tile
				TileEntity hostTile = owner instanceof PartEnergyInterface ? ((PartEnergyInterface) owner).getHostTile() : (TileEnergyInterface) owner;

				// Check not null
				if (hostTile != null) {
					// Send packet
					NetworkHandler.sendTo(new PacketProgressBar(owner, energy, energySide),
							(EntityPlayerMP) listener.player);
				}
			}
		}
	}

	public void notifyListenersOfBarFilterChange(LiquidAIEnergy bar) {

		for (ContainerEnergyInterface listener : owner.getListeners()) {
			if (listener != null) {
				NetworkHandler.sendTo(new PacketBarChange(bar, owner), (EntityPlayerMP) listener.player);
			}
		}
	}

	/**
	 * check if energy storage initialized (mod with capability for this storage loaded)
	 * @return Is mod with capability for this energy is initialized
	 */
	private boolean isStorageInitialized(LiquidAIEnergy energy) {
		return initializedStorages.contains(energy);
	}

	@Override
	public double getMaxTransfer(AEPartLocation side) {

		return owner.getMaxTransfer(side);
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {

		return owner.getFilteredEnergy(side);
	}

	@Override
	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		return owner.getEnergyStorage(energy, side);
	}

	@Override
	public void doInjectDualityWork(Actionable action) throws NullNodeConnectionException, GridAccessException {

		IGridNode node = owner.getGridNode();
		if (node == null) {
			throw new NullNodeConnectionException();
		}

		// Iterate over all sides(only for interface block)
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			// Is it modulate, or simulate?
			if (action == Actionable.MODULATE) {
				// Iterate over allowed energy type
				for (EnumCapabilityType energyType : EnumCapabilityType.values) {
					// Get energy from type
					LiquidAIEnergy energy = energyType.energy;
					// Check if storage available;
					if (isStorageInitialized(energy)) {
						// Get storage
						IInterfaceStorageDuality energyStorage = getEnergyStorage(energy, side);

						// Split value to integer
						int stored = energyStorage.getStored().intValue();

						// Check if there is energy exists and energy not filtered
						if (stored > 0 && this.getFilteredEnergy(side) != energy) {
							// Find minimum value between energy stored and max transfer
							int valuedReceive = (int) Math.min(stored, this.getMaxTransfer(side));

							// Find amount of energy that can be injected
							int injectedAmount = owner.injectEnergy(new EnergyStack(energy, valuedReceive), SIMULATE);

							// Find amount of energy that can be extracted
							int extractedAmount = energyStorage.extract(energyStorage.toNativeValue(injectedAmount),
									SIMULATE).intValue();

							// Inject energy in ME Network
							owner.injectEnergy(new EnergyStack(energy, extractedAmount), MODULATE);

							// Drain extracted amount from network
							energyStorage.extract(energyStorage.toNativeValue(extractedAmount), MODULATE);
						}
					}
				}
			}
			if (!(owner instanceof TileEnergyInterface)) {
				// Break if owner is partEnergyInterface (iterate only one time)
				debug = false;
				break;
			}
		}
	}

	@Override
	public void doExtractDualityWork(Actionable action) throws NullNodeConnectionException, GridAccessException {
		IGridNode node = owner.getGridNode();
		if (node == null) {
			throw new NullNodeConnectionException();
		}

		// Iterate over each sides
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			// Check if action is modulate
			if (action == Actionable.MODULATE) {
				// Check if filtered energy not equal to null
				if (getFilteredEnergy(side) != null) {
					// Get storage class type
					Class<?> t = getEnergyStorage(getFilteredEnergy(side), INTERNAL).getTypeClass();

					// Get storage duality
					IInterfaceStorageDuality interfaceStorageDuality = getEnergyStorage(getFilteredEnergy(side),
							INTERNAL);

					// Check not long
					if (t != Long.class) {
						// Get int value from stored amount
						int stored = interfaceStorageDuality.getStored().intValue();

						// Get int value from capacity
						int capacity = interfaceStorageDuality.getMaxStored().intValue();

						// minimum value between max transfer and empty space in storage
						int valuedExtract = Math.min(capacity - stored, (int) getMaxTransfer(side));

						// Simulate energy insertion into our storage duality
						int injectedAmount = interfaceStorageDuality.receive(interfaceStorageDuality.toNativeValue(
								valuedExtract), SIMULATE).intValue();

						// Simulate energy extraction from cells array
						int extractedAmount = owner.extractEnergy(new EnergyStack(getFilteredEnergy(side), injectedAmount),
								SIMULATE);

						// Make storage receive extracted amount
						interfaceStorageDuality.receive((interfaceStorageDuality.toNativeValue(extractedAmount)), MODULATE);

						// Drain extracted amount from network
						owner.extractEnergy(new EnergyStack(getFilteredEnergy(side), extractedAmount), MODULATE);

						// Unlike the "binary" energy storage, the real (physical) storage should not have high transfer values, like 500k RF/t
						// Otherwise it will be really OP
						transferEnergy(getFilteredEnergy(side),
								Math.min(stored, Math.min((int) getMaxTransfer(side), 50000)),
								side.getFacing().getOpposite());
					} else {
						// TODO: 2019-02-27 Add tesla extraction
					}
				}
			}

			if (!(owner instanceof TileEnergyInterface)) {
				// Break if owner is partEnergyInterface (iterate only one time)
				break;
			}
		}
	}

	private void transferEnergy(LiquidAIEnergy filteredEnergy, int Amount, EnumFacing side) {
		TileEntity tile = owner.getFacingTile(side);

		if (tile == null) {
			return;
		}

		if (filteredEnergy == null) {
			return;
		}

		for (EnumCapabilityType type : EnumCapabilityType.values) {
			if (tile.hasCapability(type.getInputCapability(), side)) {
				CapabilityHelper capabilityHelper = new CapabilityHelper(tile, AEPartLocation.fromFacing(side));
				getEnergyStorage(filteredEnergy,
						AEPartLocation.fromFacing(side)).modifyEnergyStored(-capabilityHelper.receiveEnergy(Amount,
						false,
						filteredEnergy));
			}
		}
	}
}
