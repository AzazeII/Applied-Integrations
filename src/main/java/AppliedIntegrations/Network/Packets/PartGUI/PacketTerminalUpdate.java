package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnergyList;
import appeng.api.config.SortOrder;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

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

		super(partToken.getX(), partToken.getY(), partToken.getZ(), partToken.getSide().getFacing(), partToken.getWorld());
		this.list = monitor;
		this.part = partToken;
		this.order = order;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Read size
		int size = buf.readInt();

		// Create list
		EnergyList list = new EnergyList();

		// Iterate until i = size
		for (int i = 0; i < size; i++) {
			// Read energy and create stack
			IAEEnergyStack stack = AEEnergyStack.fromStack(new EnergyStack(readEnergy(buf), 0));

			// Read size and set stack size
			stack.setStackSize(buf.readLong());

			// Add stack to list
			list.add(stack);
		}

		// Read host
		this.part = readPart(buf);

		// Set list
		this.list = list;

		// Read order ordinal
		this.order = SortOrder.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Write size of list
		buf.writeInt(list.size());

		// Iterate for each list entry
		list.iterator().forEachRemaining((entry -> {
			// Write entries' stack
			writeEnergy(entry.getEnergy(), buf);


			// Write entries' count
			buf.writeLong(entry.getStack().amount);
		}));

		// Write host
		writePart(buf);

		// Write ordinal of order
		buf.writeByte((byte) order.ordinal());
	}
}
