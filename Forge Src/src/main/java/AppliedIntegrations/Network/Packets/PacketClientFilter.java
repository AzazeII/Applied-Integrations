package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.AppliedCoord;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Parts.EnergyInterface.PartEnergyInterface;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.AIPrivateInventory;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import scala.App;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static AppliedIntegrations.API.LiquidAIEnergy.RF;
/**
 * @Author Azazell
 */
// Sends data from client to server (gui to part)
public class PacketClientFilter extends AIPacket<PacketClientFilter> {

    public LiquidAIEnergy energy;
    public int index;

    public AIPart part;

    public PacketClientFilter(){}

    // Only neutral point between client, and server
    public PacketClientFilter( int x,int y,int z,ForgeDirection side, World w, LiquidAIEnergy energy, int index) {
        this.energy = energy;
        this.index = index;

        this.part = Utils.getPartByParams(x,y,z,side,w);

        if(part != null) {
            ((IEnergyMachine)part).updateFilter(energy, 0);
        }
    }
    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}
