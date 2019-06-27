package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author Azazell
 */
public class ContainerEnergyInterface extends ContainerWithUpgradeSlots {
	// Number of upgrades
	private static int NUMBER_OF_UPGRADE_SLOTS = 1;

	// X of upgrades
	private static int UPGRADE_X_POS = 186;

	// Y of upgrades
	private static int UPGRADE_Y_POS = 8;

	public IEnergyInterface energyInterface;
	public PartEnergyInterface part;
	public EntityPlayer player;

	public Map<AEPartLocation, Number> sideStorageMap = new LinkedHashMap<>();
	public Number storage;

	public ContainerEnergyInterface(final EntityPlayer player, final IEnergyInterface energyInterface) {
		super(energyInterface, player);

		// Set player
		this.player = player;

		// Bind player's inventory
		this.bindPlayerInventory(player.inventory, 149, 207);

		// Update interface
		this.energyInterface = energyInterface;

		// Ignored on client
		if (Platform.isServer()) {
			// Force update filtered energy of gui
			// getDuality().notifyListenersOfFilterEnergyChange(filteredEnergy, 0);
		}

		// check if interface host or tile?
		if (this.energyInterface instanceof PartEnergyInterface) {
			// Get host
			PartEnergyInterface part = (PartEnergyInterface) this.energyInterface;

			// register listener
			part.addListener(this);

			// Assign host
			this.part = part;

			// Get upgrade inventory
			AIGridNodeInventory inventory = part.getUpgradeInventory();

			// Add slots to inventory
			this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);
		} else if (this.energyInterface instanceof TileEnergyInterface) {
			// Get tile from interface
			TileEnergyInterface tile = (TileEnergyInterface) this.energyInterface;

			// register listener
			tile.addListener(this);

			// Get upgrade inventory
			AIGridNodeInventory inventory = tile.getUpgradeInventory();

			// add slots
			this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);
		}
	}

	private void syncHostWithGUI() {
		NetworkHandler.sendTo(new PacketCoordinateInit(energyInterface), (EntityPlayerMP) this.player);
	}

	public void onStorageUpdate(LiquidAIEnergy energy, AEPartLocation energySide, IEnergyInterface sender) {
		// Get stored energy from interface
		Number stored = sender.getEnergyStorage(energy, energySide).getStored();

		// If interface-sender is bus, then update central bar. If interface-sender is tile, then update bar from side
		// Check if sender is part-interface
		if (sender instanceof PartEnergyInterface) {
			// Update storage of central bar
			this.storage = stored;
		} else if (sender instanceof TileEnergyInterface) {
			// Update storage of bar from side
			this.sideStorageMap.put(energySide, stored);
		}
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {

		super.onContainerClosed(player);

		if (part != null) {
			this.part.removeListener(this);
			this.part.setRealContainer(null);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		this.syncHostWithGUI();
	}

	@Override
	public ISyncHost getSyncHost() {
		return energyInterface;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		// Check if host match energy interface interface
		if (host instanceof IEnergyInterface) {
			// Check for cast safety
			this.energyInterface = (IEnergyInterface) host;
		}
	}
}


