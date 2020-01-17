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
		int initialMana = output.getCurrentMana();
		output.recieveMana((int) toSend);
		int newMana = output.getCurrentMana();
		return newMana - initialMana;
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}

	@Override
	public boolean isFull() {
		if (!PartManaP2PTunnel.this.isOutput()) {
			try {
				for (PartManaP2PTunnel t : PartManaP2PTunnel.this.getOutputs()) {
					if (t.getAdjacentManaStorage().isFull())
						return true;
				}
			} catch (GridAccessException ignored) { }

			return false;
		} else{
			return PartManaP2PTunnel.this.getAdjacentManaStorage().isFull();
		}
	}

	@Override
	public void recieveMana(int mana) {
		if (!PartManaP2PTunnel.this.isOutput()) {
			// Split mana equally on all outputs making p2p tunnel a perfect way to split mana
			int totalReceived = 0;

			try {
				final int outputTunnels = PartManaP2PTunnel.this.getOutputs().size();
				if( outputTunnels == 0 | mana == 0 ) {
					return;
				}

				final double amountPerOutput = (double) mana / (double) outputTunnels;
				double overflow = amountPerOutput == 0 ? mana : mana % amountPerOutput;
				for( PartManaP2PTunnel target : PartManaP2PTunnel.this.getOutputs() ) {
					final IManaReceiver output = target.getAdjacentManaStorage();
					final double toSend = amountPerOutput + overflow;
					final double received = receiveManaWithDiff(output, toSend);
					overflow = toSend - received;
					totalReceived += received;
				}

				PartManaP2PTunnel.this.queueTunnelDrain( PowerUnits.RF, totalReceived );
			} catch( GridAccessException ignored ) { }
		} else {
			// Pass call to handler
			PartManaP2PTunnel.this.getAdjacentManaStorage().recieveMana(mana);
		}
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		if (!PartManaP2PTunnel.this.isOutput()) {
			try {
				for (PartManaP2PTunnel t : PartManaP2PTunnel.this.getOutputs()) {
					if (t.getAdjacentManaStorage().canRecieveManaFromBursts())
						return true;
				}
			} catch (GridAccessException ignored) { }

			return false;
		} else{
			return PartManaP2PTunnel.this.getAdjacentManaStorage().canRecieveManaFromBursts();
		}
	}

	@Override
	public int getCurrentMana() {
		if (!PartManaP2PTunnel.this.isOutput()) {
			int total = 0;

			try {
				for (PartManaP2PTunnel t : PartManaP2PTunnel.this.getOutputs()) {
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
		if( this.isActive() ) {
			final TileEntity self = this.getTile();
			final TileEntity te = self.getWorld().getTileEntity( self.getPos().offset( this.getSide().getFacing() ) );

			if( te instanceof IManaReceiver) {
				return (IManaReceiver) te;
			}
		}

		return NULL_HANDLER;
	}
}
