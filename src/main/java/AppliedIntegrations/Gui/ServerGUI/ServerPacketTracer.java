package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerServerPacketTracer;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkGui;
import AppliedIntegrations.Gui.ServerGUI.SubGui.PortDirections;
import AppliedIntegrations.Gui.ServerGUI.SubGui.ServerGui;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.client.gui.widgets.GuiToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

public class ServerPacketTracer extends AIBaseGui {

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

    private LinkedHashMap<ForgeDirection, NetworkGui> netInterface = new LinkedHashMap<>();

    private GuiToggleButton[] energy = new GuiToggleButton[3];
    private GuiToggleButton[] item = new GuiToggleButton[3];
    private GuiToggleButton[] fluid = new GuiToggleButton[3];
    private GuiToggleButton[] essentia = new GuiToggleButton[3];
    private GuiToggleButton[] gas = new GuiToggleButton[3];
    private GuiToggleButton[] mana = new GuiToggleButton[3];

    private GuiToggleButton extract;
    private GuiToggleButton inject;
    private GuiToggleButton craft;

    private GuiToggleButton energyGrid;

    /**
     * Map of all server from server id
     */
    public LinkedHashMap<Integer,ServerGui> ServerMap = new LinkedHashMap<>();

    private boolean hasMaster;
    private int id=0;
    private ServerGui MasterServerGui;
    private AIGuiButton selectedNetwork;

    public ServerPacketTracer(ContainerServerPacketTracer container, TileServerCore master, int x, int y, int z, EntityPlayer player) {
        super(container);

    }
    @Override
    protected void mouseClicked(int mX, int mY, int e) {
        for(NetworkGui g : netInterface.values()){
            if(g != null)
                if(g.isMouseOverButton(mX,mY)) {
                    selectedNetwork = g;
                    return;
                }
        }
        selectedNetwork = null;
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

        int xCoord = BAR_X + 69;

        this.buttonList.add(this.inject = new GuiToggleButton(xCoord,this.guiTop+2,11 * 16, 12 * 16,SecurityPermissions.INJECT.getUnlocalizedName(), SecurityPermissions.INJECT.getUnlocalizedTip()));
        this.buttonList.add(this.extract = new GuiToggleButton(xCoord+16,this.guiTop+2,11 * 16 + 1, 12 * 16 + 1,SecurityPermissions.EXTRACT.getUnlocalizedName(), SecurityPermissions.EXTRACT.getUnlocalizedTip()));
        this.buttonList.add(this.craft = new GuiToggleButton(xCoord+32,this.guiTop+2,11 * 16 + 2, 12 * 16 + 2,SecurityPermissions.CRAFT.getUnlocalizedName(), SecurityPermissions.CRAFT.getUnlocalizedTip()));

        for(int i=0; i<3; i++) {
            this.buttonList.add(this.item[i] = new GuiToggleButton(xCoord+16*i,this.guiTop+20,0,16,"",""));
            this.buttonList.add(this.fluid[i] = new GuiToggleButton(xCoord+16*i,this.guiTop+38, 0,16,"",""));
            this.buttonList.add(this.gas[i] = new GuiToggleButton(xCoord+16*i,this.guiTop+56,0,16,"",""));
            this.buttonList.add(this.energy[i] = new GuiToggleButton(xCoord+16*i,this.guiTop+74,0,16,"",""));
            this.buttonList.add(this.mana[i] = new GuiToggleButton(xCoord+16*i,this.guiTop+92,0,16,"",""));

            this.SettingList.add(this.item[i]);
            this.SettingList.add(this.fluid[i]);
            this.SettingList.add(this.gas[i]);
            this.SettingList.add(this.energy[i]);
            this.SettingList.add(this.mana[i]);

        }

        this.buttonList.add(this.energyGrid = new GuiToggleButton(xCoord+16, this.guiTop+110,0,16,"",""));

        this.SettingList.add(this.inject);
        this.SettingList.add(this.extract);
        this.SettingList.add(this.craft);
        this.SettingList.add(this.energyGrid);

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
        for(NetworkGui g : netInterface.values()){
            if(g.isMouseOverButton(mX,mY)){
                drawHoveringText(g.getTip(),mX,mY,fontRendererObj);
            }
        }
        for(GuiToggleButton btn : SettingList){
            if(AIGuiHelper.INSTANCE.isPointInRegion( btn.yPosition, btn.xPosition, btn.height, btn.width, mX, mY )){
                if(btn.visible) {
                    List<String> list = new ArrayList<>();
                    list.add(btn.getMessage());

                    drawHoveringText(list, mX, mY, fontRendererObj);
                }
            }
        }

    }

