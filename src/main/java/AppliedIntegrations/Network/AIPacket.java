package AppliedIntegrations.Network;

import AppliedIntegrations.API.AppliedCoord;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Network.Packets.Overlook;
import appeng.api.parts.IPartHost;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

// Credit to vazkii for making this class: https://gist.github.com/Vazkii/13e0ce45577bbae49362
public abstract class AIPacket<REQ extends AIPacket> implements Serializable, IMessage, IMessageHandler<REQ, IMessage> {

    private static final HashMap<Class, Pair<Reader, Writer>> handlers = new HashMap();
    private static final HashMap<Class, Field[]> fieldCache = new HashMap();

    static {
        putInMap(byte.class, AIPacket::readByte, AIPacket::writeByte);
        putInMap(short.class, AIPacket::readShort, AIPacket::writeShort);
        putInMap(int.class, AIPacket::readInt, AIPacket::writeInt);
        putInMap(long.class, AIPacket::readLong, AIPacket::writeLong);
        putInMap(float.class, AIPacket::readFloat, AIPacket::writeFloat);
        putInMap(double.class, AIPacket::readDouble, AIPacket::writeDouble);
        putInMap(boolean.class, AIPacket::readBoolean, AIPacket::writeBoolean);
        putInMap(char.class, AIPacket::readChar, AIPacket::writeChar);
        putInMap(String.class, AIPacket::readString, AIPacket::writeString);
        putInMap(NBTTagCompound.class, AIPacket::readNBT, AIPacket::writeNBT);
        putInMap(ItemStack.class, AIPacket::readItemStack, AIPacket::writeItemStack);

        // Base parts, Energy, World and tiles
        putInMap(LiquidAIEnergy.class, AIPacket::readEnergy, AIPacket::writeEnergy);
        // parts
        putInMap(AIPart.class,AIPacket::readPart,AIPacket::writePart);
        putInMap(World.class,AIPacket::readWorld,AIPacket::writeWorld);
        putInMap(EnumFacing.class, AIPacket::readDirection,AIPacket::writeDirection);
        putInMap(AppliedCoord.class,AIPacket::readLoc, AIPacket::writeLoc);
        // tile
        putInMap(TileEntity.class,AIPacket::readTile,AIPacket::writeTile);

    }

    private static void writeTile(TileEntity tile, ByteBuf byteBuf) {

        writeWorld(tile.getWorld(),byteBuf);

        byteBuf.writeInt(tile.getPos().getX());
        byteBuf.writeInt(tile.getPos().getY());
        byteBuf.writeInt(tile.getPos().getZ());

    }

    private static TileEntity readTile(ByteBuf byteBuf) {

        World w = readWorld(byteBuf);
        return w.getTileEntity(new BlockPos(byteBuf.readInt(),byteBuf.readInt(),byteBuf.readInt()));
    }


