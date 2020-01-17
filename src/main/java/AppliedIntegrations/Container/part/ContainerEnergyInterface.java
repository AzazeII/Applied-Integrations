package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.util.AEPartLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static AppliedIntegrations.grid.Implementation.AIEnergy.RF;

/**
 * @Author Azazell
 */
public class ContainerEnergyInterface extends ContainerWithUpgradeSlots implements IFilterContainer {
	public List<WidgetEnergySlot> energySlotList = new ArrayList<>();

	private static int NUMBER_OF_UPGRADE_SLOTS = 1;
	private static int UPGRADE_X_POS = 186;
	private static int UPGRADE_Y_POS = 8;

	public IEnergyInterface energyInterface;
	public PartEnergyInterface part;
	public EntityPlayer player;

	public Map<AEPartLocation, Number> sideStorageMap = new LinkedHashMap<>();
	public Number storage;
	public LiquidAIEnergy linkedMetric = RF;

	public ContainerEnergyInterface(final EntityPlayer player, final IEnergyInterface energyInterface) {
		super(player);

		this.player = player;
		this.bindPlayerInventory(player.inventory, 149, 207);
		this.energyInterface = energyInterface;

		// Here we add slot of inventory inside given interface
		if (this.energyInterface instanceof PartEnergyInterface) {
			PartEnergyInterface part = (PartEnergyInterface) this.energyInterface;
			part.addListener(this);
			this.part = part;

			AIGridNodeInventory inventory = part.getUpgradeInventory();
			this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);
		} else if (this.energyInterface instanceof TileEnergyInterface) {
			TileEnergyInterface tile = (TileEnergyInterface) this.energyInterface;
			tile.addListener(this);
			AIGridNodeInventory inventory = tile.getUpgradeInventory();
			this.addUpgradeSlots(inventory, NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);
		}
	}

	public void onStorageUpdate(AEPartLocation energySide, IEnergyInterface sender,
	                            Number stored) {
		// If interface-sender is bus, then update central bar. If interface-sender is tile, then update bar from side
		if (sender instanceof PartEnergyInterface) {
			this.storage = stored;
		} else if (sender instanceof TileEnergyInterface) {
			this.sideStorageMap.put(energySide, stored);
		}
	}

	@Override
	protected void syncHostWithGUI() {
		super.syncHostWithGUI();
		if (energyInterface instanceof PartEnergyInterface) {
			NetworkHandler.sendTo(new PacketFilterServerToClient(energyInterface.getFilteredEnergy(AEPartLocation.INTERNAL),
					0, energyInterface), (EntityPlayerMP) this.player);
		} else if (energyInterface instanceof TileEnergyInterface) {
			for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				NetworkHandler.sendTo(new PacketFilterServerToClient(energyInterface.getFilteredEnergy(side),
						side.ordinal(), energyInterface), (EntityPlayerMP) this.player);
			}
		}
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		super.onContainerClosed(player);

		if (part != null) {
			this.part.removeListener(this);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public void updateEnergy(@Nonnull LiquidAIEnergy energy, int index) {
		this.energySlotList.get(index).setCurrentStack(new EnergyStack(energy, 0));
	}

	@Override
	public ISyncHost getSyncHost() {
		return energyInterface;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if (host instanceof IEnergyInterface) {
			this.energyInterface = (IEnergyInterface) host;
		}
	}
}


