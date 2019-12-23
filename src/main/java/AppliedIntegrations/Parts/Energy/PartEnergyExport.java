package AppliedIntegrations.Parts.Energy;


import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class PartEnergyExport extends AIOPart {

	public PartEnergyExport() {
		super(PartEnum.EnergyExportBus);
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(4, 4, 12, 12, 12, 14);
		bch.addBox(5, 5, 14, 11, 11, 15);
		bch.addBox(6, 6, 15, 10, 10, 16);
		bch.addBox(6, 6, 11, 10, 10, 12);
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public TickRateModulation doWork(int valuedTransfer, IGridNode node) {
		// Create helper
		CapabilityHelper helper = new CapabilityHelper(adjacentEnergyStorage, getHostSide().getOpposite());

		// Try to extract all filtered energies from network and put them nto container next to us
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (filteredEnergies.contains(energy)) {
				if (helper.operatesEnergy(energy)) {
					int extracted = extractEnergy(new EnergyStack(energy, valuedTransfer), Actionable.SIMULATE);
					int received = helper.receiveEnergy(extracted, true, energy);

					// Modulate extraction & insertion
					extractEnergy(new EnergyStack(energy, helper.receiveEnergy(received, false, energy)), Actionable.MODULATE);

					if (extracted > 0) {
						// tick faster after successfull energy extraction
						return TickRateModulation.FASTER;
					}
				}
			}
		}

		// Tick slower
		return TickRateModulation.SLOWER;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {

		return 0;
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {

		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.EXPORT_HAS_CHANNEL;
			} else {
				return PartModelEnum.EXPORT_ON;
			}
		}
		return PartModelEnum.EXPORT_OFF;
	}
}
