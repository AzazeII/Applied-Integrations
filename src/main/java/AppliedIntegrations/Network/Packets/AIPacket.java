package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @Author Azazell
 */
public abstract class AIPacket implements IMessage {
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

	protected void writePos(BlockPos pos, ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	protected void writeVec(Vec3d vec, ByteBuf buf) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}

	protected BlockPos readPos(ByteBuf buf) {
		return BlockPos.fromLong(buf.readLong());
	}

	protected Vec3d readVec(ByteBuf buf) {
		return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	private void writeWorld(ByteBuf buf, World world) {
		buf.writeInt(world.provider.getDimension());
	}

	protected void writeEnum(Enum anum, ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, anum.getDeclaringClass().getName());
		buf.writeInt(anum.ordinal());
	}

	protected void writeSyncHost(ISyncHost host, ByteBuf buf, boolean useWorld) {
		buf.writeLong(host.getHostPos().toLong());
		buf.writeInt(host.getHostSide().ordinal());

		if (useWorld) {
			writeWorld(buf, host.getHostWorld());
		}
	}

	protected ISyncHost readSyncHost(ByteBuf buf) {
		ISyncHost host;

		BlockPos pos = BlockPos.fromLong(buf.readLong());
		AEPartLocation side = AEPartLocation.values()[buf.readInt()];
		World w = readWorld(buf);

		if (side == AEPartLocation.INTERNAL) {
			host = (ISyncHost) w.getTileEntity(pos);
		} else {
			host = Utils.getSyncHostByParams(pos, side, w);
		}

		return host;
	}

	protected Enum readEnum(ByteBuf buf) {
		try {
			Class enumClass = Class.forName(ByteBufUtils.readUTF8String(buf));
			return (Enum) enumClass.getEnumConstants()[buf.readInt()];
		} catch(ClassNotFoundException ignored) {
			return null;
		}
	}

	private World readWorld(ByteBuf buf) {
		return DimensionManager.getWorld(buf.readInt());
	}
}
