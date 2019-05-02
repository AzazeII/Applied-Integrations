package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkGui;
import AppliedIntegrations.Gui.ServerGUI.SubGui.PortDirections;
import AppliedIntegrations.Gui.ServerGUI.SubGui.ServerGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Vector;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @Author Azazell
 */
public class GuiServerTerminal extends AIBaseGui {

    public TileServerCore mInstance;

    private final int GUI_X = 342;
    private final int GUI_Y = 139;

    private final int GUI_WIDTH = 256;
    private final int GUI_HEIGH = 246;

    private final int BAR_WIDTH = 50;
    private final int BAR_HEIGH = 256;

    public final int MASTER_X = 274;
    public final int MASTER_Y = 163;

    private final int MASTER_CENTER_X = 113;
    private final int MASTER_CENTER_Y = 79;

    private final int BAR_X = MASTER_X+GUI_WIDTH+2;

    private ResourceLocation tracer = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_terminal.png");
    private ResourceLocation cable = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_cable.png");

    private Vector<ServerGui> servers = new Vector<>();

    private LinkedHashMap<EnumFacing, NetworkGui> netInterface = new LinkedHashMap<>();

    public EntityPlayer player;

    /**
     * Map of all servers from server id
     */
    public LinkedHashMap<Integer,ServerGui> serverMap = new LinkedHashMap<>();

    private boolean hasMaster;
    private int id=0;
    private ServerGui masterServerGui;
    private AIGuiButton selectedNetwork;

    public GuiServerTerminal(ContainerServerTerminal container, TileServerCore master, EntityPlayer player) {
        super(container, player);

        this.player = player;

    }
    @Override
    protected void mouseClicked(int mX, int mY, int e) {
        for(NetworkGui g : netInterface.values()){
            if(g != null)
                if(g.isMouseOverButton(mX,mY)) {
                    selectedNetwork = g;
                    return;
                }else if(g.isMouseOverMarker(mX,mY)){
                    g.isLinked = !g.isLinked;
                }
        }

        selectedNetwork = null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(this.tracer);

        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);

        if(selectedNetwork != null)
            this.drawTexturedModalRect(guiLeft + GUI_WIDTH + 2, guiTop, 50, 0, BAR_WIDTH, BAR_HEIGH);
    }
    @Override
    public void initGui(){
        this.masterServerGui = new ServerGui(id++,this.MASTER_X, this.MASTER_Y,true,this);

        this.servers.add(this.masterServerGui);

        this.guiLeft = GUI_X;
        this.guiTop = GUI_Y;
    }

    /**
     * Use only for tooltip drawing
     */
    @Override
    public void drawScreen(int mX, int mY, float pOpacity) {
        super.drawScreen(mX,mY,pOpacity);
        /*for(ServerGui g : servers){
            if(g.isMouseOverButton(mX,mY)){
                drawHoveringText(g.getTip(),mX,mY,fontRendererObj);
            }
        }*/
        for(NetworkGui g : netInterface.values()){
            if(g.isMouseOverButton(mX,mY)){
                drawHoveringText(g.getTip(),mX,mY,fontRenderer);
            }
        }
    }

    private void drawLine(ServerGui sG, NetworkGui nG) {
        // Get tesselator
        Tessellator tessellator = Tessellator.getInstance();

        // Get builder
        BufferBuilder builder = tessellator.getBuffer();

        // Start drawing lines
        builder.begin(GL_LINES, POSITION_TEX);

        // Draw straight line
        builder.pos(sG.x, sG.y, 0).endVertex();
        builder.pos(nG.x, nG.y, 0).endVertex();;

        // End drawing
        tessellator.draw();
    }

    /**
     * Use only for gui drawing
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        this.fontRenderer.drawString(I18n.translateToLocal("ME Server Network Tracer"), 9, 2, 4210752);

        // Iterate for each server
        for(ServerGui sG : serverMap.values()) {
            // Check not null
            if (sG != null) {
                // Check if it's main server
                if (sG.isMainServer) {
                    // Iterate for each network connected to server
                    for (NetworkGui nG : sG.linkedNetworks) {
                        // Check not null
                        if (nG != null) {
                            PortDirections portDirections = portDirection(nG.dir);

                            int offset = 55;

                            nG.x = (int) (MASTER_CENTER_X + portDirections.offsetX * offset * 1.5);
                            nG.y = MASTER_CENTER_Y + portDirections.offsetY * offset;

                            nG.isLinked = true;

                            if (nG.isLinked) {
                                GL11.glPushMatrix();

                                // Pass drawing to function
                                drawLine(sG, nG);

                                GL11.glPopMatrix();
                            }

                            nG.renderGui(1F);
                        }
                    }
                }

                sG.renderGui(0.3F);
            }
        }
    }

    @Nonnull
    private PortDirections portDirection(EnumFacing dir) {
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
        return PortDirections.NaD;
    }

    public void setMaster(NetworkData data){
        serverMap.put(data.id,this.masterServerGui);
    }

    public void addNetwork(NetworkData data){
        if(!data.isServerNetwork) {

            PortDirections portDirections = portDirection(data.dir.getFacing());

            int offset = 55;

            NetworkGui nG = new NetworkGui((int)(MASTER_CENTER_X+portDirections.offsetX*offset*1.5), MASTER_CENTER_Y +portDirections.offsetY*offset,this, id++, data.dir.getFacing(), data.id);

            nG.isLinked = true;

            this.netInterface.put(data.dir.getFacing(),nG);
            ServerGui sG = this.serverMap.get(data.id);

            if(sG != null){
                sG.linkedNetworks[data.dir.ordinal()] = nG;
            }
        }
    }

    public void removeNetwork(){

    }

    public void onStateReceive(boolean state) {
        this.hasMaster = state;
    }

    @Override
    public ISyncHost getSyncHost() {
        return mInstance;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof TileServerCore)
            mInstance = (TileServerCore)host;
    }
}
