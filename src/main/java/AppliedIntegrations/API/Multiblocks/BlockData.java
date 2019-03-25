package AppliedIntegrations.API.Multiblocks;

import net.minecraft.block.Block;

/**
 * @Author Azazell
 */
public class BlockData {
    public byte x, y, z;
    public Block b;
    public int meta;
    public Patterns.BlockType type;

    public BlockData(int x, int y, int z, Block b, Patterns.BlockType type)
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
}
