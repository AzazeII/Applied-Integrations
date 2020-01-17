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
			int total = 0;

			try {
				for (PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs()) {
					if (t.getAdjacentWillHandler() != null) {
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
			int totalReceived = 0;

			try {
				// Split energy equally on all output tunnels
				final int outputTunnels = PartWillP2PTunnel.this.getOutputs().size();
				if (outputTunnels == 0 | amount == 0) {
					return 0;
				}

				final double amountPerOutput = amount / outputTunnels;
				double overflow = amountPerOutput == 0 ? amount : amount % amountPerOutput;

				for (PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs()) {
					TileDemonPylon output = t.getAdjacentWillHandler();

					if (output == null)
						continue;

					double toSend = amountPerOutput + overflow;
					double received = WorldDemonWillHandler.fillWill(output.getWorld(), output.getPos(), type, toSend, doFill);

					overflow = toSend - received;
					totalReceived += received;
				}

				if (doFill) {
					PartWillP2PTunnel.this.queueTunnelDrain(PowerUnits.AE, type != EnumDemonWillType.DEFAULT ? totalReceived * 2 : totalReceived);
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
				for (PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs()) {
					TileDemonPylon pylon = t.getAdjacentWillHandler();

					if (pylon == null)
						continue;

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
		if (this.isActive()) {
			final TileEntity self = this.getTile();
			final TileEntity te = self.getWorld().getTileEntity(self.getPos().offset(this.getSide().getFacing()));
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
		if (!isOutput()) {
			TileDemonPylon pylon = getAdjacentWillHandler();
			if (pylon == null) {
				return TickRateModulation.SAME;
			}

			for (EnumDemonWillType will : EnumDemonWillType.values()) {
				double drain = WorldDemonWillHandler.drainWill(pylon.getWorld(), pylon.getPos(), will,1D, false);
				double fill = inputHandler.fillDemonWill(will, drain, true);
				WorldDemonWillHandler.drainWill(pylon.getWorld(), pylon.getPos(), will,fill, true);
			}
		}

		return TickRateModulation.SAME;
	}

	public IDemonWillConduit getInputHandler() {
		return inputHandler;
	}
}
