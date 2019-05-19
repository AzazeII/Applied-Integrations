package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Parts.PartModelEnum;
import WayofTime.bloodmagic.demonAura.WorldDemonWillHandler;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.soul.IDemonWillConduit;
import WayofTime.bloodmagic.tile.TileDemonPylon;
import appeng.api.config.PowerUnits;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartModel;
import appeng.me.GridAccessException;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class PartWillP2PTunnel extends AIPartP2PTunnel<PartWillP2PTunnel> implements IGridTickable {
	private class InputWillHandler implements IDemonWillConduit {
		@Override
		public int getWeight() {
			// Total weight amount in each adjacent handler from outputs
			int total = 0;

			try {
				// Iterate for each p2p output
				for (PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs()) {
					// Check not null
					if (t.getAdjacentWillHandler() != null) {
						// Add weight amount from output
						total += t.getAdjacentWillHandler().getWeight();
					}
				}
			} catch (GridAccessException e) {
				return 0;
			}

			return total;
		}

		@Override
		public double fillDemonWill(EnumDemonWillType type, double amount, boolean doFill) {
			// Total added amount
			int totalReceived = 0;

			try {
				// Get count of output tunnels
				final int outputTunnels = PartWillP2PTunnel.this.getOutputs().size();

				// Check if amount requested or count of tunnels is zero
				if (outputTunnels == 0 | amount == 0) {
					return 0;
				}

				// Get amount for one tunnel
				final double amountPerOutput = amount / outputTunnels;

				// Get overflow amount
				double overflow = amountPerOutput == 0 ? amount : amount % amountPerOutput;

				// Iterate for each channel
				for (PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs()) {
					// Get output conduit
					TileDemonPylon output = t.getAdjacentWillHandler();

					// Check not null
					if (output == null)
						continue;

					// Get amount to send
					double toSend = amountPerOutput + overflow;

					// Get received amount
					double received = WorldDemonWillHandler.fillWill(output.getWorld(), output.getPos(), type, toSend, doFill);

					// Recalculate overflow
					overflow = toSend - received;

					// Add to totalReceived
					totalReceived += received;
				}

				// Check if it wasn't simulation
				if (doFill) {
					// Drain energy
					PartWillP2PTunnel.this.queueTunnelDrain(PowerUnits.AE, totalReceived);
				}
			} catch (GridAccessException ignored) {
			}

			return totalReceived;
		}

		@Override
		public double drainDemonWill(EnumDemonWillType type, double amount, boolean doDrain) {
			return 0;
		}

		@Override
		public boolean canFill(EnumDemonWillType type) {
			return true;
		}

		@Override
		public boolean canDrain(EnumDemonWillType type) {
			return false;
		}

		@Override
		public double getCurrentWill(EnumDemonWillType type) {
			// Total will amount in each adjacent handler from outputs
			int total = 0;

			try {
				// Iterate for each p2p output
				for (PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs()) {
					// Get adjacent pylon-handler
					TileDemonPylon pylon = t.getAdjacentWillHandler();

					// Check not null
					if (pylon == null)
						continue;

					// Get current will and add to total
					total += WorldDemonWillHandler.getCurrentWill(pylon.getWorld(), pylon.getPos(), type);
				}
			} catch (GridAccessException e) {
				return 0;
			}

			return total;
		}
	}

	private IDemonWillConduit inputHandler = new InputWillHandler();

	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_WILL.getFirstModel());

	public PartWillP2PTunnel(ItemStack is) {
		super(is);
	}

	private TileDemonPylon getAdjacentWillHandler() {
		// Check if part is active
		if (this.isActive()) {
			// Get self
			final TileEntity self = this.getTile();

			// Get facing tile
			final TileEntity te = self.getWorld().getTileEntity(self.getPos().offset(this.getSide().getFacing()));

			// Check if facing tile is will conduit
			if (te instanceof TileDemonPylon) {
				return (TileDemonPylon) te;
			}
		}

		return null;
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel(this.isPowered(), this.isActive());
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(2, 2, false,false);
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		// Check if p2p tunnel is input
		if (!isOutput()) {
			// Get pylon
			TileDemonPylon pylon = getAdjacentWillHandler();

			// Check not null
			if (pylon == null) {
				return TickRateModulation.SAME;
			}

			// Iterate for each will type
			for (EnumDemonWillType will : EnumDemonWillType.values()) {
				// Simulate drain from chunk
				double drain = WorldDemonWillHandler.drainWill(pylon.getWorld(), pylon.getPos(), will,1D, false);

				// Modulate fill to chunk
				double fill = inputHandler.fillDemonWill(will, drain, true);

				// Drain amount filled
				WorldDemonWillHandler.drainWill(pylon.getWorld(), pylon.getPos(), will,fill, true);
			}
		}

		return TickRateModulation.SAME;
	}

	public IDemonWillConduit getInputHandler() {
		return inputHandler;
	}
}
