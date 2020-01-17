package AppliedIntegrations.Parts.Energy;
import AppliedIntegrations.Container.part.ContainerEnergyFormation;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Helpers.Energy.StackCapabilityHelper;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import appeng.util.Platform;
import appeng.util.item.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusContainer.listContainsNonNullValues;
import static AppliedIntegrations.Parts.Energy.PartEnergyStorage.FILTER_SIZE;
import static appeng.api.config.Actionable.MODULATE;
import static java.util.Collections.singletonList;

/**
 * @Author Azazell
 */
public class PartEnergyFormation extends AIPlanePart implements ICellContainer, IEnergyMachine {
	public final List<LiquidAIEnergy> filteredEnergies = new LinkedList<>();
	private List<ChangeHandler<LiquidAIEnergy>> filteredEnergiesChangeHandler = new ArrayList<>();
	private boolean updateRequested;
	public List<ContainerEnergyFormation> linkedListeners = new ArrayList<>();

	public PartEnergyFormation() {
		super(PartEnum.EnergyFormation);

		for (int index = 0; index < FILTER_SIZE; index++) {
			this.filteredEnergies.add(null);
			this.filteredEnergiesChangeHandler.add(new ChangeHandler<>());
		}
	}

	@Override
	public IPartModel getStaticModels() {
		if (isPowered()) {
			if (isActive()) {
				return PartModelEnum.FORMATION_HAS_CHANNEL;
			} else {
				return PartModelEnum.FORMATION_ON;
			}
		}
		return PartModelEnum.FORMATION_OFF;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiFormationPlane, player, getHostSide(), getHostTile().getPos());
				updateRequested = true;
				return true;
			}
		}

		return false;
	}

	@Override
	protected void doWork(int ticksSinceLastCall) {
		// Try to extract filtered energies from network to entity next to us
		for (ContainerEnergyFormation listener : linkedListeners) {
			for (int i = 0; i < FILTER_SIZE; i++) {
				int finalI = i;

				filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
					NetworkHandler.sendTo(new PacketFilterServerToClient(energy, finalI, this),
							(EntityPlayerMP) listener.player);
				}));

				if (updateRequested) {
					NetworkHandler.sendTo(new PacketFilterServerToClient(filteredEnergies.get(i), finalI, this),
							(EntityPlayerMP) listener.player);
				}
			}
		}

		updateRequested = false;
	}

	@Override
	public void blinkCell(int slot) {
		// Ignored (this host not operating any cells)
	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		if (channel != this.getChannel() || currentEntities.isEmpty()) {
			return new LinkedList<>();
		}

		return singletonList(new IMEInventoryHandler<IAEEnergyStack>() {
			@Override
			public AccessRestriction getAccess() {

				return AccessRestriction.WRITE;
			}

			@Override
			public boolean isPrioritized(IAEEnergyStack input) {
				// TODO: 2019-03-31 Priority
				return false;
			}

			@Override
			public boolean canAccept(IAEEnergyStack input) {

				return input instanceof IAEEnergyStack;
			}

			@Override
			public int getPriority() {
				// TODO: 2019-03-31 Priority
				return 0;
			}

			@Override
			public int getSlot() {
				return 0;
			}

			@Override
			public boolean validForPass(int i) {

				return true;
			}

			@Override
			public IAEEnergyStack injectItems(IAEEnergyStack input, Actionable type, IActionSource src) {
				if (input == null) {
					return null;
				}

				if (!listContainsNonNullValues(filteredEnergies) && !filteredEnergies.contains(input.getEnergy())) {
					return input;
				}

				if (currentEntities.isEmpty()) {
					return input;
				}

				if (input.getStackSize() > Integer.MAX_VALUE) {
					return input;
				}

				int amountInjected = 0;

				int request = (int) input.getStackSize();

				for (Entity workingEntity : currentEntities) {
					if (workingEntity instanceof EntityItem) {
						amountInjected += fillStack(((EntityItem) workingEntity).getItem(), workingEntity, request);
					} else if (workingEntity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) workingEntity;
						for (ItemStack stack : player.inventory.mainInventory) {
							amountInjected += fillStack(stack, workingEntity, request);
						}
					}
				}

				if (amountInjected == input.getStackSize()) {
					return null;
				} else {
					return input.copy().setStackSize(input.getStackSize() - amountInjected);
				}
			}

			@Override
			public IAEEnergyStack extractItems(IAEEnergyStack request, Actionable mode, IActionSource src) {
				return null;
			}

			@Override
			public IItemList getAvailableItems(IItemList out) {
				return new ItemList();
			}

			@Override
			public IStorageChannel getChannel() {
				return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
			}
		});
	}

	private int fillStack(ItemStack stack, Entity ent, int request) {
		StackCapabilityHelper helper = new StackCapabilityHelper(stack);

		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (helper.hasCapability(energy)) {
				int injected = helper.injectEnergy(energy, request, MODULATE);

				if (injected > 0) {
					spawnLightning(ent);
				}

				return injected;
			}
		}

		return 0;
	}

	@Override
	public int getPriority() {
		// TODO: 2019-03-31 Add priority gui tab
		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
		if (iCellInventory != null) {
			iCellInventory.persist();
		}

		getHostTile().getWorld().markChunkDirty(getHostTile().getPos(), getHostTile());
	}

	@Override
	public void updateFilter(LiquidAIEnergy energy, int index) {
		filteredEnergies.set(index, energy);
	}
}
