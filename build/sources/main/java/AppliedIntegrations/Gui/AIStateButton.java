package AppliedIntegrations.Gui;

import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import com.google.common.base.Splitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import java.util.List;
/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIStateButton
        extends AIGuiButton
{
    /**
     * Button background
     */
    private IStateIconTexture backgroundIcon;

    /**
     * Offset from the top-left corner of the button to draw the icon.
     */
    private int iconXOffset;

    /**
     * Offset from the top-left corner of the button to draw the icon.
     */
    private int iconYOffset;

    /**
     * Icon to draw on the button
     */
    protected IStateIconTexture stateIcon;

    public AIStateButton(final int ID, final int xPosition, final int yPosition, final int buttonWidth, final int buttonHeight,
                         final IStateIconTexture icon, final int iconXOffset, final int iconYOffset, final IStateIconTexture backgroundIcon )
    {
        // Call super
        super( ID, xPosition, yPosition, buttonWidth, buttonHeight, "" );

        // Set the icon
        this.stateIcon = icon;

        // Set the offsets
        this.iconXOffset = iconXOffset;
        this.iconYOffset = iconYOffset;

        // Set background
        this.backgroundIcon = backgroundIcon;
    }

    private void drawScaledTexturedModalRect(	final int xPosition, final int yPosition, final int u, final int v, final int width, final int height,
                                                 final int textureWidth, final int textureHeight )
    {
        // No idea what this is
        float magic_number = 0.00390625F;

        // Calculate the UV's
        float minU = u * magic_number;
        float maxU = ( u + textureWidth ) * magic_number;
        float minV = v * magic_number;
        float maxV = ( v + textureHeight ) * magic_number;
        double dHeight = height;
        double dWidth = width;

        // Get the tessellator
        /*Tessellator tessellator = Tessellator.instance;

        // Start drawing
        tessellator.startDrawingQuads();

        // Top left corner
        tessellator.addVertexWithUV( xPosition, yPosition + dHeight, this.zLevel, minU, maxV );

        // Top right corner
        tessellator.addVertexWithUV( xPosition + dWidth, yPosition + dHeight, this.zLevel, maxU, maxV );

        // Bottom right corner
        tessellator.addVertexWithUV( xPosition + dWidth, yPosition, this.zLevel, maxU, minV );

        // Bottom left corner
        tessellator.addVertexWithUV( xPosition, yPosition, this.zLevel, minU, minV );

        // Draw
        tessellator.draw();*/
    }

    /**
     * Draws an icon to the screen.
     *
     * @param minecraftInstance
     * @param icon
     * @param xPos
     * @param yPos
     * @param iconWidth
     * @param iconHeight
     */
    protected void drawIcon(final Minecraft minecraftInstance, final IStateIconTexture icon, final int xPos, final int yPos, final int iconWidth,
                            final int iconHeight ) {
        // Bind the sheet
        minecraftInstance.getTextureManager().bindTexture(icon.getTexture());

        // Draw the icon
        this.drawScaledTexturedModalRect(xPos + this.iconXOffset, yPos + this.iconYOffset, icon.getU(), icon.getV(), iconWidth, iconHeight,
                icon.getWidth(), icon.getHeight());
    }

}
