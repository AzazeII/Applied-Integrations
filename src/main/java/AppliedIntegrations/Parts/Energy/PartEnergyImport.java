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
		// Create helper
		CapabilityHelper helper = new CapabilityHelper(adjacentEnergyStorage, getHostSide().getOpposite());

		// Iterate over all energies
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Check if filter contains any values
			if (filteredEnergies.size() > 0)
			// Check if filter not contains current energy
			{
				if (!filteredEnergies.contains(energy)) {
					continue;
				}
			}

			// Check if tile can operate given energy
			if (helper.operatesEnergy(energy)) {

				// Simulate injection
				int injected = injectEnergy(new EnergyStack(energy, valuedTransfer), Actionable.SIMULATE);

				// Create helper
				helper.extractEnergy(injected, false, energy);

				// Modulate injection
				injectEnergy(new EnergyStack(energy, injected), Actionable.MODULATE);

				// Check if energy was actually injected
				if (injected > 0)
				// Tick faster
				{
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

	private int getMaxTransfer() {

		return 500;
	}

	private LiquidAIEnergy getFilteredEnergy() {

		return null;
	}
}
