package AppliedIntegrations.Gui;


import AppliedIntegrations.Container.ContainerWithNetworkTool;
import AppliedIntegrations.Container.slot.SlotNetworkTool;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import appeng.api.AEApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POLYGON;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIBaseGui
        extends GuiContainer
        implements IWidgetHost
{
    private static final int upgradeU = AEStateIconsEnum.UPGRADE_CARD_BACKGROUND.getU();
    private static final int upgradeV = AEStateIconsEnum.UPGRADE_CARD_BACKGROUND.getV();
    private static final int upgradeWidth = AEStateIconsEnum.UPGRADE_CARD_BACKGROUND.getWidth();
    private static final int upgradeHeight = AEStateIconsEnum.UPGRADE_CARD_BACKGROUND.getHeight();

    /**
     * Lines to draw when drawTooltip is called.
     */
    protected final List<String> tooltip = new ArrayList<String>();

    public AIBaseGui(final Container container )
    {
        super( container );
    }

    /**
     * Adds to the tooltip based on which button the mouse is over.
     *
     * @param mouseX
     * @param mouseY
     * @return True when a tooltip was added, false otherwise.
     */
    private final boolean addTooltipFromButtons( final int mouseX, final int mouseY )
    {
        // Is the mouse over any buttons?
        for( Object obj : this.buttonList )
        {
            // Is it a base button?
            if( obj instanceof AIGuiButton)
            {
                // Cast
                AIGuiButton currentButton = (AIGuiButton)obj;

                // Is the mouse over it?
                if( currentButton.isMouseOverButton( mouseX, mouseY ) )
                {
                    // Get the tooltip
                    currentButton.getTooltip( this.tooltip );

                    // And stop searching
                    return true;
                }
            }
        }

        return false;
    }

    protected static final int GUI_MAIN_WIDTH = 176;


    protected static final int GUI_UPGRADES_WIDTH = 35;
    protected static final int GUI_UPGRADES_HEIGHT = 35;

    /**
     * Checks if the specified point is within the bounds of the specified slot.
     *
     * @param slot
     * @param x
     * @param y
     * @return True if the point is within the slot, false otherwise.
     */
    private final boolean isPointWithinSlot(final Slot slot, final int x, final int y )
    {
        return AIGuiHelper.INSTANCE.isPointInGuiRegion( slot.yPos, slot.xPos, 16, 16, x, y, this.guiLeft, this.guiTop );
    }

    /**
     * Checks if a not-left-click was on a button.<BR>
     * Left click is handled by GuiScreen.mouseClicked()
     *
     * @param mouseX
     * @param mouseY
     * @return True if click was handled.
     */
    private final boolean nonLeftClickHandler_Buttons( final int mouseX, final int mouseY, final int mouseButton )
    {
        if( mouseButton != AIGuiHelper.MOUSE_BUTTON_LEFT )
        {
            // Mouse over button?
            for( Object buttonObj : this.buttonList )
            {
                // Cast
                GuiButton button = (GuiButton)buttonObj;

                // Was mouse over the button?
                if( button.mousePressed( this.mc, mouseX, mouseY ) )
                {
                    // Play clicky sound
                    button.playPressSound( this.mc.getSoundHandler() );

                    // Call button click event
                    this.onButtonClicked( button, mouseButton );

                    // Handled
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Draws the slot backgrounds for the network tool and upgrade slots.
     *
     * @param alpha
     * @param mouseX
     * @param mouseY
     */
    protected final void drawAEToolAndUpgradeSlots( final float alpha, final int mouseX, final int mouseY )
    {
        Minecraft.getMinecraft().renderEngine.bindTexture( AEStateIconsEnum.AE_STATES_TEXTURE );

        // Locate any upgrade or network slots
        for( int i = 0; i < this.inventorySlots.inventorySlots.size(); i++ )
        {
            // Get the slot
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get( i );

            // Is it network or upgrade?
            if( ( slot instanceof SlotNetworkTool) )
            {
                // Draw background
                this.drawTexturedModalRect( this.guiLeft + slot.xPos-3, this.guiTop + slot.yPos-5, AIBaseGui.upgradeU,
                        AIBaseGui.upgradeV, AIBaseGui.upgradeWidth, AIBaseGui.upgradeHeight );
            }
        }
    }

    /**
     * Gets the slot who contains the specified point.
     *
     * @param x
     * @param y
     * @return Slot the point is within, null if point is within no slots.
     */
    protected final Slot getSlotAtMousePosition( final int x, final int y )
    {
        // Loop over all slots
        for( int i = 0; i < this.inventorySlots.inventorySlots.size(); i++ )
        {
            // Get the slot
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get( i );

            // Is the point within the slot?
            if( this.isPointWithinSlot( slot, x, y ) )
            {
                // Return the slot
                return slot;
            }
        }

        // Point was not within any slot
        return null;
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton )
    {

        if( this.nonLeftClickHandler_Buttons( mouseX, mouseY, mouseButton ) )
        {
            return;
        }

        // Is this container one that could have a network tool?
        if( this.inventorySlots instanceof ContainerWithNetworkTool )
        {
            // Do we have a network tool?
            if( ( (ContainerWithNetworkTool)this.inventorySlots ).hasNetworkTool() )
            {
                // Get the slot the mouse was clicked over
                Slot slot = this.getSlotAtMousePosition( mouseX, mouseY );

                // Was the slot the network tool?
                if( ( slot != null ) && ( slot.getStack() != null ) &&
                        ( slot.getStack().isItemEqual( AEApi.instance().definitions().items().networkTool().maybeStack( 1 ).get() ) ) )
                {
                    // Do not allow any interaction with the network tool slot.
                    return;
                }
            }
        }
        try {
            // Pass to super
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }catch(IOException e){

        }
    }

    /**
     * Called when a button is clicked. Includes which button was pressed.
     *
     * @param button
     * @param mouseButton
     */
    protected void onButtonClicked( final GuiButton button, final int mouseButton )
    {
    }

    /**
     * Called when a button is left-clicked<BR>
     * Note: Do not override, use {@link #onButtonClicked(GuiButton, int) onButtonClicked} instead.
     *
     * @see #onButtonClicked(GuiButton, int )
     */
    @Override
    public void actionPerformed( final GuiButton button )
    {
        this.onButtonClicked( button, AIGuiHelper.MOUSE_BUTTON_LEFT );
    }

    @Override
    public void drawScreen( final int mouseX, final int mouseY, final float mouseButton )
    {
        // Call super
        super.drawScreen( mouseX, mouseY, mouseButton );

        // Empty tooltip?
        if( this.tooltip.isEmpty() )
        {
            // Get the tooltip from the buttons
            this.addTooltipFromButtons( mouseX, mouseY );
        }

        // Draw the tooltip
        if( !this.tooltip.isEmpty() )
        {
            // Draw
            this.drawHoveringText( this.tooltip, mouseX, mouseY, this.fontRenderer );

            // Clear the tooltip
            this.tooltip.clear();
        }
    }
    protected void drawRoundedHollowBox(int x,int y,int height,int width,int color){
        drawVerticalLine(x-5, y-1-4, y-height+3, color);//left
        drawHorizontalLine(x, x+width-1, y-height, color);//upper line
        drawVerticalLine(x+4+width, y-1-4, y-height+3, color);//right
        drawHorizontalLine(x, x+width-1, y-2, color);//lower line
        for (int i=0;i<4;i++){
            drawRect(x-i, y-2-i, x-1-i, y-1-i, color);//lower left
            drawRect(x+i+width, y-2-i, x+1+i+width, y-1-i, color);//lower right
            drawRect(x-i, y-height+i, x-1-i, y+1-height+i, color);//upper left
            drawRect(x+i+width, y-height+i, x+1+width+i, y+1-height+i, color);//upper right
        }
    }
    @Override
    public final FontRenderer getFontRenderer()
    {
        return this.fontRenderer;
    }

    /**
     * Gets the starting X position for the Gui.
     */
    @Override
    public final int guiLeft()
    {
        return this.guiLeft;
    }

    /**
     * Gets the starting Y position for the Gui.
     */
    @Override
    public final int guiTop()
    {
        return this.guiTop;
    }
}
