package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.Server.ContainerServerPacketTracer;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkGui;
import AppliedIntegrations.Gui.ServerGUI.SubGui.PortDirections;
import AppliedIntegrations.Gui.ServerGUI.SubGui.ServerGui;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.client.gui.widgets.GuiToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Vector;

import static AppliedIntegrations.Gui.ServerGUI.NetworkPermissions.*;

/**
 * @Author Azazell
 */
public class ServerPacketTracer extends AIBaseGui {

    public TileServerCore mInstance;

    private final int GUI_X = 342;
    private final int GUI_Y = 139;

    private final int GUI_WIDTH = 256;
    private final int GUI_HEIGH = 246;

    private final int BAR_WIDTH = 50;
    private final int BAR_HEIGH = 256;

    public final int Master_X = 274;
    public final int Master_Y = 163;

    private final int Master_CenterX = 113;
    private final int Master_CenterY = 79;

    private final int BAR_X = Master_X+GUI_WIDTH+2;

    private ResourceLocation tracer = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/ServerTracer.png");
    private ResourceLocation tracerBar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/TracerEditBar.png");

    private ResourceLocation cable = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/MECable.png");

    private Vector<ServerGui> Servers = new Vector<>();

    public Vector<IGrid> Networks = null;

    private Vector<GuiToggleButton> SettingList = new Vector<>();

    private LinkedHashMap<EnumFacing, NetworkGui> netInterface = new LinkedHashMap<>();

    private GuiServerButton[] energy = new GuiServerButton[3];
    private GuiServerButton[] item = new GuiServerButton[3];
    private GuiServerButton[] fluid = new GuiServerButton[3];
    private GuiServerButton[] essentia = new GuiServerButton[3];
    private GuiServerButton[] gas = new GuiServerButton[3];
    private GuiServerButton[] mana = new GuiServerButton[3];

    private GuiToggleButton extract;
    private GuiToggleButton inject;
    private GuiToggleButton craft;

    private GuiServerButton energyGrid;

    public EntityPlayer player;

    /**
     * Map of all servers from server id
     */
    public LinkedHashMap<Integer,ServerGui> ServerMap = new LinkedHashMap<>();

    private boolean hasMaster;
    private int id=0;
    private ServerGui MasterServerGui;
    private AIGuiButton selectedNetwork;

