package AppliedIntegrations.Gui.ServerGUI.SubGui;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * @Author Azazell
 */
public class ServerGui extends SubServerGui {

    public LinkedHashMap<AEPartLocation, NetworkGui> linkedNetworks = new LinkedHashMap<>();

    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server.png");

    private boolean renderOverlay;
    private float zoom;

    public boolean isMainServer;

    public ServerGui(final int ID, int pX, int pY, boolean option, GuiServerTerminal rootGUI) {
        super(ID, rootGUI, pX, pY,null);
        this.isMainServer = option;
    }

    @Override
    public void getTooltip(List<String> tooltip) {

    }

    public List<String> getTip(){
        // Create empty list
        List<String> tip = new ArrayList<>();

        // Fill list with tittle
        tip.add("ME Main Server");

        // Fill list with actual information
        tip.add("Connected: "+ linkedNetworks.values().size() +" / 6 Users");

        return tip;
    }

    @Override
    public void drawButton(final Minecraft minecraftInstance, final int x, final int y, float r) {
        // Full white
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Push matrix to GL, to isolate changes from main gui
        GL11.glPushMatrix();

        // Scale size to zoom
        GL11.glScalef(this.zoom, this.zoom, this.zoom);

        // Pass call to super-class function
        renderOverlay(renderOverlay);

        // Bind our gui texture
        minecraftInstance.renderEngine.bindTexture(texture);

        // Draw texture
        drawTexturedModalRect( x, y, 0, 0, 260,260 );

        // Pop matrix. Isolate changes from outer gui world
        GL11.glPopMatrix();
    }

    public void renderGui(float zoom) {
        // Change zoom
        this.zoom = zoom;

        // Call draw method
        drawButton(Minecraft.getMinecraft(), x, y, 0);
    }

    public int getCenterPointX(){
        return x - width/2;
    }

    public int getCenterPointY(){
        return y - height/2;
    }
}
