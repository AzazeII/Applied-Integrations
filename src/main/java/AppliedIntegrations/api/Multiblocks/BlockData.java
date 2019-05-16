package AppliedIntegrations.api.Multiblocks;

import net.minecraft.block.Block;
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

	public BlockPos getPos() {
		return new BlockPos(x, y, z);
	}
}
