package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerServerPacketTracer;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkGui;
import AppliedIntegrations.Gui.ServerGUI.SubGui.PortDirections;
import AppliedIntegrations.Gui.ServerGUI.SubGui.ServerGui;
import AppliedIntegrations.Utils.AILog;
import appeng.api.networking.IGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;
import java.util.Vector;

public class ServerPacketTracer extends AIBaseGui {

    private final int GUI_X = 342;
    private final int GUI_Y = 139;

    private final int GUI_WIDTH = 256;
    private final int GUI_HEIGH = 246;

    private final int Master_X = 274;
    private final int Master_Y = 163;

    private final int Master_CenterX = 113;
    private final int Master_CenterY = 79;

    private ResourceLocation tracer = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/ServerTracer.png");
    private ResourceLocation cable = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/MECable.png");


    private Vector<ServerGui> Servers = new Vector<>();

    public Vector<IGrid> Networks = null;

    private LinkedHashMap<ForgeDirection, NetworkGui> netInterface = new LinkedHashMap<>();

    /**
     * Map of all server from server id
     */
    public LinkedHashMap<Integer,ServerGui> ServerMap = new LinkedHashMap<>();

    private boolean hasMaster;
    private int id=0;
    private ServerGui MasterServerGui;

    public ServerPacketTracer(ContainerServerPacketTracer container, TileServerCore master, int x, int y, int z, EntityPlayer player) {
        super(container);

    }
    @Override
    protected void mouseClicked(int mX, int mY, int e) {
        for(ServerGui g : Servers){
            g.isMouseOverButton(mX,mY);
        }
        for(NetworkGui g : netInterface.values()){
            if(g != null)
                if(g.isMouseOverButton(mX,mY))
                    AILog.chatLog("Mouse clicked on:"+" NetworkGUI"+g.dir.toString());
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

        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);
    }
    @Override
    public void initGui(){
        this.MasterServerGui = new ServerGui(id++,this.Master_X, this.Master_Y,true);

        this.Servers.add(this.MasterServerGui);

        this.guiLeft = GUI_X;
        this.guiTop = GUI_Y;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        this.fontRendererObj.drawString("ME Server Packet Tracer", 9, 2, 4210752);

        for(ServerGui sG : ServerMap.values()){
            if(sG != null) {
                if (sG.isMainServer) {
                    sG.renderGui(0.3F);
                    for(NetworkGui nG : sG.linkedNetworks.toArray(new NetworkGui[sG.linkedNetworks.size()])){

                        PortDirections portDirections = portDirection(nG.dir);

                        int offset = 55;

                        nG.xPosition = Master_CenterX+portDirections.offsetX*offset;
                        nG.yPosition = Master_CenterY+portDirections.offsetY*offset;

                        nG.renderGui(1F,Master_CenterX+portDirections.offsetX*offset, Master_CenterY+portDirections.offsetY*offset);

                        if(nG.isLinked){

                            // Black lines
                            GL11.glColor4f( 0F, 0F, 0F, 1.0F );

                            GL11.glPushMatrix();

                            //------- drawVertexLine(cable,sG.getCenterPointX(), sG.getCenterPointY(), nG.xPosition,nG.yPosition); -------\\

                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }
    }

    private PortDirections portDirection(ForgeDirection dir) {
        switch (dir){
            case EAST:
                return PortDirections.E;
            case WEST:
                return PortDirections.W;
            case SOUTH:
                return PortDirections.S;
            case DOWN:
                return PortDirections.D;
            case NORTH:
                return PortDirections.N;
            case UP:
                return PortDirections.U;
        }
        return null;
    }

    public void setMaster(NetworkData data){
        ServerMap.put(data.id,this.MasterServerGui);
    }

    public void addNetwork(NetworkData data){
        if(!data.isServerNetwork) {
            NetworkGui newGUi = new NetworkGui(this, id++, data.dir, data.id);

            this.netInterface.put(data.dir,newGUi);
            ServerGui sG = this.ServerMap.get(data.id);

            if(sG != null){
                sG.linkedNetworks.add(newGUi);
            }
        }
    }

    public void removeNetwork(){

    }

    public void onStateReceive(boolean state) {
        this.hasMaster = state;
    }
}
