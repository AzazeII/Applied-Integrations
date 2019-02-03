package AppliedIntegrations.API.Multiblocks;

import net.minecraft.block.Block;

public class BlockData {
    public byte x, y, z;
    public Block b;
    public int meta;

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
