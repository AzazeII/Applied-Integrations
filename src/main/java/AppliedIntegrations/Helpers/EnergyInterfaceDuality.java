package AppliedIntegrations.Helpers;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import teamroots.embers.power.EmberCapabilityProvider;

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
		if (IntegrationsHelper.instance.isLoaded(Ember)) {
			initializedStorages.add(Ember);
		}
	}

	public <T> T getCapability(Capability<T> capability, AEPartLocation side) {

		if (capability == Capabilities.FORGE_ENERGY) {
			// FE (RF) Capability
			return (T) this.getEnergyStorage(RF, side);
			// Ember capability
		} else if (AIConfig.enableEmberFeatures && IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability) {
			return (T) this.getEnergyStorage(Ember, side);
			// Joule capability
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
		} else if (AIConfig.enableEmberFeatures && IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability) {
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
				NetworkHandler.sendTo(new PacketFilterServerToClient(energy, 0, owner), (EntityPlayerMP) listener.player);
			}
		}
	}	@Override
	public double getMaxTransfer(AEPartLocation side) {

		return owner.getMaxTransfer(side);
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
					NetworkHandler.sendTo(new PacketProgressBar(owner, energy, energySide), (EntityPlayerMP) listener.player);
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



	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {

		return owner.getFilteredEnergy(side);
	}


	@Override
	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {

		return owner.getEnergyStorage(energy, side);
	}


	@Override
	public void doInjectDualityWork(Actionable action) throws NullNodeConnectionException {

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
						// Split value to integer
						Number num = (Number) getEnergyStorage(energy, side).getStored();
						Integer stored = num.intValue();
						// Check if there is energy exists and energy not filtered
						if (stored > 0 && this.getFilteredEnergy(side) != energy) {
							// Find minimum value between energy stored and max transfer
							int ValuedReceive = (int) Math.min(stored, this.getMaxTransfer(side));
							// Find amount of energy that can be injected
							int InjectedAmount = owner.InjectEnergy(new EnergyStack(energy, ValuedReceive), SIMULATE);

							// Inject energy in ME Network
							owner.InjectEnergy(new EnergyStack(energy, InjectedAmount), MODULATE);
							// Remove injected amount from interface storage
							owner.getEnergyStorage(energy, side).modifyEnergyStored(-InjectedAmount);
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


	/**
	 * check if energy storage initialized (mod with capability for this storage loaded)
	 *
	 * @return
	 */
	private boolean isStorageInitialized(LiquidAIEnergy energy) {

		return initializedStorages.contains(energy);
	}


	@Override
	public void doExtractDualityWork(Actionable action) throws NullNodeConnectionException {

		IGridNode node = owner.getGridNode();
		if (node == null) {
			throw new NullNodeConnectionException();
		}

		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			if (action == Actionable.MODULATE) {
				if (getFilteredEnergy(side) != null) {
					Class<?> T = getEnergyStorage(getFilteredEnergy(side), INTERNAL).getTypeClass();
					IInterfaceStorageDuality interfaceStorageDuality = getEnergyStorage(getFilteredEnergy(side), INTERNAL);

					if (T != Long.class) {

						// Get int value from stored amount
						int stored = ((Number) interfaceStorageDuality.getStored()).intValue();
						// Get int value from capacity
						int capacity = ((Number) interfaceStorageDuality.getMaxStored()).intValue();
						// minimum value between max transfer and empty space in storage
						int valuedExtract = Math.min(capacity - stored, (int) getMaxTransfer(side));
						// Extract energy from drive array
						int extracted = owner.ExtractEnergy(new EnergyStack(getFilteredEnergy(side), valuedExtract), SIMULATE);

						// Check if storage can store new energy
						if (extracted + stored <= capacity) {
							// Drain energy from network
							owner.ExtractEnergy(new EnergyStack(getFilteredEnergy(side), extracted), MODULATE);

							// Give energy to tile's storage
							interfaceStorageDuality.modifyEnergyStored(extracted);
						}

						// Unlike the "binary" energy storage, the real (physical) storage should not have high transfer values, like 500k RF/t
						// Otherwise it will be really OP
						transferEnergy(getFilteredEnergy(side), Math.min(stored, Math.min((int) getMaxTransfer(side), 50000)), side.getFacing());
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
				getEnergyStorage(filteredEnergy, AEPartLocation.fromFacing(side)).modifyEnergyStored(-capabilityHelper.receiveEnergy(Amount, false, filteredEnergy));
			}
		}
	}
}
