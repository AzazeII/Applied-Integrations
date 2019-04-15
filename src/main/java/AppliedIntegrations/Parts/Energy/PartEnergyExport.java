package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.CapabilityHelper;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AECableType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class PartEnergyExport extends AIOPart {

	public PartEnergyExport() {
		super(PartEnum.EnergyExportBus, SecurityPermissions.EXTRACT);
	}

	@Override
	public boolean energyTransferAllowed(LiquidAIEnergy Energy) {
		return true;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {

		bch.addBox( 4, 4, 12, 12, 12, 14 );
		bch.addBox( 5, 5, 14, 11, 11, 15 );
		bch.addBox( 6, 6, 15, 10, 10, 16 );
		bch.addBox( 6, 6, 11, 10, 10, 12 );
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered())
			if (this.isActive())
				return PartModelEnum.EXPORT_HAS_CHANNEL;
			else
				return PartModelEnum.EXPORT_ON;
		return PartModelEnum.EXPORT_OFF;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 0;
	}

	@Override
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType )
	{
		// Call super
		super.writeToNBT( data, saveType );

		boolean doSave = ( saveType == PartItemStack.WORLD );
		if( !doSave )
		{
			// Are there any filters?
			for( LiquidAIEnergy energy : this.filteredEnergies )
			{
				if( energy != null )
				{
					// Only save the void stateProp if filters are set.
					doSave = true;
					break;
				}
			}
		}

	}

	@Override
	public TickRateModulation doWork(int valuedTransfer, IGridNode node) {
		// Create helper
		CapabilityHelper helper = new CapabilityHelper(adjacentEnergyStorage, getSide().getOpposite());

		// Iterate over all energies
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()){

			// Check if we are filtering this energy
			if(filteredEnergies.contains(energy)) {

				// Check if tile can operate given energy
				if(helper.operatesEnergy(energy)) {

					// Simulate extraction
					int extracted = ExtractEnergy(new EnergyStack(energy, valuedTransfer), Actionable.SIMULATE);

					// Create helper
					helper.receiveEnergy(extracted, false, energy);

					// Modulate extraction
					ExtractEnergy(new EnergyStack(energy, extracted), Actionable.MODULATE);

					// Check if energy was actually extracted
					if(extracted > 0)
						// Tick faster
						return TickRateModulation.FASTER;
				}
			}
		}

		return TickRateModulation.SLOWER;
	}
}
