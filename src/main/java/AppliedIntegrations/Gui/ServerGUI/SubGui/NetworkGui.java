package AppliedIntegrations.Gui.ServerGUI.SubGui;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.ServerGUI.NetworkPermissions;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import appeng.api.config.SecurityPermissions;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.glLineWidth;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @Author Azazell
 */
public class NetworkGui extends SubServerGui {

    // ME Controller texture
    private ResourceLocation texture = new ResourceLocation(AppEng.MOD_ID, "textures/blocks/controller.png");

    private ResourceLocation lightOff = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/network_adapter_off.png");
    private ResourceLocation lightOn = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/network_adapter_on.png");

    public int linkedServerID;
    public EnumFacing dir;

    public boolean isLinked;
    public float zoom;
    private boolean renderOverlay;


    /**
     * All permissions of this network
     */
    public LinkedHashMap<SecurityPermissions,NetworkPermissions> networkPermissions = new LinkedHashMap<>();

    public NetworkGui(int posX, int posY, GuiServerTerminal rootGUI, int ID, EnumFacing side, int linkedTo) {
        super(ID, rootGUI, posX, posY,null);

        this.dir = side;
        this.linkedServerID = linkedTo;
    }

    private void drawLine(ServerGui server) {
        // Get tesselator
        Tessellator tessellator = Tessellator.getInstance();

        // Get builder
        BufferBuilder builder = tessellator.getBuffer();

        // Normalize coordinates
        GlStateManager.translate(0,0, 0);

        // Start drawing lines
        builder.begin(GL_LINES, POSITION_TEX);

        // Draw straight line
        builder.pos(463 - server.x, 227 - server.y, 0).endVertex();
        builder.pos(this.x  , this.y  , 0).endVertex();

        // End drawing
        tessellator.draw();
    }

    @Override
    public void getTooltip(List<String> tooltip) {

    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        // Isolate changes from outer render
        GL11.glPushMatrix();

        // Pass call to super-class function
        renderOverlay(renderOverlay);

        // Bind our texture
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        // Draw basic rect
        drawTexturedModalRect(x, y, 0, 0, 16,16);

        if(!isLinked) {
            // Bind texture with off marker
            Minecraft.getMinecraft().renderEngine.bindTexture(lightOff);
        } else {
            // Bind texture with on marker
            Minecraft.getMinecraft().renderEngine.bindTexture(lightOn);
        }

        // Draw textured rect
        drawTexturedModalRect(x, y - 14, 0, 0, 16, 16);

        // Draw cable connection to server
        drawLine(root.getServerFromID(linkedServerID));

        // Isolate changes from outer render
        GL11.glPopMatrix();
    }

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
