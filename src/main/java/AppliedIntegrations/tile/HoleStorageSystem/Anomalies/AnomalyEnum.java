package AppliedIntegrations.tile.HoleStorageSystem.Anomalies;
import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnumCapabilityType;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import appeng.api.config.Actionable;
import appeng.api.util.AEPartLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;
import java.util.function.Consumer;

import static AppliedIntegrations.grid.Implementation.AIEnergy.RF;
import static AppliedIntegrations.tile.HoleStorageSystem.Anomalies.EntropyTransformations.entropyMap;

/**
 * @Author Azazell
 */
public enum AnomalyEnum {
	// Consumes all energy from machines in range
	EMP((t) -> {
		List<BlockPos> positions = t.getBlocksInRadius(t.getMaxDestructionRange() * 1.5);
		for (BlockPos pos : positions) {
			TileEntity tile = t.getWorld().getTileEntity(pos);
			if (tile == null) {
				continue;
			}

			boolean hasCapabilityHandled = false;
			for (EnumCapabilityType type : EnumCapabilityType.values) {
				Capability capability = type.getOutputCapabilities();

				for (EnumFacing facing : EnumFacing.values()) {
					if (tile.hasCapability(capability, facing)) {
						hasCapabilityHandled = true;
						break;
					}
				}
			}

			if (hasCapabilityHandled) {
				for (EnumFacing facing : EnumFacing.values()) {
					CapabilityHelper helper = new CapabilityHelper(tile, AEPartLocation.fromFacing(facing));

					// TODO: 2019-03-26 Add not only RF
					// Extract all energy types from this tile
					t.addStack(AEEnergyStack.fromStack(new EnergyStack(RF, helper.extractAllStored(22000))), Actionable.MODULATE);
				}
			}
		}
	}),

	// Shifts block's entropy
	EntropyShift((t) -> {
		List<BlockPos> positions = t.getBlocksInRadius(t.getMaxDestructionRange() * 1.5);
		positions.forEach((pos) -> {
			IBlockState b = t.getWorld().getBlockState(pos);
			if (entropyMap.keySet().contains(b)) {
				t.getWorld().setBlockState(pos, entropyMap.get(b));
				t.addMass(10);

				if (t.getWorld().isRemote) {
					t.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ(), 0, 0.1, 0, 0);
				}
			}
		});
	});

	public Consumer<TileBlackHole> action;

	// Action method
	AnomalyEnum(Consumer<TileBlackHole> action) {
		this.action = action;
	}
}
