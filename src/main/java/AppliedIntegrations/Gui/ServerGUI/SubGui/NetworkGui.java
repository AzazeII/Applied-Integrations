package AppliedIntegrations.Gui.ServerGUI.SubGui;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.NetworkPermissions;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import appeng.api.config.SecurityPermissions;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author Azazell
 */
public class NetworkGui extends AIGuiButton {

    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/network.png");
    private ResourceLocation lightOff = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/network_adapter_off.png");
    private ResourceLocation lightOn = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/network_adapter_on.png");

    public int LinkedServer;
    public EnumFacing dir;
    public GuiServerTerminal root;

    public boolean isLinked;
    public float zoom;
    private boolean renderOverlay;


    /**
     * All permissions of this network
     */
    public LinkedHashMap<SecurityPermissions,NetworkPermissions> networkPermissions = new LinkedHashMap<>();

    public NetworkGui(int posX, int posY, GuiServerTerminal rootGUI, int ID, EnumFacing side, int linkedTo) {
        super(ID, posX,posY,null);

        this.dir = side;
        this.LinkedServer = linkedTo;
        this.root = rootGUI;
    }


    @Override
    public void getTooltip(List<String> tooltip) {

    }
    /*@Override
    public void drawButton(final Minecraft minecraftInstance, final int x, final int y )
    {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        GL11.glPushMatrix();

        this.drawTexturedRect( texture, x, y, 0, 0, 16,16,16,16,this.zoom );

        if (renderOverlay){

            Tessellator tessellator = Tessellator.instance;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(1, 1, 0, 1);
            tessellator.startDrawingQuads();

            tessellator.addVertex(x,y,0);
            tessellator.addVertex(x+width,y,0);
            tessellator.addVertex(x+width,y+height,0);
            tessellator.addVertex(x,y+height,0);


            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }

        if(!isLinked)
            this.drawTexturedRect(lightOff,x,y-14,0,0,16,16,16,16,this.zoom);
        else
            this.drawTexturedRect(lightOn,x,y-14,0,0,16,16,16,16,this.zoom);

        GL11.glPopMatrix();
    }*/

    public boolean isMouseOverMarker( final int mouseX, final int mouseY) {
        return AIGuiHelper.INSTANCE.isPointInGuiRegion( this.y+16, this.x+4, 8, 4, mouseX, mouseY, root.getLeft(), root.getTop() );
    }

    @Override
    public boolean isMouseOverButton( final int mouseX, final int mouseY ) {
        return AIGuiHelper.INSTANCE.isPointInGuiRegion( this.y, this.x, 16, 16, mouseX, mouseY, root.getLeft(), root.getTop() );
    }
    public void renderGui(float zoom) {
        this.zoom = zoom;

        drawButton(Minecraft.getMinecraft(),this.x,this.y, 0);
    }

    public List<String> getTip() {
        List<String> tip = new ArrayList<>();
        tip.add("ME Network");
        tip.add("Connected On Direction: "+this.dir.name());
        return tip;
    }

    public void mouseClicked() {
        if(renderOverlay == true)
            renderOverlay = false;
        else
            renderOverlay = true;
    }
}