    public ServerPacketTracer(ContainerServerPacketTracer container, TileServerCore master,EntityPlayer player) {
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
        for(GuiToggleButton btn : SettingList){
            if(btn instanceof GuiServerButton){
                GuiServerButton button = (GuiServerButton)btn;
                if(button.visible){
                   button.doAction();
                   return;
                }
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

        this.MasterServerGui = new ServerGui(id++,this.Master_X, this.Master_Y,true,this);

        this.Servers.add(this.MasterServerGui);

        this.guiLeft = GUI_X;
        this.guiTop = GUI_Y;

        //---------------------------------------------------------------------------------------------------- Declare buttons in bar ----------------------------------------------------------------------------------------------------//

        int xCoord = BAR_X-273;
        int yCoord = this.guiTop/2-70;

        SettingList.add(this.inject = new GuiToggleButton(xCoord,yCoord+2,11 * 16, 12 * 16,SecurityPermissions.INJECT.getUnlocalizedName(), SecurityPermissions.INJECT.getUnlocalizedTip()));
        SettingList.add(this.extract = new GuiToggleButton(xCoord+16,yCoord+2,11 * 16 + 1, 12 * 16 + 1,SecurityPermissions.EXTRACT.getUnlocalizedName(), SecurityPermissions.EXTRACT.getUnlocalizedTip()));
        SettingList.add(this.craft = new GuiToggleButton(xCoord+32,yCoord+2,11 * 16 + 2, 12 * 16 + 2,SecurityPermissions.CRAFT.getUnlocalizedName(), SecurityPermissions.CRAFT.getUnlocalizedTip()));

        for(int i=0;i<3;i++){
            SettingList.add(this.item[i] = new GuiServerButton(xCoord+i*16,yCoord+20,Items,this));
            SettingList.add(this.fluid[i] = new GuiServerButton(xCoord+i*16,yCoord+38,Fluid,this));
            SettingList.add(this.gas[i] = new GuiServerButton(xCoord+i*16,yCoord+56,Gas,this));
            SettingList.add(this.essentia[i] = new GuiServerButton(xCoord+i*16,yCoord+72,Essentia,this));
            SettingList.add(this.energy[i] = new GuiServerButton(xCoord+i*16,yCoord+90,Energy,this));
            SettingList.add(this.mana[i] = new GuiServerButton(xCoord+i*16,yCoord+108,Mana,this));
        }

        SettingList.add(this.energyGrid = new GuiServerButton(xCoord+16, yCoord+126,EnergyGrid,this));

        for(GuiToggleButton btn : SettingList){
            if(btn instanceof GuiServerButton){
                GuiServerButton button = (GuiServerButton)btn;
                button.setAction(()->{

                    button.isActive = !button.isActive;

                    if(selectedNetwork instanceof NetworkGui) {
                        //NetworkHandler.sendToServer(new PacketServerFeedback(this.mInstance,button.isActive, ((NetworkGui) selectedNetwork).LinkedServer,
                         //       ((NetworkGui) selectedNetwork).dir, ((NetworkGui) selectedNetwork).networkPermissions));
                    }

                });
            }
        }
    }

    /**
     * Use only for tooltip drawing
     */
    @Override
    public void drawScreen(int mX, int mY, float pOpacity) {
        super.drawScreen(mX,mY,pOpacity);
        /*for(ServerGui g : Servers){
            if(g.isMouseOverButton(mX,mY)){
                drawHoveringText(g.getTip(),mX,mY,fontRendererObj);
            }
        }*/
        for(GuiToggleButton btn : SettingList){
            if(btn instanceof GuiServerButton){
                GuiServerButton button = (GuiServerButton)btn;
                if(button.visible){
                    if(button.isMouseOverButton(mX,mY)){
                        drawHoveringText(button.getTip(),mX,mY,fontRenderer);
                    }
                }
            }
        }
        for(NetworkGui g : netInterface.values()){
            if(g.isMouseOverButton(mX,mY)){
                drawHoveringText(g.getTip(),mX,mY,fontRenderer);
            }
        }
    }

    /**
     * Use only for gui drawing
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        this.fontRenderer.drawString(I18n.translateToLocal("ME Server Network Tracer"), 9, 2, 4210752);

        for(ServerGui sG : ServerMap.values()){
            if(sG != null) {
                if (sG.isMainServer) {
                    for(NetworkGui nG : sG.LinkedNetworks){
                        if(nG != null) {
                            PortDirections portDirections = portDirection(nG.dir);

                            int offset = 55;

                            nG.x = (int) (Master_CenterX + portDirections.offsetX * offset * 1.5);
                            nG.y = Master_CenterY + portDirections.offsetY * offset;

                            nG.isLinked = true;

                            if (nG.isLinked) {

                                Color lineColor = new Color(0, 0, 0, 255);

                                GL11.glPushMatrix();

                                int l = (int) (Master_CenterX + portDirections.offsetX * 37 * 1.5);

                                if (nG.dir == EnumFacing.UP) {
                                    this.drawHorizontalLine(Master_CenterX, l, Master_CenterY + 10, lineColor.getRGB());
                                    this.drawVerticalLine(l, Master_CenterY + 10, nG.y - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.x + 8, l, nG.y - 6, lineColor.getRGB());
                                } else if (nG.dir == EnumFacing.NORTH) {

                                    this.drawHorizontalLine(Master_CenterX, l - 10, Master_CenterY + 8, lineColor.getRGB());
                                    this.drawVerticalLine(l - 10, Master_CenterY + 8, nG.y - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.x + 8, l - 10, nG.y - 6, lineColor.getRGB());
                                } else if (nG.dir == EnumFacing.DOWN) {

                                    this.drawHorizontalLine(Master_CenterX, l + 10, Master_CenterY - 10, lineColor.getRGB());
                                    this.drawVerticalLine(l + 10, Master_CenterY - 10, nG.y - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.x + 8, l + 10, nG.y - 6, lineColor.getRGB());

                                } else if (nG.dir == EnumFacing.WEST) {

                                    this.drawHorizontalLine(Master_CenterX, l - 5, Master_CenterY - 10, lineColor.getRGB());
                                    this.drawVerticalLine(l - 5, Master_CenterY - 10, nG.y - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.x + 8, l - 5, nG.y - 6, lineColor.getRGB());

                                } else if (nG.dir == EnumFacing.SOUTH) {

                                    this.drawHorizontalLine(Master_CenterX, l + 10, Master_CenterY + 8, lineColor.getRGB());
                                    this.drawVerticalLine(l + 10, Master_CenterY + 8, nG.y - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.x + 8, l + 10, nG.y - 6, lineColor.getRGB());

                                } else if (nG.dir == EnumFacing.EAST) {

                                    this.drawHorizontalLine(Master_CenterX, l + 15, Master_CenterY + 10, lineColor.getRGB());
                                    this.drawVerticalLine(l + 15, Master_CenterY + 10, nG.y - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.x + 8, l + 15, nG.y - 6, lineColor.getRGB());

                                }

                                GL11.glPopMatrix();
                            }

                            nG.renderGui(1F);
                        }
                    }
                }

                sG.renderGui(0.3F);
            }
        }

        for(GuiToggleButton btn : this.SettingList){
            btn.visible = selectedNetwork != null;

            btn.drawButton(Minecraft.getMinecraft(),btn.x,btn.y, 0);
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
        ServerMap.put(data.id,this.MasterServerGui);
    }

    public void addNetwork(NetworkData data){
        if(!data.isServerNetwork) {

            PortDirections portDirections = portDirection(data.dir.getFacing());

            int offset = 55;

            NetworkGui nG = new NetworkGui((int)(Master_CenterX+portDirections.offsetX*offset*1.5),Master_CenterY+portDirections.offsetY*offset,this, id++, data.dir.getFacing(), data.id);

            nG.isLinked = true;

            this.netInterface.put(data.dir.getFacing(),nG);
            ServerGui sG = this.ServerMap.get(data.id);

            if(sG != null){
                sG.LinkedNetworks[data.dir.ordinal()] = nG;
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
