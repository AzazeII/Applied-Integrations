package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerEnergyStorage extends ContainerWithUpgradeSlots implements IFilterContainer {
	public List<WidgetEnergySlot> energySlotList = new ArrayList<>();

	private static final int UPGRADE_SLOT_X = 187;
	private static final int UPGRADE_SLOT_Y = 8;
	private static int PLAYER_INV_POSITION_Y = 102;
	private static int HOTBAR_INV_POSITION_Y = 160;

	public GuiImgButton accessMode;
	private PartEnergyStorage storageBus;

	public ContainerEnergyStorage(final PartEnergyStorage part, final EntityPlayer player) {
		super(player);

		part.linkedListeners.add(this);

		this.storageBus = part;
		this.addUpgradeSlots(part.getUpgradeInventory(), 1, UPGRADE_SLOT_X, UPGRADE_SLOT_Y);
		this.bindPlayerInventory(player.inventory, PLAYER_INV_POSITION_Y + 67, HOTBAR_INV_POSITION_Y + 67);
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return true;
	}

	@Override
	public ISyncHost getSyncHost() {
		return this.storageBus;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if (host instanceof PartEnergyStorage) {
			this.storageBus = (PartEnergyStorage) host;
		}
	}

	@Override
	public void updateEnergy(@Nonnull LiquidAIEnergy energy, int index) {
		this.energySlotList.get(index).setCurrentStack(new EnergyStack(energy, 0));
	}
}