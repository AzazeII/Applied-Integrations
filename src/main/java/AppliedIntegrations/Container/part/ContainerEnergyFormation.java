package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Parts.Energy.PartEnergyFormation;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerEnergyFormation extends ContainerWithUpgradeSlots implements IFilterContainer {
	public List<WidgetEnergySlot> energySlotList = new ArrayList<>();

	private PartEnergyFormation plane;

	private static int PLAYER_INV_POSITION_Y = 102;
	private static int HOTBAR_INV_POSITION_Y = 160;

	public ContainerEnergyFormation(PartEnergyFormation plane, EntityPlayer player) {
		super(player);
		this.plane = plane;
		this.plane.linkedListeners.add(this);
		this.bindPlayerInventory(player.inventory, PLAYER_INV_POSITION_Y + 67, HOTBAR_INV_POSITION_Y + 67);
	}

	@Override
	public void updateEnergy(LiquidAIEnergy energy, int index) {
		this.energySlotList.get(index).setCurrentStack(new EnergyStack(energy, 0));
	}

	@Override
	public ISyncHost getSyncHost() {
		return plane;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		// Check if host match our host class
		if (host instanceof PartEnergyFormation) {
			// Update current host
			this.plane = (PartEnergyFormation) host;
		}
	}
}
