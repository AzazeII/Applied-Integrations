package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AIPacket implements IMessage {
    public World w;
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

    protected AIPart getPart(ByteBuf buf){
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        World w = getWorld(buf);

        EnumFacing side = getSide(buf);

        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();

        this.w = w;
        this.side = side;

        return Utils.getPartByParams(pos, side, w);
    }

    private EnumFacing getSide(ByteBuf buf) {
        return EnumFacing.getFront(buf.readInt());
    }


    protected LiquidAIEnergy getEnergy(ByteBuf buf) {
        int buffed = buf.readInt();

        if(buffed != -1)
            return LiquidAIEnergy.linkedIndexMap.get(buffed);
        return null;
    }

    public World getWorld(ByteBuf buf) {
        World world = DimensionManager.getWorld( buf.readInt() );

        if( FMLCommonHandler.instance().getSide() == Side.CLIENT )
        {
            if( world == null )
            {
                world = Minecraft.getMinecraft().world;
            }
        }

        return world;
    }

    protected void setPart(ByteBuf buf){
        buf.writeLong(new BlockPos(x,y,z).toLong());

        setWorld(buf, w);
        buf.writeInt(side.ordinal());
    }

    protected void setWorld(ByteBuf buf, World world) {
        buf.writeInt(world.provider.getDimension());
    }

    protected void setEnergy(LiquidAIEnergy energy, ByteBuf buf) {
        if(energy != null)
            buf.writeInt(energy.getIndex());
        else
            buf.writeInt(-1);
    }

    protected void writePos(BlockPos pos, ByteBuf buf){
        buf.writeLong(pos.toLong());
    }

    protected BlockPos readPos(ByteBuf buf){
        return BlockPos.fromLong(buf.readLong());
    }

    protected void writeTile(TileEntity tile, ByteBuf buf){
        writePos(tile.getPos(), buf);
        setWorld(buf, tile.getWorld());
    }

    protected TileEntity readTile(ByteBuf buf){
        BlockPos pos = readPos(buf);
        return getWorld(buf).getTileEntity(pos);
    }

    protected void writeVec(Vec3d vecA, ByteBuf buf) {
        writePos(new BlockPos(vecA), buf);
    }

    protected Vec3d readVec(ByteBuf buf) {
        return new Vec3d(readPos(buf));
    }
}
