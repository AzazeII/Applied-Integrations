package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartModel;
import appeng.me.GridAccessException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import teamroots.embers.api.capabilities.EmbersCapabilities;
import teamroots.embers.api.power.IEmberCapability;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class PartEmberP2PTunnel extends AIPartP2PTunnel<PartEmberP2PTunnel> {
	private static class NullEmberHandler implements IEmberCapability {
		@Override
		public double getEmber() {
			return 0;
		}

		@Override
		public double getEmberCapacity() {
			return 0;
		}

		@Override
		public void setEmber(double v) {

		}

		@Override
		public void setEmberCapacity(double v) {

		}

		@Override
		public double addAmount(double v, boolean b) {
			return 0;
		}

		@Override
		public double removeAmount(double v, boolean b) {
			return 0;
		}

		@Override
		public void writeToNBT(NBTTagCompound nbtTagCompound) {

		}

		@Override
		public void readFromNBT(NBTTagCompound nbtTagCompound) {

		}

		@Override
		public void onContentsChanged() {

		}
	}

	private class OutputEmberHandler implements IEmberCapability {
		@Override
		public double getEmber() {
			return PartEmberP2PTunnel.this.getAdjacentEmberStorage().getEmber();
		}

		@Override
		public double getEmberCapacity() {
			return PartEmberP2PTunnel.this.getAdjacentEmberStorage().getEmberCapacity();
		}

		@Override
		public void setEmber(double v) {

		}

		@Override
		public void setEmberCapacity(double v) {

		}

		@Override
		public double addAmount(double v, boolean b) {
			return 0;
		}

		@Override
		public double removeAmount(double amount, boolean simulate) {
			final double total = PartEmberP2PTunnel.this.getAdjacentEmberStorage().removeAmount( amount, simulate );

			if(!simulate) {
				PartEmberP2PTunnel.this.queueTunnelDrain( PowerUnits.RF, total );
			}

			return total;
		}

		@Override
		public void writeToNBT(NBTTagCompound nbtTagCompound) {

		}

		@Override
		public void readFromNBT(NBTTagCompound nbtTagCompound) {

		}

		@Override
		public void onContentsChanged() {

		}
	}

	private class InputEmberHandler implements IEmberCapability {
		@Override
		public double getEmber() {
			// Total amount stored in each adjacent handler from outputs
			int total = 0;

			try {
				// Iterate for each p2p output
				for( PartEmberP2PTunnel t : PartEmberP2PTunnel.this.getOutputs() ) {
					// Add stored amount from output
					total += t.getAdjacentEmberStorage().getEmber();
				}
			} catch( GridAccessException e ) {
				return 0;
			}

			return total;
		}

		@Override
		public double getEmberCapacity() {
			// Total max amount in each adjacent handler from outputs
			int total = 0;

			try {
				// Iterate for each p2p output
				for( PartEmberP2PTunnel t : PartEmberP2PTunnel.this.getOutputs() ) {
					// Add max amount from output
					total += t.getAdjacentEmberStorage().getEmberCapacity();
				}
			} catch( GridAccessException e ) {
				return 0;
			}

			return total;
		}

		@Override
		public void setEmber(double v) {

		}

		@Override
		public void setEmberCapacity(double v) {

		}

		@Override
		public double addAmount(double amount, boolean simulate) {
			// Total added amount
			int totalReceived = 0;

			try {
				// Get count of output tunnels
				final int outputTunnels = PartEmberP2PTunnel.this.getOutputs().size();

				// Check if amount requested or count of tunnels is zero
				if( outputTunnels == 0 | amount == 0 ) {
					return 0;
				}

				// Get amount for one tunnel
				final double amountPerOutput = amount / outputTunnels;

				// Get overflow amount
				double overflow = amountPerOutput == 0 ? amount : amount % amountPerOutput;

				// Iterate for each channel
				for( PartEmberP2PTunnel target : PartEmberP2PTunnel.this.getOutputs() ) {
					// Get output capability
					final IEmberCapability output = target.getAdjacentEmberStorage();

					// Get amount to send
					final double toSend = amountPerOutput + overflow;

					// Get received amount
					final double received = output.addAmount( toSend, simulate );

					// Recalculate overflow
					overflow = toSend - received;

					// Add to totalReceived
					totalReceived += received;
				}

				// Check if it wasn't simulation
				if( !simulate ) {
					// Drain energy
					PartEmberP2PTunnel.this.queueTunnelDrain( PowerUnits.RF, totalReceived );
				}
			} catch( GridAccessException ignored ) { }

			return totalReceived;
		}

		@Override
		public double removeAmount(double v, boolean b) {
			return 0;
		}

		@Override
		public void writeToNBT(NBTTagCompound nbtTagCompound) {

		}

		@Override
		public void readFromNBT(NBTTagCompound nbtTagCompound) {

		}

		@Override
		public void onContentsChanged() {

		}
	}

	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_EMBER.getFirstModel());

	private static final IEmberCapability NULL_HANDLER = new NullEmberHandler();

	private OutputEmberHandler outputHandler = new OutputEmberHandler();
	private InputEmberHandler inputHandler = new InputEmberHandler();

	public PartEmberP2PTunnel(ItemStack is) {
		super(is);
	}

	@Nonnull
	private IEmberCapability getAdjacentEmberStorage() {
		if( this.isActive() ) {
			final TileEntity self = this.getTile();
			final TileEntity te = self.getWorld().getTileEntity( self.getPos().offset( this.getSide().getFacing() ) );
			if( te != null && te.hasCapability( EmbersCapabilities.EMBER_CAPABILITY, this.getSide().getOpposite().getFacing() ) ) {
				return te.getCapability( EmbersCapabilities.EMBER_CAPABILITY, this.getSide().getOpposite().getFacing() );
			}
		}

		return NULL_HANDLER;
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}

	@Override
	public boolean hasCapability(Capability<?> capability) {
		if (capability == EmbersCapabilities.EMBER_CAPABILITY )
			return true;
		return super.hasCapability(capability);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability) {
		if (capability == EmbersCapabilities.EMBER_CAPABILITY ) {
			if (this.isOutput()) {
				return (T) this.outputHandler;
			}

			return (T) this.inputHandler;
		}
		return super.getCapability(capability);
	}

}
