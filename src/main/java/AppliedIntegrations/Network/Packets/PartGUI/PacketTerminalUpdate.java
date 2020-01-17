package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnergyList;
import appeng.api.config.SortOrder;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.util.Iterator;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed to send all data of ME Energy Terminal to gui.
 */
public class PacketTerminalUpdate extends AIPacket {
	public AIPart part;

	public IItemList<IAEEnergyStack> list;

	public SortOrder order;

	public PacketTerminalUpdate() {

	}

	public PacketTerminalUpdate(IItemList<IAEEnergyStack> monitor, @Nonnull SortOrder order, AIPart partToken) {
		this.list = monitor;
		this.part = partToken;
		this.order = order;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int size = buf.readInt();
		EnergyList list = new EnergyList();

		for (int i = 0; i < size; i++) {
			final LiquidAIEnergy energy = readEnergy(buf);
			if (energy == null) {
				continue;
			}

			IAEEnergyStack stack = AEEnergyStack.fromStack(new EnergyStack(energy, 0));

			stack.setStackSize(buf.readLong());
			list.add(stack);
		}

		this.part = (AIPart) readSyncHostClient(buf);
		this.list = list;
		this.order = SortOrder.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		int listSizeIndex = buf.writerIndex();
		buf.writeInt(list.size());

		int actualSize = 0;
		final Iterator<IAEEnergyStack> iterator = list.iterator();
		for (int i = 0; i < list.size(); i++) {
			if (iterator.hasNext()) {
				IAEEnergyStack entry = iterator.next();
				writeEnergy(entry.getEnergy(), buf);
				buf.writeLong(entry.getStack().amount);
				actualSize++;
			}
		}

		buf.setInt(listSizeIndex, actualSize);
		writeSyncHost(part, buf, false);
		buf.writeByte((byte) order.ordinal());
	}
}
