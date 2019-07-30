package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.slot.SlotFilter;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import AppliedIntegrations.Parts.Interaction.PartInteractionPlane;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.RedstoneMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerInteractionPlane extends ContainerWithUpgradeSlots implements IUpgradeHostContainer {
	public final List<SlotFilter> filters = new ArrayList<>();
	private static final int SLOT_AREA = 3;
	private boolean[] slotMatrix = {
			false, false, false,
			false, true, false,
			false, false, false
	};
	private PartInteractionPlane plane;

	public ContainerInteractionPlane(EntityPlayer player, PartInteractionPlane interaction) {
		super(player);
		this.plane = interaction;

		this.bindPlayerInventory(player.inventory, 102, 160);
		this.addUpgradeSlots(interaction.upgradeInventoryManager.upgradeInventory, ContainerPartEnergyIOBus.NUMBER_OF_UPGRADE_SLOTS, ContainerPartEnergyIOBus.UPGRADE_X_POS,
																											ContainerPartEnergyIOBus.UPGRADE_Y_POS);
		int index = 0;
		for (int x = 0; x < SLOT_AREA; x++ ) {
			for (int y = 0; y < SLOT_AREA; y++) {
				SlotFilter slot = new SlotFilter(interaction.filterInventory, index, 62 + (x * 18), 22 + (y * 18), slotMatrix);

				this.addSlotToContainer(slot);
				this.filters.add(slot);

				index++;
			}
		}
	}

	@Override
	public ISyncHost getSyncHost() {
		return plane;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		this.plane = (PartInteractionPlane) host;
	}

	@Override
	public void updateState(boolean redstoneControl, RedstoneMode redstoneMode, byte filterSize) {
		// Change slot matrix depending on filter size
		if (plane.upgradeInventoryManager.filterSize == 0) {
			slotMatrix = new boolean[]{
					false, false, false,
					false, true, false,
					false, false, false
			};
		} else if (plane.upgradeInventoryManager.filterSize == 1) {
			slotMatrix = new boolean[]{
					false, true, false,
					true, true, true,
					false, true, false
			};
		} else if (plane.upgradeInventoryManager.filterSize >= 2) {
			slotMatrix = new boolean[]{
					true, true, true,
					true, true, true,
					true, true, true
			};
		}

		for (SlotFilter filter : filters) {
			filter.updateMatrix(slotMatrix);
		}
	}

	@Override
	protected void syncHostWithGUI() {
		super.syncHostWithGUI();
		NetworkHandler.sendTo(new PacketFullSync((byte) plane.upgradeInventoryManager.filterSize, RedstoneMode.IGNORE,
				plane.upgradeInventoryManager.redstoneControlled, plane), (EntityPlayerMP) player);
	}
}
