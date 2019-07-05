package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
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

	protected BlockPos readPos(ByteBuf buf) {
		return BlockPos.fromLong(buf.readLong());
	}

	private void writeWorld(ByteBuf buf, World world) {
		buf.writeInt(world.provider.getDimension());
	}

	protected void writeSyncHost(ISyncHost host, ByteBuf buf, boolean useWorld) {
		buf.writeLong(host.getHostPos().toLong());
		buf.writeInt(host.getHostSide().ordinal());

		if (useWorld) {
			writeWorld(buf, host.getHostWorld());
		}
	}

	// Separate r/w method for server and client
	protected static ISyncHost readSyncHostClient(ByteBuf buf) {
		ISyncHost host;

		BlockPos pos = BlockPos.fromLong(buf.readLong());

		AEPartLocation side = AEPartLocation.values()[buf.readInt()];

		if (side == AEPartLocation.INTERNAL) {
			host = (ISyncHost) Minecraft.getMinecraft().world.getTileEntity(pos);
		} else {
			host = Utils.getSyncHostByParams(pos, side, Minecraft.getMinecraft().world);
		}

		return host;
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

	private World readWorld(ByteBuf buf) {
		return DimensionManager.getWorld(buf.readInt());
	}
}
