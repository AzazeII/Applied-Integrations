package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet is only needed for updating energy bar in Energy interface, when you need to update current energy value, you can send this packet
 */
public class PacketProgressBar extends AIPacket {

	public IEnergyInterface sender;

	public LiquidAIEnergy energy;

	public AEPartLocation energySide;

	public PacketProgressBar() {

	}

	public PacketProgressBar(IEnergyInterface sender, LiquidAIEnergy energy, AEPartLocation energySide) {

		super(sender.getPositionVector().getX(), sender.getPositionVector().getY(), sender.getPositionVector().getZ(), sender.getSide().getFacing(), sender.getWorld());
		this.sender = sender;
		this.energy = energy;
		this.energySide = energySide;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		sender = (IEnergyInterface) readSyncHost(buf);
		energy = readEnergy(buf);
		energySide = AEPartLocation.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writeSyncHost(sender, buf);
		writeEnergy(energy, buf);
		buf.writeByte(energySide.ordinal());
	}
}