    @Override
    public final IMessage onMessage(REQ message, MessageContext context){
        return HandleMessage(context);
    }
    public abstract IMessage HandleMessage(MessageContext ctx);

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            Class<?> clazz = getClass();
            Field[] clFields = getClassFields(clazz);
            for(Field f : clFields) {
                Class<?> type = f.getType();
                if(acceptField(f, type))
                    readField(f, type, buf);
            }
        } catch(Exception e) {
            throw new RuntimeException("Error at reading packet " + this, e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            Class<?> clazz = getClass();
            Field[] clFields = getClassFields(clazz);
            for(Field f : clFields) {
                Class<?> type = f.getType();
                if(acceptField(f, type))
                    writeField(f, type, buf);
            }
        } catch(Exception e) {
            throw new RuntimeException("Error at writing packet " + this, e);
        }
    }

    protected static Field[] getClassFields(Class<?> clazz) {
        if(fieldCache.containsValue(clazz))
            return fieldCache.get(clazz);
        else {
            Field[] fields = clazz.getFields();
            Arrays.sort(fields, (Field f1, Field f2) -> {
                return f1.getName().compareTo(f2.getName());
            });
            fieldCache.put(clazz, fields);
            return fields;
        }
    }

    protected final void writeField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {
        Pair<Reader, Writer> handler = getHandler(clazz);
        handler.getRight().write(f.get(this), buf);

    }

    protected final void readField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {
        Pair<Reader, Writer> handler = getHandler(clazz);
        f.set(this, handler.getLeft().read(buf));
    }

    protected static Pair<Reader, Writer> getHandler(Class<?> clazz) {
        Pair<Reader, Writer> pair = handlers.get(clazz);
        if(pair == null)
            throw new RuntimeException("No R/W handler for  " + clazz);
        return pair;
    }

    protected static boolean acceptField(Field f, Class<?> type) {
        int mods = f.getModifiers();
        if(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods))
            return false;
        if(f.isAnnotationPresent(Overlook.class))
            return false;
        return handlers.containsKey(type);
    }

    protected static <T extends Object>void putInMap(Class<T> type, Reader<T> reader, Writer<T> writer) {
        handlers.put(type, Pair.of(reader, writer));
    }

    protected static byte readByte(ByteBuf buf) {
        return buf.readByte();
    }

    protected static void writeByte(byte b, ByteBuf buf) {
        buf.writeByte(b);
    }

    protected static short readShort(ByteBuf buf) {
        return buf.readShort();
    }

    protected static void writeShort(short s, ByteBuf buf) {
        buf.writeShort(s);
    }

    protected static int readInt(ByteBuf buf) {
        return buf.readInt();
    }

    protected static void writeInt(int i, ByteBuf buf) {
        buf.writeInt(i);
    }

    protected static long readLong(ByteBuf buf) {
        return buf.readLong();
    }

    protected static void writeLong(long l, ByteBuf buf) {
        buf.writeLong(l);
    }

    protected static float readFloat(ByteBuf buf) {
        return buf.readFloat();
    }

    protected static void writeFloat(float f, ByteBuf buf) {
        buf.writeFloat(f);
    }

    protected static double readDouble(ByteBuf buf) {
        return buf.readDouble();
    }

    protected static void writeDouble(double d, ByteBuf buf) {
        buf.writeDouble(d);
    }

    protected static boolean readBoolean(ByteBuf buf) {
        return buf.readBoolean();
    }

    protected static void writeBoolean(boolean b, ByteBuf buf) {
        buf.writeBoolean(b);
    }

    protected static char readChar(ByteBuf buf) {
        return buf.readChar();
    }

    protected static void writeChar(char c, ByteBuf buf) {
        buf.writeChar(c);
    }

    protected static String readString(ByteBuf buf) {
        return ByteBufUtils.readUTF8String(buf);
    }

    protected static void writeString(String s, ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, s);
    }

    protected static NBTTagCompound readNBT(ByteBuf buf) {
        return ByteBufUtils.readTag(buf);
    }

    protected static void writeNBT(NBTTagCompound cmp, ByteBuf buf) {
        ByteBufUtils.writeTag(buf, cmp);
    }

    protected static ItemStack readItemStack(ByteBuf buf) {
        return ByteBufUtils.readItemStack(buf);
    }

    protected static void writeItemStack(ItemStack stack, ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
    }


    // Applied Integrations
    protected static void writeEnergy( LiquidAIEnergy energy,ByteBuf buf){
        if(energy != null)
            ByteBufUtils.writeUTF8String(buf,energy.getTag());
        else
            ByteBufUtils.writeUTF8String(buf,"null");
    }
    protected static LiquidAIEnergy readEnergy(ByteBuf buf){
        String read = ByteBufUtils.readUTF8String(buf);
        if(read != "null")
            return LiquidAIEnergy.getEnergy(read);
        else
            return null;
    }
    // parts
    protected static AIPart readPart(ByteBuf buf){
        try {
            World world = readWorld(buf);
            TileEntity entity = world.getTileEntity(buf.readInt(), buf.readInt(), buf.readInt());
            EnumFacing side = EnumFacing.getOrientation(buf.readInt());

            return (AIPart) (((IPartHost) entity).getPart(side));
        }catch (Exception e){}
        return null;
    }
    protected static void writePart(AIPart part, ByteBuf buf){
        try {
            TileEntity tile = part.getHostTile();
            buf.writeInt(tile.getWorld().provider.dimensionId);
            buf.writeInt(tile.getPos().getX());
            buf.writeInt(tile.getPos().getY());
            buf.writeInt(tile.getPos().getZ());

            buf.writeInt(part.getSide().ordinal());
        }catch (Exception e){
        }
    }
    private static World readWorld(ByteBuf buf) {
        World world = DimensionManager.getWorld(buf.readInt());

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (world == null) {
                world = Minecraft.getMinecraft().world;
            }
        }

        return world;
    }
    private static void writeWorld(World w, ByteBuf buf){

        try{
            buf.writeInt(w.provider.getDimension());
        }catch (NullPointerException e){
            buf.writeInt(DimensionManager.getNextFreeDimId());
        }
    }

    private static void writeLoc(AppliedCoord coord, ByteBuf buf){
        writeWorld(coord.getWorld(),buf);

        buf.writeInt(coord.x);
        buf.writeInt(coord.y);
        buf.writeInt(coord.z);

        writeDirection(coord.side,buf);
    }
    private static AppliedCoord readLoc(ByteBuf buf){
        return new AppliedCoord(readWorld(buf),buf.readInt(),buf.readInt(),buf.readInt(),readDirection(buf));
    }

    private static void writeDirection(EnumFacing forgeDirection, ByteBuf byteBuf) {
        byteBuf.writeByte((byte)forgeDirection.ordinal());
    }

    private static EnumFacing readDirection(ByteBuf buf){
        return EnumFacing.getOrientation(buf.readByte());
    }

    public interface Writer<T extends Object> {
        void write(T t, ByteBuf buf);
    }

    public interface Reader<T extends Object> {
        T read(ByteBuf buf);
    }

}
