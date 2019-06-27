package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.tile.AITile;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @Author Azazell
 */
public abstract class AIPacket implements IMessage {
	public World w;

	public EnumFacing side;

	public int x, y, z;

	public AIPacket() {

		this(0, 0, 0, null, null);
	}

	public AIPacket(int x, int y, int z, EnumFacing side, World w) {

		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
		this.w = w;
	}

	public AIPacket(ISyncHost token) {
		this(token.getHostPos().getX(), token.getHostPos().getY(), token.getHostPos().getZ(), token.getHostSide().getFacing(), token.getHostWorld());
	}

	protected LiquidAIEnergy readEnergy(ByteBuf buf) {

		int buffed = buf.readInt();

		if (buffed != -1) {
			return LiquidAIEnergy.linkedIndexMap.get(buffed);
		}
		return null;
	}

	protected void writeEnergy(LiquidAIEnergy energy, ByteBuf buf) {

		if (energy != null) {
			buf.writeInt(energy.getIndex());
		} else {
			buf.writeInt(-1);
		}
	}

	protected void writeVec(Vec3d vecA, ByteBuf buf) {

		writePos(new BlockPos(vecA), buf);
	}

	protected void writePos(BlockPos pos, ByteBuf buf) {

		buf.writeLong(pos.toLong());
	}

	protected Vec3d readVec(ByteBuf buf) {

		return new Vec3d(readPos(buf));
	}

	protected BlockPos readPos(ByteBuf buf) {

		return BlockPos.fromLong(buf.readLong());
	}

	protected void writeSyncHost(ISyncHost host, ByteBuf buf) {
		if (host instanceof AIPart) {
			// Write state to buf
			buf.writeBoolean(true);

			writePart(buf);
		} else if (host instanceof AITile) {
			// Write state to buf
			buf.writeBoolean(false);

			writeTile((AITile) host, buf);
		}
	}

	protected void writePart(ByteBuf buf) {

		buf.writeLong(new BlockPos(x, y, z).toLong());

		writeWorld(buf, w);
		buf.writeInt(side.ordinal());
	}

	protected void writeTile(TileEntity tile, ByteBuf buf) {

		writePos(tile.getPos(), buf);
		writeWorld(buf, tile.getWorld());
	}

	protected void writeWorld(ByteBuf buf, World world) {

		buf.writeInt(world.provider.getDimension());
	}

	protected ISyncHost readSyncHost(ByteBuf buf) {

		boolean isPart = buf.readBoolean();

		ISyncHost host;

		if (isPart) {
			host = readPart(buf);
		} else {
			host = (ISyncHost) readTile(buf);
		}

		return host;
	}

	protected AIPart readPart(ByteBuf buf) {

		BlockPos pos = BlockPos.fromLong(buf.readLong());
		World w = readWorld(buf);

		EnumFacing side = readSide(buf);

		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();

		this.w = w;
		this.side = side;

		return Utils.getPartByParams(pos, side, w);
	}

	protected TileEntity readTile(ByteBuf buf) {

		BlockPos pos = readPos(buf);
		return readWorld(buf).getTileEntity(pos);
	}

	private World readWorld(ByteBuf buf) {
		WorldServer world = DimensionManager.getWorld(buf.readInt());

		// Ignore on server
		if (Platform.isClient()) {
			// Check not null
			if (world == null) {
				// Transform world into client world
				return Minecraft.getMinecraft().world;
			}
		}

		return world;
	}

	private EnumFacing readSide(ByteBuf buf) {

		return EnumFacing.getFront(buf.readInt());
	}
}
