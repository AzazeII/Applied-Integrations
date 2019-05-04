package AppliedIntegrations.api.Multiblocks;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 */
public class BlockData {
    public byte x, y, z;
    public Block b;
    public int meta;
    public BlockType type;

    public BlockData(int x, int y, int z, Block b, BlockType type)
    {
        this(x,y,z,b);
        this.type = type;
    }

    public BlockData(int x, int y, int z, Block b)
    {
        this.x = (byte)x;
        this.y = (byte)y;
        this.z = (byte)z;
        this.b = b;
    }
    public BlockData(int x, int y, int z, Block b, int meta)
    {
        this(x,y,z,b);
        this.meta = meta;
    }

    public BlockPos getPos() {
        return new BlockPos(x, y, z);
    }
}
