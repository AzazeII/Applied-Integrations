package AppliedIntegrations.Gui.Widgets;


import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Gui.IWidgetHost;
import AppliedIntegrations.Gui.AIGuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class EnergyWidgetBase
        extends AIWidget
{

    /**
     * Stack this widget represents.
     */
    private final IEnergyStack EnergyStack;

    /**
     * Cached Energy name.
     */
    protected String EnergyName = "";

    /**
     * The current player.
     */
    private EntityPlayer player;

    /**
     * Color of the Energy as ARGB.
     */
    private byte[] EnergyColorBytes;

    public EnergyWidgetBase( final IWidgetHost hostGui, final IEnergyStack stack, final int xPos, final int yPos, final EntityPlayer player )
    {
        // Call super
        super( hostGui, xPos, yPos );

        // Set the player
        this.player = player;

        // Create the Energy stack
        this.EnergyStack = new EnergyStack();

        // Set the Energy
        this.setEnergy( stack );
    }

    /**
     * Clears the stack and optionally fires the on stack changed event.
     *
     * @param doUpdate
     */
    protected void clearStack( final boolean doUpdate )
    {
        this.EnergyStack.setAll( null, 0 );
        this.EnergyName = "";

        if( doUpdate )
        {
            this.onStackChanged();
        }
    }

    /**
     * Draws the Energy icon
     */
    protected void drawEnergy()
    {
        // Ensure there is an Energy to draw
        if( !this.EnergyStack.hasEnergy() )
        {
            return;
        }
        LiquidAIEnergy energy = EnergyStack.getEnergy();
            Minecraft.getMinecraft().renderEngine.bindTexture(energy.getImage());
            drawTexturedModalRect(this.xPosition+1,this.yPosition+1,1,1,16,16);
    }

    /**
     * Called when the stack changes.
     */
    protected void onStackChanged()
    {
        // Is there an Energy?
        if( EnergyStack.hasEnergy() )
        {
            // Get the Energy name
            this.EnergyName = this.EnergyStack.getEnergyName( this.player );

            // Get the color bytes
            this.EnergyColorBytes = AIGuiHelper.INSTANCE.convertPackedColorToARGBb( this.EnergyStack.getEnergy().getColor() );

            // Set full alpha
            this.EnergyColorBytes[0] = (byte)255;
        }
        else
        {
            // Clear the info
            this.clearStack( false );
        }
    }

    /**
     * Clears the stack.
     */
    public void clearWidget()
    {
        this.clearStack( true );
    }

    /**
     * Gets the stack size.
     *
     * @return
     */
    public long getAmount()
    {
        return this.EnergyStack.getStackSize();
    }

    /**
     * Gets the Energy stack for this widget.
     *
     * @return
     */
    public LiquidAIEnergy getEnergy()
    {
        return this.EnergyStack.getEnergy();
    }

    /**
     * Returns the Energy stack.
     *
     * @return
     */
    public IEnergyStack getStack()
    {
        return this.EnergyStack;
    }

    /**
     * Draws the Energy name and amount
     */
    @Override
    public void getTooltip( final List<String> tooltip )
    {
        if( this.hasEnergy() )
        {
            // Add the name
            tooltip.add( this.EnergyStack.getStackSize() + this.EnergyName );

        }
    }

    /**
     * Returns true if the widget has an Energy.
     *
     * @return
     */
    public boolean hasEnergy()
    {
        return this.EnergyStack.hasEnergy();
    }

    /**
     * Set's the Energy stack based on the passed values and par manipulated on.
     *
     * @param Energy
     * @param amount
     */
    public void setEnergy(final LiquidAIEnergy Energy, final long amount )
    {
        // Set the Energy
        this.EnergyStack.setAll( Energy, amount );
        this.onStackChanged();
    }

    /**
     * Sets the Energy based on the stack
     *
     * @param stack
     */
    public void setEnergy( final IEnergyStack stack )
    {
        // Copy the values
        this.EnergyStack.setAll( stack );

        this.onStackChanged();
    }
}
