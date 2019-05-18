package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartModel;
import appeng.me.GridAccessException;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;
import vazkii.botania.api.mana.IManaReceiver;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
@Optional.Interface(iface = "vazkii.botania.api.mana.IManaReceiver", modid = "botania", striprefs = true)
public class PartManaP2PTunnel extends AIPartP2PTunnel<PartManaP2PTunnel> implements IBotaniaIntegrated, IManaReceiver {
	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_MANA.getFirstModel());

	private static final IManaReceiver NULL_HANDLER = new IManaReceiver() {
		@Override
		public int getCurrentMana() {
			return 0;
		}

		@Override
		public boolean isFull() {
			return false;
		}

		@Override
		public void recieveMana(int mana) {

		}

		@Override
		public boolean canRecieveManaFromBursts() {
			return false;
		}
	};

	public PartManaP2PTunnel(ItemStack is) {
		super(is);
	}

	// Same as receiveMana, but returns received amount
	private double receiveManaWithDiff(IManaReceiver output, double toSend) {
		// Get initial mana amount
		int initialMana = output.getCurrentMana();

		// Receive mana
		output.recieveMana((int) toSend);

		// Get new mana amount
		int newMana = output.getCurrentMana();

		// Return diff between new and initial mana amount
		return newMana - initialMana;
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}

	@Override
	public boolean isFull() {
		// Check if part isn't output
		if (!PartManaP2PTunnel.this.isOutput()) {
			try {
				// Iterate for each p2p output
				for (PartManaP2PTunnel t : PartManaP2PTunnel.this.getOutputs()) {
					// Check if storage isn't full
					if (t.getAdjacentManaStorage().isFull())
						return true;
				}
			} catch (GridAccessException ignored) { }

			return false;
		} else{
			// Pass call to handler
			return PartManaP2PTunnel.this.getAdjacentManaStorage().isFull();
		}
	}

	@Override
	public void recieveMana(int mana) {
		// Check if part isn't output
		if (!PartManaP2PTunnel.this.isOutput()) {
			// Total added amount
			int totalReceived = 0;

			try {
				// Get count of output tunnels
				final int outputTunnels = PartManaP2PTunnel.this.getOutputs().size();

				// Check if amount requested or count of tunnels is zero
				if( outputTunnels == 0 | mana == 0 ) {
					return;
				}

				// Get amount for one tunnel
				final double amountPerOutput = mana / outputTunnels;

				// Get overflow amount
				double overflow = amountPerOutput == 0 ? mana : mana % amountPerOutput;

				// Iterate for each channel
				for( PartManaP2PTunnel target : PartManaP2PTunnel.this.getOutputs() ) {
					// Get output capability
					final IManaReceiver output = target.getAdjacentManaStorage();

					// Get amount to send
					final double toSend = amountPerOutput + overflow;

					// Get received amount
					final double received = receiveManaWithDiff(output, toSend);

					// Recalculate overflow
					overflow = toSend - received;

					// Add to totalReceived
					totalReceived += received;
				}

				// Drain energy
				PartManaP2PTunnel.this.queueTunnelDrain( PowerUnits.RF, totalReceived );
			} catch( GridAccessException ignored ) { }
		}else {
			// Pass call to handler
			PartManaP2PTunnel.this.getAdjacentManaStorage().recieveMana(mana);
		}
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		// Check if part isn't output
		if (!PartManaP2PTunnel.this.isOutput()) {
			try {
				// Iterate for each p2p output
				for (PartManaP2PTunnel t : PartManaP2PTunnel.this.getOutputs()) {
					// Check if storage can receive mana
					if (t.getAdjacentManaStorage().canRecieveManaFromBursts())
						return true;
				}
			} catch (GridAccessException ignored) { }

			return false;
		} else{
			// Pass call to handler
			return PartManaP2PTunnel.this.getAdjacentManaStorage().canRecieveManaFromBursts();
		}
	}

	@Override
	public int getCurrentMana() {
		// Check if part isn't output
		if (!PartManaP2PTunnel.this.isOutput()) {
			// Total amount stored in each adjacent handler from outputs
			int total = 0;

			try {
				// Iterate for each p2p output
				for (PartManaP2PTunnel t : PartManaP2PTunnel.this.getOutputs()) {
					// Add stored amount from output
					total += t.getAdjacentManaStorage().getCurrentMana();
				}
			} catch (GridAccessException e) {
				return 0;
			}

			return total;
		} else {
			// Pass call to handler
			return PartManaP2PTunnel.this.getAdjacentManaStorage().getCurrentMana();
		}
	}

	@Nonnull
	private IManaReceiver getAdjacentManaStorage() {
		// Check if part is active
		if( this.isActive() ) {
			// Get self
			final TileEntity self = this.getTile();

			// Get facing tile
			final TileEntity te = self.getWorld().getTileEntity( self.getPos().offset( this.getSide().getFacing() ) );

			// Check if facing tile is mana receiver
			if( te instanceof IManaReceiver) {
				return (IManaReceiver) te;
			}
		}

		// Null handler
		return NULL_HANDLER;
	}
}
