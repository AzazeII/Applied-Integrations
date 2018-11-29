package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerServerPacketTracer;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Utils.AILog;
import appeng.api.networking.IGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Vector;

public class ServerPacketTracer extends AIBaseGui {

    private ResourceLocation tracer = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/ServerTracer.png");

    private Vector<ServerGui> Servers = new Vector<>();
    public Vector<IGrid> Networks = null;
    public TileServerCore ServerMaster;
    private Vector<NetworkGui> netInterface = new Vector<>();

    public int x,y,z;

    private int zoom = 0;

    public ServerPacketTracer(ContainerServerPacketTracer container, TileServerCore master, int x, int y, int z, EntityPlayer player) {
        super(container);
        this.ServerMaster=master;

        this.x=x;
        this.y=y;
        this.z=z;

        if(ServerMaster != null)
            AILog.chatLog(ServerMaster.toString(),player);

    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            zoom += i;
        }
    }
    @Override
    public AIContainer getNodeContainer() {
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.tracer);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        Minecraft.getMinecraft().renderEngine.bindTexture(tracer);

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 200, 200);
    }
    @Override
    public void initGui(){
        if(ServerMaster != null) {
            ServerGui gui = new ServerGui(0, "Server №0");
            gui.pY = this.ySize / 2;
            gui.pX = this.xSize / 2;
            this.Servers.add(gui);
        }
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        this.fontRendererObj.drawString("ME Server FireWall", 9, 2, 4210752);




        if(ServerMaster != null) {
            for (ServerGui server : Servers) {
                GL11.glScalef(0.3F, 0.3F, 0.3F);
                server.renderGui();
                GL11.glScalef(3F, 3F, 3F);
            }
        }


    }
}
