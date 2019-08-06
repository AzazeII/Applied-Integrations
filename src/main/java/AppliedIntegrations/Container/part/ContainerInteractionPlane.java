package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.ITabContainer;
import AppliedIntegrations.Container.slot.SlotFilter;
import AppliedIntegrations.Container.slot.SlotToggle;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import AppliedIntegrations.Parts.Interaction.PartInteractionPlane;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.RedstoneMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.Parts.Interaction.PartInteractionPlane.EnumInteractionPlaneTabs;

/**
 * @Author Azazell
 */
public class ContainerInteractionPlane extends ContainerWithUpgradeSlots implements IUpgradeHostContainer, ITabContainer {
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

		// Bind inventories
		this.bindPlayerInventory(player.inventory, 102, 160);
		this.bindPlayerInventory(interaction.mainInventory, 84, 142);
		this.addUpgradeSlots(interaction.upgradeInventoryManager.upgradeInventory, ContainerPartEnergyIOBus.NUMBER_OF_UPGRADE_SLOTS, ContainerPartEnergyIOBus.UPGRADE_X_POS,
																					ContainerPartEnergyIOBus.UPGRADE_Y_POS);

		// Filter slots
		int index = 0;
		for (int x = 0; x < SLOT_AREA; x++ ) {
			for (int y = 0; y < SLOT_AREA; y++) {
				SlotFilter slot = new SlotFilter(interaction.filterInventory, index, 62 + (x * 18), 22 + (y * 18), slotMatrix);

				this.addSlotToContainer(slot);
				this.filters.add(slot);

				index++;
			}
		}

		// Armor slots
		for (int y = 0; y < 4; y++) {
			this.addSlotToContainer(new SlotToggle(interaction.armorInventory, y, 8, 8 + (y * 18)));
		}

		this.toggleInvSlots(true);
	}

	private void toggleInvSlots(boolean isEnabled) {
		// Toggle slot. Inverse if slot is in inventory of fake player
		for (Slot slot : this.inventorySlots) {
			if (slot instanceof SlotToggle) {
				((SlotToggle) slot).isEnabled =
						(slot.inventory == plane.mainInventory || slot.inventory == plane.armorInventory || slot.inventory == plane.offhandInventory) != isEnabled;
			}
		}

		// Constantly disable 1st hotbar slot, it may be configured from first inventory and second inventory(this inventory) shouldn't have access to this slot
		hotbarSlots[0].isEnabled = false;
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

	@Override
	public void setTab(Enum tabEnum) {
		EnumInteractionPlaneTabs planeTab = (EnumInteractionPlaneTabs) tabEnum;

		if (planeTab == EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER) {
			// Make slots from first tab enabled
			toggleInvSlots(true);

		} else if (planeTab == EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_INVENTORY) {
			// Make slots from first tab disabled
			toggleInvSlots(false);
		}
	}
}
