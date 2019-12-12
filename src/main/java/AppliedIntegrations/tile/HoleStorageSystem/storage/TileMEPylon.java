package AppliedIntegrations.tile.HoleStorageSystem.storage;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Blocks.Additions.BlockBlackHole;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketPylonSingularitySync;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.BlackHoleSystem.IPylon;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.HoleStorageSystem.AITileStorageCell;
import AppliedIntegrations.tile.HoleStorageSystem.TimeHandler;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.storage.helpers.impl.*;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.util.Platform;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static AppliedIntegrations.Blocks.Additions.BlockMEPylon.FACING;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.of;

/**
 * @Author Azazell
 */
public class TileMEPylon extends AITileStorageCell implements ICellContainer, IGridTickable, IPylon {
	// How long drain(growth of beam) continues?
	public static final int DRAIN_LASTS_SECONDS = 3;

	// Linked maps of *passive* handlers, which standing as handler factory
	private static LinkedHashMap<IStorageChannel, Class<? extends BlackHoleSingularityInventoryHandler<?>>> passiveBlackHoleHandlers = new LinkedHashMap<>();
	private static LinkedHashMap<IStorageChannel, Class<? extends WhiteHoleSingularityInventoryHandler<?>>> passiveWhiteHoleHandlers = new LinkedHashMap<>();

	public ISingularity operatedTile;

	// Drain in AE
	public float beamDrain = 0F;

	// Should this tile drain energy from ME?
	public boolean shouldDrain = false;

	// Time handler for drain
	public TimeHandler drainHandler = new TimeHandler();

	// Linked maps of *active* handlers, which is generated by passive maps and used by current pylon
	private LinkedHashMap<IStorageChannel, BlackHoleSingularityInventoryHandler<?>> activeBlackHoleHandlers = new LinkedHashMap<>();
	private LinkedHashMap<IStorageChannel, WhiteHoleSingularityInventoryHandler<?>> activeWhiteHoleHandlers = new LinkedHashMap<>();

	private boolean activeHandlersLoaded = false;

	private boolean configured = false;

	// Adds new handler for black hole storage
	public static void addBlackHoleHandler(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, IStorageChannel chan) {
		// Add handler class
		passiveBlackHoleHandlers.put(chan, handlerClassA);
	}

	// Adds new handler for white hole storage
	public static void addWhiteHoleHandler(Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB, IStorageChannel chan) {
		// Add handler class
		passiveWhiteHoleHandlers.put(chan, handlerClassB);
	}

	private void notifyClient() {
		// Notify client
		//NetworkHandler.sendToAllInRange(new PacketPylonSingularitySync(this.operatedTile, getBeamState(), shouldDrain, this.getPos()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
		NetworkHandler.sendToAll(new PacketPylonSingularitySync(this.operatedTile, getBeamState(), shouldDrain, this.getPos()));
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		TickRateModulation superTickRate = super.tickingRequest(node, ticksSinceLastCall);

		// Call only on server
		if (Platform.isServer()) {
			// Check if proxy isn't configured yet
			if (!configured) {
				// Configure proxy
				getProxy().setValidSides(of(getFw().getOpposite()));

				// Toggle configuration
				configured = true;
			}

			// Check if handlers not exist yet
			if (!activeHandlersLoaded) {
				// Init handlers
				initHandlers();
			}

			// Check if active
			if (node.isActive()) {
				// Check if has no handled singularity
				if (!hasSingularity()) {
					tryToGetSingularity();
				} else if (shouldDrain) {
					// Check if time not passed yet
					if (!drainHandler.hasTimePassed(world, DRAIN_LASTS_SECONDS)) {
						// Consume energy for data transmitting, over beam
						IEnergyGrid energyGrid = node.getGrid().getCache(IEnergyGrid.class);

						// Simulate drain, and check if grid has enough energy
						double drain = energyGrid.extractAEPower(beamDrain, Actionable.SIMULATE, PowerMultiplier.CONFIG);

						// Drain energy
						energyGrid.extractAEPower(drain, Actionable.MODULATE, PowerMultiplier.CONFIG);

						// Sync client
						notifyClient();
					} else {
						// Don't drain energy anymore
						shouldDrain = false;

						// Sync client
						notifyClient();
					}
				}
			}
		}

		return superTickRate;
	}

