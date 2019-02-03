package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.IEnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import AppliedIntegrations.Utils.AIUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class WidgetEnergySelector
        extends EnergyWidgetBase
{
    /**
     * Thickness of the selector outline.
     */
    private static final int borderThickness = 1;

    /**
     * The number of iterations in the gradient
     */
    private static final int GRADIENT_COUNT = 15;

    /**
     * Color of the border while Energy is selected
     */
    private static final int selectorBorderColor = 0xFF00FFFF;

    /**
     * Array of colors that pulse behind the Energy
     */
    private int[] backgroundPulseGradient;

    /**
     * If true the amount will not be drawn, also if the stack is craftable
     * it will show the crafting text.
     */
    protected boolean hideAmount = false;

    public WidgetEnergySelector(final IEnergySelectorGui selectorGui, final IEnergyStack stack, final int xPos, final int yPos,
                                final EntityPlayer player )
    {
        // Call super
        super( selectorGui,stack,xPos,yPos,player );
    }

    /**
     * Draws the selector outline.
     *
     * @param posX
     * @param posY
     * @param width
     * @param height
     * @param color
     * @param thickness
     */
    private void drawHollowRectWithCorners( final int posX, final int posY, final int width, final int height, final int color, final int thickness )
    {
        // Calculate points

        // Ending X point of the right line
        int rightXEnd = posX + width;

        // Beginning X point of the right line
        int rightXBegin = rightXEnd - thickness;

        // Ending X point of the left line
        int leftXEnd = posX + thickness;

        // Ending Y point of the top line
        int topYEnd = posY + thickness;

        // Ending Y point of the bottom line
        int bottomYEnd = posY + height;

        // Beginning Y point of the bottom line
        int bottomYBegin = bottomYEnd - thickness;

        // Draw background gradient
        Gui.drawRect( posX, posY, rightXEnd, bottomYEnd, color );

        // Draw notches

        // Top-left notch
        Gui.drawRect( posX, posY, leftXEnd + 1, topYEnd + 1, selectorBorderColor );

        // Top-right notch
        Gui.drawRect( rightXEnd, posY, rightXBegin - 1, topYEnd + 1, selectorBorderColor );

        // Bottom-right notch
        Gui.drawRect( rightXEnd, bottomYEnd, rightXBegin - 1, bottomYBegin - 1, selectorBorderColor );

        // Bottom-left notch
        Gui.drawRect( posX, bottomYEnd, leftXEnd + 1, bottomYBegin - 1, selectorBorderColor );

        // Draw lines

        // Top side
        Gui.drawRect( posX, posY, rightXEnd, topYEnd, selectorBorderColor );

        // Bottom side
        Gui.drawRect( posX, bottomYBegin, rightXEnd, bottomYEnd, selectorBorderColor );

        // Left side
        Gui.drawRect( posX, posY, leftXEnd, bottomYEnd, selectorBorderColor );

        // Right side
        Gui.drawRect( rightXBegin, posY, rightXEnd, bottomYEnd, selectorBorderColor );
    }

    /**
     * Gets the background gradient color based on the current time.
     *
     * @return
     */
    private int getBackgroundColor()
    {
        // Get the current time, slowed down.
        int time = (int)( System.currentTimeMillis() / 45L );

        // Lerp the index
        int index = Math.abs( Math.abs( time % ( GRADIENT_COUNT * 2 ) ) - GRADIENT_COUNT );

        // Return the index
        return this.backgroundPulseGradient[index];
    }

    @Override
    protected void onStackChanged()
    {
        // Call super
        super.onStackChanged();

        if( !this.hasEnergy() )
        {
            return;
        }

        // Get the Energy color
        int EnergyColor = this.getStack().getEnergy().getColor();

        // Create the gradient using the Energy color, varying between opacities
        this.backgroundPulseGradient = AIGuiHelper.INSTANCE
                .createColorGradient( 0x70000000 | EnergyColor, 0x20000000 | EnergyColor, GRADIENT_COUNT + 1 );
    }

    /**
     * Draws the Energy icon and selector border if it is selected.
     */
    @Override
    public void drawWidget()
    {
        // Is the widget empty?
        if( !this.hasEnergy() )
        {
            return;
        }
        // Disable lighting
        GL11.glDisable( GL11.GL_LIGHTING );

        // Enable blending
        GL11.glEnable( GL11.GL_BLEND );

        // Set the blending mode to blend alpha
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );

        // No tint
        GL11.glColor3f( 1.0F, 1.0F, 1.0F );

        // Get the selected Energy
        LiquidAIEnergy selectedEnergy = ( (IEnergySelectorGui)this.hostGUI ).getSelectedEnergy();

        // Does the selected Energy match the widgets?
        if( selectedEnergy == this.getEnergy() )
        {
            // Draw the selection box
            this.drawHollowRectWithCorners( this.xPosition, this.yPosition,
                    AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE,
                    this.getBackgroundColor(), WidgetEnergySelector.borderThickness );
        }

        // Draw the Energy
        this.drawEnergy();

        // Get the amount and crafting
        long stackSize = this.getAmount();

        // Is there anything to draw?
        if( ( ( stackSize > 0 ) && !this.hideAmount ) )
        {
            // Text to draw
            String text;

            // Get the font renderer
            final FontRenderer fontRenderer = this.hostGUI.getFontRenderer();

            // Disable unicode if enabled
            final boolean unicodeFlag = fontRenderer.getUnicodeFlag();
            fontRenderer.setUnicodeFlag( false );


            // Set the scale
            float scale =  0.85f;

            // Set the position offset
            float positionOffset = 0 ;

            // Calculate position
            float posX = this.xPosition + positionOffset + AIWidget.WIDGET_SIZE;
            float posY = ( this.yPosition + positionOffset + AIWidget.WIDGET_SIZE ) - 1;
            text = ""+stackSize;



            // Draw it
            AIGuiHelper.drawScaledText( fontRenderer, text, scale, posX, posY );

            // Reset unicode status
            fontRenderer.setUnicodeFlag( unicodeFlag );
        }

        // Enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );

        // Disable blending
        GL11.glDisable( GL11.GL_BLEND );
    }

    /**
     * Called when the widget is clicked.
     */
    @Override
    public void onMouseClicked()
    {
        // Get this Energy
        LiquidAIEnergy widgetEnergy = this.getEnergy();

        // Get the selected Energy
        LiquidAIEnergy selectedEnergy = ( (IEnergySelectorGui)this.hostGUI ).getSelectedEnergy();

        // Did the selected Energy change?
        boolean changed = false;

        // Are both Energies the same?
        if( widgetEnergy == selectedEnergy )
        {
            // Is something selected?
            if( selectedEnergy != null )
            {
                // Unselect
                selectedEnergy = null;
                changed = true;
            }
        }
        else
        {
            // Set to the widget Energy
            selectedEnergy = widgetEnergy;
            changed = true;
        }

        // Was the selected Energy changed?
        if( changed )
        {
            // Play clicky sound
            AIUtils.playClientSound( null, "gui.button.press" );

            // Set the selected Energy
            ( (IEnergySelectorGui)this.hostGUI ).getContainer().setSelectedEnergy( selectedEnergy );
        }
    }

    /**
     * Sets whether or not to show the amount.
     *
     * @param hide
     * @return
     */
    public void setHideAmount( final boolean hide )
    {
        this.hideAmount = hide;
    }

}
