package AppliedIntegrations.Gui.ServerGUI.SubGui;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

public class ServerGui extends AIGuiButton {

    public Vector<NetworkGui> linkedNetworks = new Vector<>();
    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Server.png");

    private boolean renderOverlay;
    private float zoom;

    public boolean isMainServer;

    public ServerGui(final int ID, int pX, int pY, boolean option )
    {
        super(ID,pX,pY,null);
        this.isMainServer = option;
    }

    @Override
    public void getTooltip(List<String> tooltip) {

    }

    @Override
    public void drawButton( final Minecraft minecraftInstance, final int x, final int y )
    {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        GL11.glPushMatrix();

        GL11.glScalef(this.zoom,this.zoom,this.zoom);

        if (renderOverlay){

            Tessellator tessellator = Tessellator.instance;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(1, 1, 0, 1);
            tessellator.startDrawingQuads();

            // Left side
            tessellator.addVertex(x,y,0);
            tessellator.addVertex(x+width,y,0);
            tessellator.addVertex(x+width,y+height,0);
            tessellator.addVertex(x,y+height,0);


            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }

        minecraftInstance.renderEngine.bindTexture(texture);

        this.drawTexturedModalRect( x, y, 0, 0, 260,260 );

        GL11.glPopMatrix();
    }
    public void renderGui(float zoom) {
        this.zoom = zoom;
        drawButton(Minecraft.getMinecraft(),xPosition,yPosition);
    }

    public int getCenterPointX(){
        return xPosition - width/2;
    }

    public int getCenterPointY(){
        return yPosition - height/2;
    }

    public void mouseClicked() {

        if(renderOverlay == true)
            renderOverlay = false;
        else
            renderOverlay = true;
    }
}
