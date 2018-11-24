package AppliedIntegrations.Gui;

import appeng.api.config.SortOrder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;
/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class GuiButtonSortingMode
        extends AIStateButton
{
    private String tooltipSortBy = "";

    public GuiButtonSortingMode( final int ID, final int xPosition, final int yPosition, final int width, final int height )
    {
        super( ID, xPosition, yPosition, width, height, AEStateIconsEnum.SORT_MODE_ALPHABETIC, 0, 0, AEStateIconsEnum.REGULAR_BUTTON );
    }

    @Override
    public void getTooltip( final List<String> tooltip )
    {
        this.addAboutToTooltip( tooltip, ButtonToolTips.SortBy.getLocal(), EnumChatFormatting.GRAY + this.tooltipSortBy );
    }

    /**
     * Sets the buttons icon based on the specified comparator mode.
     *
     * @param mode
     */
    public void setSortMode( SortMode mode )
    {
        switch ( mode )
        {
            case ALPHABETIC:
                this.stateIcon = AEStateIconsEnum.SORT_MODE_ALPHABETIC;
                this.tooltipSortBy = ButtonToolTips.ItemName.getLocal();
                break;

            case NUMERIC:
                this.stateIcon = AEStateIconsEnum.SORT_MODE_AMOUNT;
                this.tooltipSortBy = ButtonToolTips.NumberOfItems.getLocal();
                break;
            case MOD:
                break;

        }
    }

    public void setSortMode( final SortOrder order )
    {
        switch ( order )
        {
            case AMOUNT:
                this.stateIcon = AEStateIconsEnum.SORT_MODE_AMOUNT;
                this.tooltipSortBy = StatCollector.translateToLocal( "gui.tooltips.appliedenergistics2.NumberOfItems" );
                break;
            case MOD:
                this.stateIcon = AEStateIconsEnum.SORT_MODE_MOD;
                this.tooltipSortBy = StatCollector.translateToLocal( "gui.tooltips.appliedenergistics2.Mod" );
                break;

            case NAME:
                this.stateIcon = AEStateIconsEnum.SORT_MODE_ALPHABETIC;
                this.tooltipSortBy = StatCollector.translateToLocal( "gui.tooltips.appliedenergistics2.ItemName" );
                break;

        }
    }

}