    /**
     * Use only for gui drawing
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("ME Server Network Tracer"), 9, 2, 4210752);

        for(Object button : this.buttonList){
            if(button instanceof GuiButton){
                GuiButton guiButton = (GuiButton)button;
                guiButton.visible = selectedNetwork != null;
            }
        }
        for(ServerGui sG : ServerMap.values()){
            if(sG != null) {
                if (sG.isMainServer) {
                    for(NetworkGui nG : sG.LinkedNetworks){
                        if(nG != null) {
                            PortDirections portDirections = portDirection(nG.dir);

                            int offset = 55;

                            nG.xPosition = (int) (Master_CenterX + portDirections.offsetX * offset * 1.5);
                            nG.yPosition = Master_CenterY + portDirections.offsetY * offset;

                            nG.isLinked = true;

                            if (nG.isLinked) {

                                Color lineColor = new Color(0, 0, 0, 255);

                                GL11.glPushMatrix();

                                int l = (int) (Master_CenterX + portDirections.offsetX * 37 * 1.5);

                                if (nG.dir == ForgeDirection.UP) {
                                    this.drawHorizontalLine(Master_CenterX, l, Master_CenterY + 10, lineColor.getRGB());
                                    this.drawVerticalLine(l, Master_CenterY + 10, nG.yPosition - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.xPosition + 8, l, nG.yPosition - 6, lineColor.getRGB());
                                } else if (nG.dir == ForgeDirection.NORTH) {

                                    this.drawHorizontalLine(Master_CenterX, l - 10, Master_CenterY + 8, lineColor.getRGB());
                                    this.drawVerticalLine(l - 10, Master_CenterY + 8, nG.yPosition - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.xPosition + 8, l - 10, nG.yPosition - 6, lineColor.getRGB());
                                } else if (nG.dir == ForgeDirection.DOWN) {

                                    this.drawHorizontalLine(Master_CenterX, l + 10, Master_CenterY - 10, lineColor.getRGB());
                                    this.drawVerticalLine(l + 10, Master_CenterY - 10, nG.yPosition - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.xPosition + 8, l + 10, nG.yPosition - 6, lineColor.getRGB());

                                } else if (nG.dir == ForgeDirection.WEST) {

                                    this.drawHorizontalLine(Master_CenterX, l - 5, Master_CenterY - 10, lineColor.getRGB());
                                    this.drawVerticalLine(l - 5, Master_CenterY - 10, nG.yPosition - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.xPosition + 8, l - 5, nG.yPosition - 6, lineColor.getRGB());

                                } else if (nG.dir == ForgeDirection.SOUTH) {

                                    this.drawHorizontalLine(Master_CenterX, l + 10, Master_CenterY + 8, lineColor.getRGB());
                                    this.drawVerticalLine(l + 10, Master_CenterY + 8, nG.yPosition - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.xPosition + 8, l + 10, nG.yPosition - 6, lineColor.getRGB());

                                } else if (nG.dir == ForgeDirection.EAST) {

                                    this.drawHorizontalLine(Master_CenterX, l + 15, Master_CenterY + 10, lineColor.getRGB());
                                    this.drawVerticalLine(l + 15, Master_CenterY + 10, nG.yPosition - 6, lineColor.getRGB());
                                    this.drawHorizontalLine(nG.xPosition + 8, l + 15, nG.yPosition - 6, lineColor.getRGB());

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

            PortDirections portDirections = portDirection(data.dir);

            int offset = 55;

            NetworkGui nG = new NetworkGui((int)(Master_CenterX+portDirections.offsetX*offset*1.5),Master_CenterY+portDirections.offsetY*offset,this, id++, data.dir, data.id);

            nG.isLinked = true;

            this.netInterface.put(data.dir,nG);
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
}
