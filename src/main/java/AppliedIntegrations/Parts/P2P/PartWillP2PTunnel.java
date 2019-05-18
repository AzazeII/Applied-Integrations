package AppliedIntegrations.Parts.P2P;


import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.soul.IDemonWillConduit;
import appeng.api.config.PowerUnits;
import appeng.me.GridAccessException;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class PartWillP2PTunnel extends AIPartP2PTunnel<PartWillP2PTunnel> implements IDemonWillConduit {
	private class InputWillHandler implements IDemonWillConduit{
		@Override
		public int getWeight() {
			// Total weight amount in each adjacent handler from outputs
			int total = 0;

			try {
				// Iterate for each p2p output
				for( PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs() ) {
					// Add weight amount from output
					total += t.getAdjacentWillHandler().getWeight();
				}
			} catch( GridAccessException e ) {
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
				if( outputTunnels == 0 | amount == 0 ) {
					return 0;
				}

				// Get amount for one tunnel
				final double amountPerOutput = amount / outputTunnels;

				// Get overflow amount
				double overflow = amountPerOutput == 0 ? amount : amount % amountPerOutput;

				// Iterate for each channel
				for( PartWillP2PTunnel target : PartWillP2PTunnel.this.getOutputs() ) {
					// Get output conduit
					final IDemonWillConduit output = target.getAdjacentWillHandler();

					// Get amount to send
					final double toSend = amountPerOutput + overflow;

					// Get received amount
					final double received = output.fillDemonWill( type, toSend, doFill);

					// Recalculate overflow
					overflow = toSend - received;

					// Add to totalReceived
					totalReceived += received;
				}

				// Check if it wasn't simulation
				if( doFill ) {
					// Drain energy. Normal drain * 2 * 3 ((RF -> AE) * (1 -> 3))
					PartWillP2PTunnel.this.queueTunnelDrain( PowerUnits.AE, totalReceived);
				}
			} catch( GridAccessException ignored ) { }

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
				for( PartWillP2PTunnel t : PartWillP2PTunnel.this.getOutputs() ) {
					// Add will amount from output
					total += t.getAdjacentWillHandler().getCurrentWill(type);
				}
			} catch( GridAccessException e ) {
				return 0;
			}

			return total;
		}
	}

	private class OutputWillHandler implements IDemonWillConduit{

		@Override
		public int getWeight() {
			return getAdjacentWillHandler().getWeight();
		}

		@Override
		public double fillDemonWill(EnumDemonWillType type, double amount, boolean doFill) {
			return getAdjacentWillHandler().fillDemonWill(type, amount, doFill);
		}

		@Override
		public double drainDemonWill(EnumDemonWillType type, double amount, boolean doDrain) {
			return 0; // Ignored since it's output handler
		}

		@Override
		public boolean canFill(EnumDemonWillType type) {
			return true;
		}

		@Override
		public boolean canDrain(EnumDemonWillType type) {
			return false; // Ignored since it's output handler

		}

		@Override
		public double getCurrentWill(EnumDemonWillType type) {
			return getAdjacentWillHandler().getCurrentWill(type);
		}
	}

	private static final IDemonWillConduit NULL_HANDLER = new IDemonWillConduit() {
		@Override
		public int getWeight() {
			return 0;
		}

		@Override
		public double fillDemonWill(EnumDemonWillType type, double amount, boolean doFill) {
			return 0;
		}

		@Override
		public double drainDemonWill(EnumDemonWillType type, double amount, boolean doDrain) {
			return 0;
		}

		@Override
		public boolean canFill(EnumDemonWillType type) {
			return false;
		}

		@Override
		public boolean canDrain(EnumDemonWillType type) {
			return false;
		}

		@Override
		public double getCurrentWill(EnumDemonWillType type) {
			return 0;
		}
	};

	private IDemonWillConduit inputHandler = new InputWillHandler();
	private IDemonWillConduit outputHandler = new OutputWillHandler();

	public PartWillP2PTunnel(ItemStack is) {
		super(is);
	}

	private IDemonWillConduit getWillHandler() {
		// Check if tunnel is output
		if (isOutput())
			return outputHandler;
		else
			return inputHandler;
	}

	@Nonnull
	private IDemonWillConduit getAdjacentWillHandler() {
		// Check if part is active
		if( this.isActive() ) {
			// Get self
			final TileEntity self = this.getTile();

			// Get facing tile
			final TileEntity te = self.getWorld().getTileEntity( self.getPos().offset( this.getSide().getFacing() ) );

			// Check if facing tile is will conduit
			if( te instanceof IDemonWillConduit ) {
				return (IDemonWillConduit) te;
			}
		}

		// Null handler
		return NULL_HANDLER;
	}

	@Override
	public int getWeight() {
		return getWillHandler().getWeight();
	}

	@Override
	public double fillDemonWill(EnumDemonWillType type, double amount, boolean doFill) {
		return getWillHandler().fillDemonWill(type, amount, doFill);
	}

	@Override
	public double drainDemonWill(EnumDemonWillType type, double amount, boolean doDrain) {
		return getWillHandler().drainDemonWill(type, amount, doDrain);
	}

	@Override
	public boolean canFill(EnumDemonWillType type) {
		return getWillHandler().canFill(type);
	}

	@Override
	public boolean canDrain(EnumDemonWillType type) {
		return getWillHandler().canDrain(type);

	}

	@Override
	public double getCurrentWill(EnumDemonWillType type) {
		return getWillHandler().getCurrentWill(type);
	}
}
