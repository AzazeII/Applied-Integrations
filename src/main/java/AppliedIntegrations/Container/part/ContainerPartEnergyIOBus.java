package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerPartEnergyIOBus extends ContainerWithUpgradeSlots implements IFilterContainer {
	public List<LiquidAIEnergy> energyFilterList = new ArrayList<LiquidAIEnergy>();
	// Number of upgrade slots
	private static int NUMBER_OF_UPGRADE_SLOTS = 4;
	// X of upgrades
	private static int UPGRADE_X_POS = 187;
	// Y of upgrades
	private static int UPGRADE_Y_POS = 8;
	private AIOPart part;

	public ContainerPartEnergyIOBus(final AIOPart part, final EntityPlayer player) {

		super(part, player);

		// Set the host
		this.part = part;

		// Add upgrade slots
		this.addUpgradeSlots(part.getUpgradeInventory(), NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);

		// Bind to the player's inventory
		this.bindPlayerInventory(player.inventory, 102, 160);

		// Register listener
		this.part.addListener(this);

		// Pre-fill list with values
		for (int i = 0; i < 9; i++) {
			energyFilterList.add(null);
		}
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		// Pass s. trace to super
		super.onContainerClosed(player);

		// Remove listener
		this.part.removeListener(this);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	@Override
	public ISyncHost getSyncHost() {
		return this.part;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		// Check if host match our host class
		if (host instanceof AIOPart) {
			// Update current host
			this.part = (AIOPart) host;
		}
	}

	@Override
	public void updateEnergy(@Nonnull LiquidAIEnergy energy, int index) {
		this.energyFilterList.set(index, energy);
	}
}
