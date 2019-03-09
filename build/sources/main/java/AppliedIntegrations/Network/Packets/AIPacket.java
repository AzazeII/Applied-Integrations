package AppliedIntegrations.Network.Packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AIPacket implements IMessage {
    public final World w;
    public EnumFacing side;
    public int x, y, z;

    public AIPacket(){
        this(0,0,0,null,null);
    }

    public AIPacket(int x, int y, int z, EnumFacing side, World w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.w = w;
    }
}
