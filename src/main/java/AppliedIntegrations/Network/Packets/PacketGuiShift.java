package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGuiShift extends AIPacket {
    public EntityPlayer player;
    public AIGuiHandler.GuiEnum gui;
    public AIPart part;

    public PacketGuiShift(){}

    public PacketGuiShift(AIGuiHandler.GuiEnum newGui, IPriorityHostExtended part) {
        super(part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), part.getSide().getFacing(), part.getWorld());

        gui = newGui;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Read host
        part = readPart(buf);

        // Read gui
        gui = AIGuiHandler.GuiEnum.values()[buf.readInt()];

        // Get player [Client Sided]
        player = Minecraft.getMinecraft().player;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Write host
        writePart(buf);

        // Write gui
        buf.writeInt(gui.ordinal());
    }
}
