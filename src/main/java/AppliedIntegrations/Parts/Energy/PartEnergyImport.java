package AppliedIntegrations.Parts.Energy;
import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.IterableHelpers;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * @Author Azazell
 */
public class PartEnergyImport extends AIOPart {
	public PartEnergyImport() {

		super(PartEnum.EnergyImportBus);
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(6, 6, 11, 10, 10, 13);
		bch.addBox(5, 5, 13, 11, 11, 14);
		bch.addBox(4, 4, 14, 12, 12, 16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos pos, Random r) {

	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public TickRateModulation doWork(int valuedTransfer, IGridNode node) {
		CapabilityHelper helper = new CapabilityHelper(adjacentEnergyStorage, getHostSide().getOpposite());

		// Importing energy from world into network
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (!IterableHelpers.containsOnlyNulls(filteredEnergies)) {
				if (!filteredEnergies.contains(energy)) {
					continue;
				}
			}

			if (helper.operatesEnergy(energy)) {
				int injected = injectEnergy(new EnergyStack(energy, valuedTransfer), Actionable.SIMULATE);
				int extracted = helper.extractEnergy(injected, false, energy);
				injectEnergy(new EnergyStack(energy, extracted), Actionable.MODULATE);

				if (injected > 0) {
					// Speed up if energy was injected
					return TickRateModulation.FASTER;
				}
			}
		}

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
				return PartModelEnum.IMPORT_HAS_CHANNEL;
			} else {
				return PartModelEnum.IMPORT_ON;
			}
		}
		return PartModelEnum.IMPORT_OFF;
	}
}
