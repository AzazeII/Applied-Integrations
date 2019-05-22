package AppliedIntegrations.api.Multiblocks;


import AppliedIntegrations.Blocks.BlockAIRegistrable;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.EnumFacing.Axis.*;

/**
 * @Author Azazell
 */
public class BlockData {
	public byte x, y, z;

	public int meta;

	public BlockType type;

	public List<Block> options;

	public BlockData(int x, int y, int z, Block b, BlockType type) {
		this(x, y, z, type, b);
	}

	public BlockData(int x, int y, int z, BlockType type, Block... blockOptions) {
		this.options = Arrays.asList(blockOptions);
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
		this.type = type;
	}

	public BlockData(int x, int y, int z, Block b) {
		this(x, y, z, b, b);
	}

	public BlockData(int x, int y, int z, Block... blockOptions) {

		this.options = Arrays.asList(blockOptions);
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
	}

	public BlockData(int x, int y, int z, List<Block> options) {
		this.options = options;
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
	}

	public BlockData(BlockPos size, BlockAIRegistrable b) {
		this(size.getX(), size.getY(), size.getZ(), b);
	}

	public BlockPos getPos() {

		return new BlockPos(x, y, z);
	}

	public boolean isPropertyOfSide(EnumFacing facing) {
		// Get axis of facing
		EnumFacing.Axis axis = facing.getAxis();

		// Check if axis is X
		if (axis == X) {
			// Check if our Y and Z is zero
			if (y != 0 || z != 0)
				return false;

			// Check if our X isn't zero
			return facing.getAxisDirection().getOffset() == 1 ? x >= 1 : x <= -1;
		}

		// Check if axis is Y
		else if (axis == Y) {
			// Check if our X and Z is zero
			if (x != 0 || z != 0)
				return false;

			// Check if our Y isn't zero
			return facing.getAxisDirection().getOffset() == 1 ? y >= 1 : y <= -1;
		}

		// Check if axis is Z
		else if (axis == Z) {
			// Check if our X and Y is zero
			if (x != 0 || y != 0)
				return false;

			// Check if our Z isn't zero
			return facing.getAxisDirection().getOffset() == 1 ? z >= 1 : z <= -1;
		}

		return true;
	}

	public BlockData offset(EnumFacing facing) {
		// Add offset to our position
		this.x += facing.getFrontOffsetX(); // X
		this.y += facing.getFrontOffsetY(); // Y
		this.z += facing.getFrontOffsetZ(); // Z

		return this;
	}

	public BlockData offset(EnumFacing facing, Integer length) {
		// Iterate until i = length
		for (int i = 0; i < length; i++){
			// Offset this
			offset(facing);
		}

		return this;
	}

	public BlockData inverse() {
		return new BlockData(x * -1, y * -1, z * -1, options);
	}
}
