package AppliedIntegrations.Network;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 */
public class ClientPacketHelper {

	// Separate r/w method for server and client
	public static ISyncHost readSyncHostClient(ByteBuf buf) {
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
}
