package AppliedIntegrations.Helpers;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketBarChange;
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

/**
 * @Author Azazell
 * Class handler for both tile interface, and host interface
 */
public class EnergyInterfaceDuality implements IEnergyInterfaceDuality {
	private IEnergyInterface owner;
	private List<LiquidAIEnergy> initializedStorages = new LinkedList<>();

	public EnergyInterfaceDuality(IEnergyInterface owner) {
		this.owner = owner;

		// RF always Initialized, as FE
		initializedStorages.add(RF);

		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			initializedStorages.add(EU);
		}
		if (IntegrationsHelper.instance.isLoaded(J, false)) {
			initializedStorages.add(J);
		}
	}

	public <T> T getCapability(Capability<T> capability, AEPartLocation side) {
		if (capability == Capabilities.FORGE_ENERGY) {
			return (T) this.getEnergyStorage(RF, side);
		} else if (IntegrationsHelper.instance.isLoaded(J, false) && capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY) {
			return (T) this.getEnergyStorage(J, side);
		}
		return null;
	}

	public boolean hasCapability(Capability<?> capability) {
		// Register FE capability
		if (capability == Capabilities.FORGE_ENERGY) {
			return true;
		} else if (IntegrationsHelper.instance.isLoaded(J, false)) {
			return capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY || capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY;
		}
		return false;
	}

	public void initStorage(AEPartLocation side) {
		// Initialize every energy storage on this side
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (IntegrationsHelper.instance.isLoaded(energy, false)) {
				owner.initEnergyStorage(energy, side);
			}
		}
	}

	// Synchronize data with all listeners
	public void notifyListenersOfEnergyBarChange(LiquidAIEnergy energy, AEPartLocation energySide) {
		// Notify every listener about new energy bar value
		for (ContainerEnergyInterface listener : owner.getListeners()) {
			if (listener != null) {
				TileEntity hostTile = owner instanceof PartEnergyInterface ? ((PartEnergyInterface) owner).getHostTile() : (TileEnergyInterface) owner;
				if (hostTile != null) {
					NetworkHandler.sendTo(new PacketProgressBar(owner, energySide, getEnergyStorage(energy, energySide).getStored()),
							(EntityPlayerMP) listener.player);
				}
			}
		}
	}

	public void notifyListenersOfBarFilterChange(LiquidAIEnergy bar) {
		// Notify every listener about new filter value
		for (ContainerEnergyInterface listener : owner.getListeners()) {
			if (listener != null) {
				NetworkHandler.sendTo(new PacketBarChange(bar, owner), (EntityPlayerMP) listener.player);
			}
		}
	}

	/**
	 * check if energy storage initialized (mod with capability for this storage loaded)
	 *
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

		// Iterate for all sides and check for stored energy for injection into network
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			if (action == Actionable.MODULATE) {
				for (EnumCapabilityType energyType : EnumCapabilityType.values) {
					LiquidAIEnergy energy = energyType.energy;
					if (isStorageInitialized(energy)) {
						IInterfaceStorageDuality energyStorage = getEnergyStorage(energy, side);
						int stored = energyStorage.getStored().intValue();

						// Simulate energy injection into network and extract injected amount from storage
						if (stored > 0 && this.getFilteredEnergy(side) != energy) {
							int valuedReceive = (int) Math.min(stored, this.getMaxTransfer(side));
							int injectedAmount = owner.injectEnergy(new EnergyStack(energy, valuedReceive), SIMULATE);
							if (injectedAmount > 0) {
								int extractedAmount = energyStorage.extract(energyStorage.toNativeValue(injectedAmount),
										SIMULATE).intValue();

								if (extractedAmount > 0) {
									owner.setLastInjectedEnergy(side, energy);
									owner.injectEnergy(new EnergyStack(energy, extractedAmount), MODULATE);
									energyStorage.extract(energyStorage.toNativeValue(extractedAmount), MODULATE);
								}
							}
						}
					}
				}
			}

			if (!(owner instanceof TileEnergyInterface)) {
				// Break if owner is partEnergyInterface (iterate only one time)
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
				// Get filtered energy
				LiquidAIEnergy filteredEnergy = getFilteredEnergy(side);

				// Check if filtered energy not equal to null
				if (filteredEnergy != null) {
					// Get storage class type
					Class<?> t = getEnergyStorage(filteredEnergy, side).getTypeClass();

					// Get storage duality
					IInterfaceStorageDuality interfaceStorageDuality = getEnergyStorage(filteredEnergy, side);

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

						// Check if any energy was injected
						if (injectedAmount > 0) {
							// Simulate energy extraction from cells array
							int extractedAmount = owner.extractEnergy(new EnergyStack(filteredEnergy,
									injectedAmount), SIMULATE);

							// Check if any energy was extracted
							if (extractedAmount > 0) {
								// Make storage receive extracted amount
								interfaceStorageDuality.receive((interfaceStorageDuality.toNativeValue(extractedAmount)),
										MODULATE);

								// Drain extracted amount from network
								owner.extractEnergy(new EnergyStack(filteredEnergy, extractedAmount),
										MODULATE);

								// Unlike the "binary" energy storage, the real (in-world) storage should not have high transfer values, like 500k RF/t
								// Otherwise it will be really OP
								transferEnergy(filteredEnergy, Math.min(stored, Math.min((int) getMaxTransfer(side), 50000)),
										side.getFacing().getOpposite());
							}
						}
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

	private void transferEnergy(LiquidAIEnergy filteredEnergy, int amount, EnumFacing side) {
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
						AEPartLocation.fromFacing(side)).modifyEnergyStored(-capabilityHelper.receiveEnergy(amount, false, filteredEnergy));
			}
		}
	}
}
