package AppliedIntegrations.Network.Packets.Server;

import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkPermissions;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage This packet needed to update network card tag data on server from client
 */
public class PacketServerFeedback extends AIPacket {

    public NBTTagCompound tag;

    public PacketServerFeedback() {

    }

    public PacketServerFeedback(NBTTagCompound tag){
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }
}
