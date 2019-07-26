package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.RedstoneMode;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerPartEnergyIOBus extends ContainerWithUpgradeSlots implements IFilterContainer {
	public List<WidgetEnergySlot> energySlotList = new ArrayList<>();
	public GuiImgButton redstoneControlBtn;

	// Number of upgrade slots
	private static int NUMBER_OF_UPGRADE_SLOTS = 4;
	// X of upgrades
	private static int UPGRADE_X_POS = 187;
	// Y of upgrades
	private static int UPGRADE_Y_POS = 8;

	public boolean[] configMatrix = {false, false, false, false, true, false, false, false, false};

	public AIOPart part;

	public ContainerPartEnergyIOBus(final AIOPart part, final EntityPlayer player) {
		super(player);

		// Set the host
		this.part = part;

		// Add upgrade slots
		this.addUpgradeSlots(part.getUpgradeInventory(), NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);

		// Bind to the player's inventory
		this.bindPlayerInventory(player.inventory, 102, 160);

		// Register listener
		this.part.addListener(this);
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		// Pass s. trace to super method
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
		this.energySlotList.get(index).setCurrentStack(new EnergyStack(energy, 0));
	}

	public void updateState(boolean redstoneControl, RedstoneMode redstoneMode, byte filterSize) {
		// Set filter matrix, from filter size
		if (filterSize == 0) {
			// Update matrix
			this.configMatrix = new boolean[]{false, false, false, false, true, false, false, false, false};
		}
		if (filterSize == 1) {
			// Update matrix
			this.configMatrix = new boolean[]{false, true, false, true, true, true, false, true, false};
		}

		if (filterSize == 2) {
			// Update matrix
			this.configMatrix = new boolean[]{true, true, true, true, true, true, true, true, true};
		}

		// Iterate for i until it equal to cM.length
		for (int i = 0; i < configMatrix.length; i++) {
			// Get slot and update it to value from config matrix
			energySlotList.get(i).shouldRender = configMatrix[i];
		}

		// Set redstone control button visibility to redstone control
		this.redstoneControlBtn.setVisibility(redstoneControl);

		// Update redstone mode
		this.redstoneControlBtn.set(redstoneMode);
	}

}