	private void tryToGetSingularity() {
		// Check if tile already has singularity
		if (hasSingularity()) {
			return;
		}

		// Check if tile can indirectly see singularity
		for (int i = 1; i < AIConfig.maxPylonDistance + 1; i++) {
			// get block with offset
			IBlockState state = world.getBlockState(pos.offset(getFw(), i));

			// Check if block is singularity
			if (state.getBlock() instanceof BlockBlackHole) {
				// Set operated tile
				setSingularity((ISingularity) world.getTileEntity(pos.offset(getFw(), i)));

				// Change energy drain to current iteration multiplied by config value
				beamDrain = (float) Math.min(i * AIConfig.pylonDrain, 10000);

				// Sync client
				notifyClient();

				// Skip other positions
				break;
			} else if (!(state.getBlock() instanceof BlockAir)) {
				// ignore
				break;
			}
		}
	}

	@Override
	public void setSingularity(ISingularity singularity) {
		// Set singularity
		operatedTile = singularity;

		// Sync client
		notifyClient();

		// Check tile not resetting
		if (operatedTile != null) {
			// Notify singularity
			operatedTile.addListener(this);
		}

		// Post cell array update
		postCellInventoryEvent();
	}

	@Override
	public void setDrain(boolean newValue) {
		// Make tile consume energy
		this.shouldDrain = newValue;

		// Update time handler
		this.drainHandler.updateData(this.getWorld());
	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {
		// Check if handlers exist
		if (!activeHandlersLoaded) {
			// Create them
			initHandlers();

			// Return null array
			return new ArrayList<>();
		}

		// return empty list
		if (!hasSingularity() || !getGridNode().isActive()) {
			return new ArrayList<>();
		}

		if (operatedTile instanceof TileBlackHole) {
			// We are operating black hole
			return singletonList(activeBlackHoleHandlers.get(iStorageChannel));
		} else {
			// We are operating white hole
			return singletonList(activeWhiteHoleHandlers.get(iStorageChannel));
		}
	}

	public void initHandlers() {
		// Check loaded
		if (activeHandlersLoaded) {
			return;
		}

		// Check has singularity
		if (!hasSingularity()) {
			return;
		}

		// Add handlers
		AIApi.instance().addHandlersForMEPylon(BlackHoleItemHandler.class, WhiteHoleItemHandler.class, AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
		AIApi.instance().addHandlersForMEPylon(BlackHoleFluidHandler.class, WhiteHoleFluidHandler.class, AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
		AIApi.instance().addHandlersForMEPylon(BlackHoleEnergyHandler.class, WhiteHoleEnergyHandler.class, AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));

		// Check if botania loaded
		if (Loader.isModLoaded("botania") && AIConfig.enableManaFeatures) {
			AIApi.instance().addHandlersForMEPylon(BlackHoleManaHandler.class, WhiteHoleManaHandler.class, AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class));
		}

		// Create maps
		activeWhiteHoleHandlers = new LinkedHashMap<>();
		activeBlackHoleHandlers = new LinkedHashMap<>();

		// Iterate over all channels
		for (IStorageChannel chan : AEApi.instance().storage().storageChannels()) {
			try {
				// Get inv
				WhiteHoleSingularityInventoryHandler<?> invWhiteHole = passiveWhiteHoleHandlers.get(chan).newInstance();
				BlackHoleSingularityInventoryHandler<?> invBlackHole = passiveBlackHoleHandlers.get(chan).newInstance();

				// Set singularity
				invWhiteHole.setSingularity(operatedTile);
				invBlackHole.setSingularity(operatedTile);

				// Fill up maps
				activeBlackHoleHandlers.put(chan, invBlackHole);
				activeWhiteHoleHandlers.put(chan, invWhiteHole);
			} catch (Exception e) {
				AILog.info(e.getMessage());
			}
		}

		activeHandlersLoaded = true;
	}

	public boolean hasSingularity() {
		return operatedTile != null;
	}

	public boolean activate(EnumHand hand, EntityPlayer p) {
		return false;
	}

	public float getBeamState() {
		return beamDrain;
	}

	public boolean drainsEnergy() {
		return shouldDrain;
	}

	private EnumFacing getFw() {
		return world.getBlockState(pos).getValue(FACING).rotateY();
	}
}
