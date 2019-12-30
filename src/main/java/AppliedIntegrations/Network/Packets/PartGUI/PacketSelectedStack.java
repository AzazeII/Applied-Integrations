package AppliedIntegrations.Network.Packets.PartGUI;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage Sync client-sided selected energy with server
 */
public class PacketSelectedStack extends AIPacket {
	public LiquidAIEnergy selectedEnergy;
	public PartEnergyTerminal part;

	public PacketSelectedStack() {}

	public PacketSelectedStack(LiquidAIEnergy selectedEnergy, PartEnergyTerminal part) {
		this.selectedEnergy = selectedEnergy;
		this.part = part;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		selectedEnergy = readEnergy(buf);
		part = (PartEnergyTerminal) readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeEnergy(selectedEnergy, buf);
		writeSyncHost(part, buf, true);
	}
}
