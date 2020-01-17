package AppliedIntegrations.api.Multiblocks;


import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

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
		this(x, y, z, Arrays.asList(blockOptions));
		this.type = type;
	}

	public BlockData(int x, int y, int z, Block b) {
		this(x, y, z, b, b);
	}

	public BlockData(int x, int y, int z, Block... blockOptions) {
		this(x, y, z, Arrays.asList(blockOptions));
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
		this.x += facing.getFrontOffsetX();
		this.y += facing.getFrontOffsetY();
		this.z += facing.getFrontOffsetZ();

		return this;
	}

	public BlockData offset(EnumFacing facing, Integer length) {
		for (int i = 0; i < length; i++){
			offset(facing);
		}

		return this;
	}

	public BlockData inverse() {
		return new BlockData(x * -1, y * -1, z * -1, options);
	}
}
