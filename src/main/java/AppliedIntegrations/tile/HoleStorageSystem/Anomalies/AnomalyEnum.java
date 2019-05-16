package AppliedIntegrations.tile.HoleStorageSystem.Anomalies;

import AppliedIntegrations.Blocks.Additions.BlockWhiteHole;
import AppliedIntegrations.Helpers.Energy.CapabilityHelper;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnumCapabilityType;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import appeng.api.config.Actionable;
import appeng.api.util.AEPartLocation;
import net.minecraft.block.Block;
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
		// Get positions in range
		List<BlockPos> positions = t.getBlocksInRadius(t.getMaxDestructionRange() * 1.5);


		// Iterate over all positions
		for (BlockPos pos : positions) {
			// Get tile entity
			TileEntity tile = t.getWorld().getTileEntity(pos);

			// Check not null
			if (tile == null) {
				// Skip
				continue;
			}

			// Does tile has any capability, linked with energy?
			boolean hasCapabilityHandled = false;

			// Iterate over all types
			for (EnumCapabilityType type : EnumCapabilityType.values) {
				// Get output capability
				Capability capability = type.getOutputCapabilities();

				// Iterate over all sides
				for (EnumFacing facing : EnumFacing.values()) {
					// Check if tile has this capability
					if (tile.hasCapability(capability, facing)) {
						// Set to true
						hasCapabilityHandled = true;

						// Break
						break;
					}
				}
			}

			if (hasCapabilityHandled) {
				// Iterate over all sides
				for (EnumFacing facing : EnumFacing.values()) {
					// Get helper
					CapabilityHelper helper = new CapabilityHelper(tile, AEPartLocation.fromFacing(facing));

					// TODO: 2019-03-26 Add not only RF
					// Extract all energy types from this tile
					t.addStack(AEEnergyStack.fromStack(new EnergyStack(RF, helper.extractAllStored(22000))), Actionable.MODULATE);
				}
			}
		}
	}),

	// Entangles two holes together
	EntangleHoles((t) -> {
		// Get positions
		List<BlockPos> positions = t.getBlocksInRadius(t.getMaxDestructionRange() * 1.5);

		// Check not entangled yet
		if (t.isEntangled()) {
			return;
		}

		// Iterate over all positions
		for (BlockPos pos : positions) {
			// Get block
			Block block = t.getWorld().getBlockState(pos).getBlock();

			// Check if block is white hole
			if (block instanceof BlockWhiteHole) {
				AILog.chatLog("Found white hole");
				// Get white hole
				TileWhiteHole whiteHole = (TileWhiteHole) t.getWorld().getTileEntity(pos);

				// Check not null
				if (whiteHole == null) {
					continue;
				}

				// Check if white and black hole not entangled yet
				if (whiteHole.isEntangled())
				// break
				{
					continue;
				}

				// Entangle
				whiteHole.setEntangledHole(t);
				t.setEntangledHole(whiteHole);
			}
		}
	}),

	// Shifts block's entropy
	EntropyShift((t) -> {
		// Get list of blocks in square on range
		List<BlockPos> positions = t.getBlocksInRadius(t.getMaxDestructionRange() * 1.5);

		// Iterate over all positions
		positions.forEach((pos) -> {
			// Get block
			IBlockState b = t.getWorld().getBlockState(pos);

			// Check contains
			if (entropyMap.keySet().contains(b)) {
				// Transform block
				t.getWorld().setBlockState(pos, entropyMap.get(b));

				// Add 10 mass for energy eating
				t.addMass(10);

				// Spawn particles on client side
				if (t.getWorld().isRemote)
				// Spawn smoke
				{
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
