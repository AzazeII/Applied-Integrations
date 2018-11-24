package AppliedIntegrations.Gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIScrollBarGui
        extends AIBaseGui
{
    public class ScrollbarParams
    {
        /**
         * X position of the scroll bar
         */
        int scrollbarPosX;

        /**
         * Y position of the scroll bar
         */
        int scrollbarPosY;

        /**
         * Height of the scroll bar
         */
        int scrollbarHeight;

        /**
         * Position + Height of the scroll bar.
         */
        int scrollbarVerticalBound;

        /**
         * Create the parameters
         *
         * @param x
         * @param y
         * @param height
         */
        public ScrollbarParams( final int x, final int y, final int height )
        {
            this.scrollbarPosX = x;
            this.scrollbarPosY = y;
            this.setHeight( height );
        }

        /**
         * Sets the height of the scrollbar
         *
         * @param height
         */
        void setHeight( final int height )
        {
            this.scrollbarHeight = height;
            this.scrollbarVerticalBound = this.scrollbarHeight + this.scrollbarPosY;
        }
    }


    /**
     * True if the scroll bar has mouse focus.
     */
    private boolean isScrollBarHeld = false;
    /**
     * The last Y position of the mouse when the scroll bar has mouse focus.
     */
    private int scrollHeldPrevY = 0;

    /**
     * Scrollbar parameters
     */
    private ScrollbarParams scrollParams;

    public AIScrollBarGui(final Container container )
    {
        super( container );


    }

    /**
     * Draw the foreground layer.
     */
    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY )
    {
        // Call super
        super.drawGuiContainerForegroundLayer( mouseX, mouseY );


    }

    /**
     * Gets the scroll bar parameters from the subclass.
     *
     * @return
     */
    protected abstract ScrollbarParams getScrollbarParameters();

    /**
     * Called when the player types a key.
     */
    @Override
    protected void keyTyped( final char key, final int keyID )
    {
        // Home Key
        if( keyID == Keyboard.KEY_HOME )
        {

        }
        // End Key
        else if( keyID == Keyboard.KEY_END )
        {


        }
        // Up Key
        else if( keyID == Keyboard.KEY_UP )
        {

        }
        // Down Key
        else if( keyID == Keyboard.KEY_DOWN )
        {

        }
        else
        {
            super.keyTyped( key, keyID );
        }

    }

    /**
     * Called when the mouse is clicked while the gui is open
     */
    @Override
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Is the mouse over the scroll bar area?


        // Call super
        super.mouseClicked( mouseX, mouseY, mouseButton );
    }

    /**
     * Called when the mouse wheel is scrolled.
     *
     * @param deltaZ
     * @param mouseX
     * @param mouseY
     */
    protected abstract void onMouseWheel( int deltaZ, int mouseX, int mouseY );

    /**
     * Called when the scroll bar has moved.
     */
    protected abstract void onScrollbarMoved();

    /**
     * Changes the height of the scroll bar.
     *
     * @param newHeight
     */
    protected void setScrollBarHeight( final int newHeight )
    {

    }

    @Override
    public void drawScreen( final int mouseX, final int mouseY, final float mouseBtn )
    {
        // Call super
        super.drawScreen( mouseX, mouseY, mouseBtn );

        // Is the mouse holding the scroll bar?
        if( this.isScrollBarHeld )
        {
            // Is the mouse button still being held down?
            if( Mouse.isButtonDown( AIGuiHelper.MOUSE_BUTTON_LEFT ) )
            {
                // Has the Y changed?
                if( mouseY == this.scrollHeldPrevY )
                {
                    return;
                }

                boolean correctForZero = false;

                // Mark the Y
                this.scrollHeldPrevY = mouseY;

                // Calculate the Y position for the scroll bar
                int repY = mouseY - this.guiTop;

                // Has the mouse exceeded the 'upper' bound?
                if( repY > this.scrollParams.scrollbarVerticalBound )
                {
                    repY = this.scrollParams.scrollbarVerticalBound;
                }
                // Has the mouse exceeded the 'lower' bound?
                else if( repY <= this.scrollParams.scrollbarPosY )
                {
                    repY = this.scrollParams.scrollbarPosY;

                    // We will have to correct for zero
                    correctForZero = true;
                }

                // Update the scroll bar


                // Inform the subclass the scrollbar has moved
                this.onScrollbarMoved();
            }
            else
            {
                // The scroll bar no longer has mouse focus
                this.isScrollBarHeld = false;
            }
        }

    }

    @Override
    public void handleMouseInput()
    {
        // Call super
        super.handleMouseInput();

        // Get the delta z for the scroll wheel
        int deltaZ = Mouse.getEventDWheel();

        // Did it move?
        if( deltaZ != 0 )
        {
            // Get the mouse position
            int mouseX = ( Mouse.getEventX() * this.width ) / this.mc.displayWidth;
            int mouseY = this.height - ( ( Mouse.getEventY() * this.height ) / this.mc.displayHeight ) - 1;

            // Call event
            this.onMouseWheel( deltaZ, mouseX, mouseY );
        }
    }

    /**
     * Sets the gui up.
     */
    @Override
    public void initGui()
    {
        // Call super
        super.initGui();

        // Get the params
        this.scrollParams = this.getScrollbarParameters();


    }

}