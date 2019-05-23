package AppliedIntegrations.api.Multiblocks;


import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static scala.actors.threadpool.Arrays.asList;

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

	@SuppressWarnings("unchecked")
	public BlockData(int x, int y, int z, BlockType type, Block... blockOptions) {
		this(x, y, z, asList(blockOptions));
		this.type = type;
	}

	public BlockData(int x, int y, int z, Block b) {
		this(x, y, z, b, b);
	}

	@SuppressWarnings("unchecked")
	public BlockData(int x, int y, int z, Block... blockOptions) {
		this(x, y, z, asList(blockOptions));
	}

	public BlockData(int x, int y, int z, List<Block> options) {
		this.options = options;
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
	}

	public BlockData(BlockPos size, Block... options) {
		this(size.getX(), size.getY(), size.getZ(), options);
	}

	public BlockPos getPos() {

		return new BlockPos(x, y, z);
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
