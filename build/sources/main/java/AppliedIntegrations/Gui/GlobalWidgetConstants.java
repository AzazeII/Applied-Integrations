package AppliedIntegrations.Gui;


import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class GlobalWidgetConstants
        extends AIScrollBarGui
{
    /**
     * Number of widgets per row.
     */
    protected static final int WIDGETS_PER_ROW = 9;

    /**
     * Number of rows per page.
     */
    protected static final int WIDGET_ROWS_PER_PAGE = 4;

    /**
     * Number of widgets per page.
     */
    protected static final int WIDGETS_PER_PAGE = GlobalWidgetConstants.WIDGETS_PER_ROW * GlobalWidgetConstants.WIDGET_ROWS_PER_PAGE;

    public GlobalWidgetConstants(final Container container )
    {
        super( container );
    }

}
