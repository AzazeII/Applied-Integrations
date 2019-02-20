package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.Gui.AEStateIconsEnum;
import AppliedIntegrations.Gui.AIStateButton;
import AppliedIntegrations.Gui.IStateIconTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiButtonAETab
        extends AIStateButton
{
    /**
     * Height of the button
     */
    private static final int BUTTON_HEIGHT = AEStateIconsEnum.TAB_BUTTON.getHeight();

    /**
     * Width of the button
     */
    private static final int BUTTON_WIDTH = AEStateIconsEnum.TAB_BUTTON.getWidth();

    private static int ICON_X_POSITION = 3;

    private static int ICON_Y_POSITION = 3;

    private String tooltipMessageUnlocalized;

    /**
     * Icon to draw on the button
     */
    protected AEStateIconsEnum icon;

    public GuiButtonAETab(final int ID, final int xPosition, final int yPosition, final IStateIconTexture icon, final String unlocalizedTooltip )
    {
        // Call super
        super(	ID, xPosition, yPosition, GuiButtonAETab.BUTTON_WIDTH, GuiButtonAETab.BUTTON_HEIGHT, icon, GuiButtonAETab.ICON_X_POSITION,
                GuiButtonAETab.ICON_Y_POSITION, AEStateIconsEnum.TAB_BUTTON );

        // Set the tooltip
        this.tooltipMessageUnlocalized = unlocalizedTooltip;
    }

    @Override
    public void drawButton(final Minecraft minecraftInstance, final int x, final int y, int l )
    {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        // Draw the tab background
        this.drawIcon( minecraftInstance, AEStateIconsEnum.TAB_BUTTON, this.x, this.y, this.width, this.height );

        if( this.stateIcon != null )
        {
            // Draw the overlay icon
            this.drawIcon( minecraftInstance, this.stateIcon, this.x, this.y,
                    AEStateIconsEnum.STANDARD_ICON_SIZE, AEStateIconsEnum.STANDARD_ICON_SIZE );
        }

    }

    @Override
    public void getTooltip( final List<String> tooltip )
    {
        if( !this.tooltipMessageUnlocalized.equals( "" ) )
        {
            tooltip.add( I18n.translateToLocal( this.tooltipMessageUnlocalized ) );
        }
    }

}