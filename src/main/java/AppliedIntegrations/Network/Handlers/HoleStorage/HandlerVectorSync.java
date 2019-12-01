package AppliedIntegrations.Network.Handlers.HoleStorage;


import AppliedIntegrations.Network.Packets.HoleStorage.PacketVectorSync;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerVectorSync implements IMessageHandler<PacketVectorSync, PacketVectorSync> {
	@Override
	public PacketVectorSync onMessage(PacketVectorSync message, MessageContext ctx) {
		// Update client sided tile
		Minecraft.getMinecraft().addScheduledTask(() -> {
			TileMETurretFoundation te = (TileMETurretFoundation) Minecraft.getMinecraft().world.getTileEntity(message.tilePos);
			te.direction = message.direction;
			te.ammo = message.ammo;
			te.blackHolePos = message.blackHolePos;
			te.whiteHolePos = message.whiteHolePos;
		});

		return null;
	}
}
