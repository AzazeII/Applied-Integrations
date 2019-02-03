package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.Gui.AIGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIGuiButton
        extends GuiButton
{
    public AIGuiButton(final int ID, final int xPosition, final int yPosition, final int width, final int height, final String text )
    {
        super( ID, xPosition, yPosition, width, height, text );
    }

    public AIGuiButton(final int ID, final int xPosition, final int yPosition, final String text )
    {
        super( ID, xPosition, yPosition, text );
    }

    /**
     * Called to get the tooltip for this button.
     *
     * @param tooltip
     * List to add tooltip string to.
     */
    public abstract void getTooltip( final List<String> tooltip );

    /**
     * Checks if the mouse is over this button.
     *
     * @param mouseX
     * @param mouseY
     * @return
     */
    public boolean isMouseOverButton( final int mouseX, final int mouseY )
    {
        return AIGuiHelper.INSTANCE.isPointInRegion( this.yPosition, this.xPosition, this.height, this.width, mouseX, mouseY );
    }

    /**
     * Custom texture drawing, ex: 123x257
     * @param texture
     * @param x
     * @param y
     * @param u
     * @param v
     * @param width
     * @param height
     * @param imageWidth
     * @param imageHeight
     * @param scale
     */
    public void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight, double scale) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        double minU = (double)u / (double)imageWidth;
        double maxU = (double)(u + width) / (double)imageWidth;
        double minV = (double)v / (double)imageHeight;
        double maxV = (double)(v + height) / (double)imageHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + scale*(double)width, y + scale*(double)height, 0, maxU, maxV);
        tessellator.addVertexWithUV(x + scale*(double)width, y, 0, maxU, minV);
        tessellator.addVertexWithUV(x, y, 0, minU, minV);
        tessellator.addVertexWithUV(x, y + scale*(double)height, 0, minU, maxV);
        tessellator.draw();
    }

}
