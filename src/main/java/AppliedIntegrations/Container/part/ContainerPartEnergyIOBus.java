package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.YesNo;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerPartEnergyIOBus extends ContainerWithUpgradeSlots implements IFilterContainer, IUpgradeHostContainer {
	public List<WidgetEnergySlot> energySlotList = new ArrayList<>();
	public GuiImgButton redstoneControlBtn;

	public static int NUMBER_OF_UPGRADE_SLOTS = 4;
	public static int UPGRADE_X_POS = 187;
	public static int UPGRADE_Y_POS = 8;

	public boolean[] configMatrix = {false, false, false, false, true, false, false, false, false};

	public AIOPart part;

	public ContainerPartEnergyIOBus(final AIOPart part, final EntityPlayer player) {
		super(player);

		this.part = part;
		this.addUpgradeSlots(part.getUpgradeInventory(), NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);
		this.bindPlayerInventory(player.inventory, 102, 160);
		this.part.addListener(this);
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		super.onContainerClosed(player);
		this.part.removeListener(this);
	}

	@Override
	protected void syncHostWithGUI() {
		super.syncHostWithGUI();
		NetworkHandler.sendTo(new PacketFullSync(part.upgradeInventoryManager, part), (EntityPlayerMP) player);
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
		if (host instanceof AIOPart) {
			this.part = (AIOPart) host;
		}
	}

	@Override
	public void updateEnergy(@Nonnull LiquidAIEnergy energy, int index) {
		this.energySlotList.get(index).setCurrentStack(new EnergyStack(energy, 0));
	}

	@Override
	public void updateState(boolean redstoneControl, boolean compareFuzzy, boolean autoCrafting,
	                        RedstoneMode redstoneMode, FuzzyMode fuzzyMode, YesNo craftOnly, byte filterSize) {
		if (filterSize == 0) {
			this.configMatrix = new boolean[]{false, false, false, false, true, false, false, false, false};
		}

		if (filterSize == 1) {
			this.configMatrix = new boolean[]{false, true, false, true, true, true, false, true, false};
		}

		if (filterSize == 2) {
			this.configMatrix = new boolean[]{true, true, true, true, true, true, true, true, true};
		}

		for (int i = 0; i < configMatrix.length; i++) {
			energySlotList.get(i).shouldRender = configMatrix[i];
		}

		this.redstoneControlBtn.setVisibility(redstoneControl);
		this.redstoneControlBtn.set(redstoneMode);
	}

}
