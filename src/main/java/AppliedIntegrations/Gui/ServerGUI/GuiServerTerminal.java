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
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

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

    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGH = 246;

    private static final int MASTER_X = 274;
    private static final int MASTER_Y = 163;

    private final int MASTER_CENTER_X = 113;
    private final int MASTER_CENTER_Y = 79;

    // Offset from vector of port direction
    private static final int PORT_DIRECTION_OFFSET = 55;

    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_terminal.png");
    private ResourceLocation cable = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_cable.png");

    // Facing -> Network map
    // Guest: What mapp.... See line 62
    private LinkedHashMap<EnumFacing, NetworkGui> serverPortMap = new LinkedHashMap<>();

    // Id -> Server map
    // Guest: What mapping class you want, for mapping gui with integers, Azazell?
    // Azazell: Yes
    private LinkedHashMap<Integer,ServerGui> serverMap = new LinkedHashMap<>();

    public EntityPlayer player;

    // Dynamic variable, id of last network added
    private int id = 0;

    // Gui of server linked with this security terminal
    private ServerGui masterServerGui;

    // Currently selected network
    private AIGuiButton selectedNetwork;

    public GuiServerTerminal(ContainerServerTerminal container, EntityPlayer player) {
        super(container, player);

        this.player = player;

    }

    @Override
    protected void mouseClicked(int mX, int mY, int e) {
        // Iterate for each network
        for(NetworkGui g : serverPortMap.values()){
            // Check not null
            if(g != null)
                // Check if mouse over network
                if(g.isMouseOverButton(mX,mY)) {
                    // Select network
                    selectedNetwork = g;

                    // Break function
                    return;
                }else if(g.isMouseOverMarker(mX,mY)){
                    g.isLinked = !g.isLinked;
                    // Check if mouse is over marker
                }
        }

        selectedNetwork = null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Bind our texture
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);

        // Draw texture
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);

        // Check if selected network not null
        /*if(selectedNetwork != null)
            // Draw one more texture
            this.drawTexturedModalRect(guiLeft + GUI_WIDTH + 2, guiTop, 50, 0, BAR_WIDTH, BAR_HEIGH);*/
    }
    @Override
    public void initGui(){
        this.masterServerGui = new ServerGui(id++, MASTER_X, MASTER_Y,true,this);
        this.serverMap.put(0, masterServerGui);

        this.guiLeft = GUI_X;
        this.guiTop = GUI_Y;

        for (AEPartLocation location : AEPartLocation.SIDE_LOCATIONS)
            addNetwork(new NetworkData(false, location, 0));
    }

    @Override
    public void drawScreen(int mX, int mY, float pOpacity) {
        super.drawScreen(mX,mY,pOpacity);

        // Iterate for each network at each port
        for(NetworkGui g : serverPortMap.values()){
            // Check if mouse is over this widget
            if(g.isMouseOverButton(mX,mY)){
                // Draw text
                drawHoveringText(g.getTip(),mX,mY,fontRenderer);
            }
        }

        // Iterate for each network at each port
        for(ServerGui g : serverMap.values()){
            // Check if mouse is over this widget
            if(g.isMouseOverButton(mX,mY)){
                // Draw text
                drawHoveringText(g.getTip(),mX,mY,fontRenderer);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        this.fontRenderer.drawString(I18n.translateToLocal("ME Server Network Terminal"), 9, 2, 4210752);

        // Iterate for each server
        for(ServerGui server : serverMap.values()) {
            // Check not null
            if (server != null) {
                // Check if it's main server
                if (server.isMainServer) {
                    // Iterate for each network connected to server
                    for (NetworkGui network : server.linkedNetworks.values()) {
                        // Check not null
                        if (network != null) {
                            // Render network
                            network.renderGui(1F);
                        }
                    }
                }
                // Render server
                server.renderGui(0.3F);
            }
        }
    }

    public void addMaster(NetworkData data){
        serverMap.put(data.id,this.masterServerGui);
    }

    public void addNetwork(NetworkData data){
        // Check if network isn't network with server
        if(!data.isServerNetwork) {
            // Convert facing to port direction
            PortDirections portDirections = PortDirections.fromFacing(data.dir.getFacing());

            // Create new network gui
            // X: Center of master network_X + direction * offset..
            // Y: Center of master network_Y + direction * offset..
            NetworkGui networkGui = new NetworkGui((int)(MASTER_CENTER_X + portDirections.offsetX* PORT_DIRECTION_OFFSET * 1.5),
                    MASTER_CENTER_Y +portDirections.offsetY * PORT_DIRECTION_OFFSET,this, id++, data.dir.getFacing(), data.id);

            // Make network linked to server
            networkGui.isLinked = true;

            // Bind network to port
            serverPortMap.put(data.dir.getFacing(), networkGui);

            // Get server gui
            ServerGui serverGui = this.serverMap.get(data.id);

            // Check not null
            if(serverGui != null){
                // Put
                serverGui.linkedNetworks.put(data.dir, networkGui);
            }
        }
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

    public ServerGui getServerFromID(int linkedServerID) {
        return serverMap.get(linkedServerID);
    }
}
