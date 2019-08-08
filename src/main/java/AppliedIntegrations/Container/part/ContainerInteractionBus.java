package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Container.Sync.ITabContainer;
import AppliedIntegrations.Container.slot.SlotFilter;
import AppliedIntegrations.Container.slot.SlotToggle;
import AppliedIntegrations.Gui.Part.Interaction.Buttons.GuiClickModeButton;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketClickModeServerToClient;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.RedstoneMode;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.Parts.Interaction.PartInteraction.EnumInteractionPlaneTabs;

/**
 * @Author Azazell
 */
public class ContainerInteractionBus extends ContainerWithUpgradeSlots implements IUpgradeHostContainer, ITabContainer {
	public final List<SlotFilter> filters = new ArrayList<>();
	private static final int SLOT_AREA = 3;
	public GuiClickModeButton shiftClickButton;
	public GuiImgButton redstoneControlButton;
	private boolean[] slotMatrix = {
			false, false, false,
			false, true, false,
			false, false, false
	};

	public PartInteraction interaction;

	public ContainerInteractionBus(EntityPlayer player, PartInteraction interaction) {
		super(player);
		this.interaction = interaction;

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

		// Armor slots (derived from ContainerPlayer)
		for (int y = 0; y < 4; ++y) {
			final EntityEquipmentSlot armorType = EntityEquipmentSlot.values()[EntityEquipmentSlot.values().length - 1 - y];
			this.addSlotToContainer(new SlotToggle(interaction.armorInventory, y, 8, 8 + y * 18) {
				public int getSlotStackLimit() {
					return 1;
				}

				public boolean isItemValid(ItemStack stack) {
					return stack.getItem().isValidArmor(stack, armorType, player);
				}

				public boolean canTakeStack(EntityPlayer playerIn) {
					ItemStack stack = getStack();
					return (stack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(stack)) && super.canTakeStack(playerIn);
				}

				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[armorType.getIndex()];
				}
			});
		}

		// Offhand slot (derived from ContainerPlayer)
		this.addSlotToContainer(new SlotToggle(interaction.offhandInventory, 0, 77, 62) {
			@Nonnull
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return "minecraft:items/empty_armor_slot_shield";
			}
		});

		this.toggleInvSlots(true);
	}

	private void toggleInvSlots(boolean isEnabled) {
		// Toggle slot. Inverse if slot is in inventory of fake player
		for (Slot slot : this.inventorySlots) {
			if (slot instanceof SlotToggle) {
				((SlotToggle) slot).isEnabled =
						(slot.inventory == interaction.mainInventory || slot.inventory == interaction.armorInventory || slot.inventory == interaction.offhandInventory) != isEnabled;
			}
		}

		// Constantly disable 1st hotbar slot, it may be configured from first inventory and second inventory(this inventory) shouldn't have access to this slot
		hotbarSlots[0].isEnabled = false;
	}

	@Override
	public ISyncHost getSyncHost() {
		return interaction;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		this.interaction = (PartInteraction) host;
	}

	@Override
	public void updateState(boolean redstoneControl, RedstoneMode redstoneMode, byte filterSize) {
		// Change slot matrix depending on filter size
		if (interaction.upgradeInventoryManager.filterSize == 0) {
			slotMatrix = new boolean[]{
					false, false, false,
					false, true, false,
					false, false, false
			};
		} else if (interaction.upgradeInventoryManager.filterSize == 1) {
			slotMatrix = new boolean[]{
					false, true, false,
					true, true, true,
					false, true, false
			};
		} else if (interaction.upgradeInventoryManager.filterSize >= 2) {
			slotMatrix = new boolean[]{
					true, true, true,
					true, true, true,
					true, true, true
			};
		}

		for (SlotFilter filter : filters) {
			filter.updateMatrix(slotMatrix);
		}

		redstoneControlButton.setVisibility(redstoneControl);
		redstoneControlButton.set(redstoneMode);
	}

	@Override
	protected void syncHostWithGUI() {
		super.syncHostWithGUI();
		NetworkHandler.sendTo(new PacketFullSync((byte) interaction.upgradeInventoryManager.filterSize, interaction.upgradeInventoryManager.redstoneMode,
				interaction.upgradeInventoryManager.redstoneControlled, interaction), (EntityPlayerMP) player);
		NetworkHandler.sendTo(new PacketClickModeServerToClient(interaction, interaction.fakePlayer.isSneaking()), (EntityPlayerMP) player);
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
