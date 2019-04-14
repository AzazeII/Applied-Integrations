package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Grid.AEEnergyStack;
import AppliedIntegrations.Grid.EnergyList;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Usage This packet needed to send all data of ME Energy Terminal to gui.
 */
public class PacketTerminalUpdate extends AIPacket {
    public AIPart part;

    public PacketTerminalUpdate(){}

    public IItemList<IAEEnergyStack> list;

    public PacketTerminalUpdate(IItemList<IAEEnergyStack> monitor, AIPart partToken){
        super(partToken.getX(), partToken.getY(), partToken.getZ(), partToken.getSide().getFacing(), partToken.getWorld());
        this.list = monitor;
        this.part = partToken;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Read size
        int size = buf.readInt();

        // Create list
        EnergyList list = new EnergyList();

        // Iterate until i = size
        for(int i = 0; i < size; i++){
            // Read energy and create stack
            IAEEnergyStack stack = AEEnergyStack.fromStack(new EnergyStack(readEnergy(buf), 0));

            // Read size and set stack size
            stack.setStackSize(buf.readLong());

            // Add stack to list
            list.add(stack);
        }

        // Read part
        this.part = readPart(buf);

        // Set list
        this.list = list;
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

        // Write part
        writePart(buf);
    }
}
