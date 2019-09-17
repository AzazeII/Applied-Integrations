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

		// Iterate until filter size
		for (int index = 0; index < FILTER_SIZE; index++) {
			// Fill vector
			this.filteredEnergies.add(null);

			// Fill list
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
		// Activation logic is server sided
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				// Open gui
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiFormationPlane, player, getHostSide(), getHostTile().getPos());

				updateRequested = true;

				// Render click
				return true;
			}
		}

		return false;
	}

	@Override
	protected void doWork(int ticksSinceLastCall) {
		// Iterate over all listeners
		for (ContainerEnergyFormation listener : linkedListeners) {
			// Iterate over all filtered energies
			for (int i = 0; i < FILTER_SIZE; i++) {
				// Create effectively final variable
				int finalI = i;

				// Create on change event
				filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
					// Sync with client
					NetworkHandler.sendTo(new PacketFilterServerToClient(energy, finalI, this),
							(EntityPlayerMP) listener.player);
				}));

				// Check if update was requested
				if (updateRequested) {
					// Sync with client
					NetworkHandler.sendTo(new PacketFilterServerToClient(filteredEnergies.get(i), finalI, this),
							(EntityPlayerMP) listener.player);
				}
			}
		}

		// Reset update request
		updateRequested = false;
	}

	@Override
	public void blinkCell(int slot) {
		// Ignored (this host not operating any cells)
	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		// Check if channel present working channel, and handler not null
		if (channel != this.getChannel() || currentEntities.isEmpty()) {
			return new LinkedList<>();
		}

		// Return only one handler for tile
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
				// Ignored
				return 0;
			}

			@Override
			public boolean validForPass(int i) {

				return true;
			}

			@Override
			public IAEEnergyStack injectItems(IAEEnergyStack input, Actionable type, IActionSource src) {
				// Check not null
				if (input == null) {
					return null;
				}

				if (!listContainsNonNullValues(filteredEnergies) && !filteredEnergies.contains(input.getEnergy())) {
					return input;
				}

				// Check has list
				if (currentEntities.isEmpty()) {
					return input;
				}

				// Check smaller or equal to Integer.MAX_VALUE
				if (input.getStackSize() > Integer.MAX_VALUE) {
					return input;
				}

				// Summary injected
				int amountInjected = 0;

				// input in integer form
				int request = (int) input.getStackSize();

				// Iterate over all entities
				for (Entity workingEntity : currentEntities) {
					// Check if entity is item
					if (workingEntity instanceof EntityItem) {
						amountInjected += fillStack(((EntityItem) workingEntity).getItem(), workingEntity, request);
					} else if (workingEntity instanceof EntityPlayer) {
						// Get player from working entity
						EntityPlayer player = (EntityPlayer) workingEntity;

						// Scan player's inventory
						for (ItemStack stack : player.inventory.mainInventory) {
							amountInjected += fillStack(stack, workingEntity, request);
						}
					}
				}

				// Check if all energy was injected
				if (amountInjected == input.getStackSize()) {
					// Return null, as everything was injected
					return null;
				} else {
					// Return input stack size - all amount injected
					return input.copy().setStackSize(input.getStackSize() - amountInjected);
				}
			}

			@Override
			public IAEEnergyStack extractItems(IAEEnergyStack request, Actionable mode, IActionSource src) {
				// No items can be extracted
				return null;
			}

			@Override
			public IItemList getAvailableItems(IItemList out) {
				// Return empty item list
				return new ItemList();
			}

			@Override
			public IStorageChannel getChannel() {
				return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
			}
		});
	}

	private int fillStack(ItemStack stack, Entity ent, int request) {
		// Check if stack belongs to one of capabilities
		StackCapabilityHelper helper = new StackCapabilityHelper(stack);

		// Iterate over all energy types
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Check if stack has capability
			if (helper.hasCapability(energy)) {
				// Simulate extraction
				int injected = helper.injectEnergy(energy, request, MODULATE);

				// Modulate injection
				if (injected > 0) {
					// Spawn lightning
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
		// Check if inventory not null
		if (iCellInventory != null) {
			// Persist inventory
			iCellInventory.persist();
		}

		// Mark dirty
		getHostTile().getWorld().markChunkDirty(getHostTile().getPos(), getHostTile());
	}

	@Override
	public void updateFilter(LiquidAIEnergy energy, int index) {
		filteredEnergies.set(index, energy);
	}
}
