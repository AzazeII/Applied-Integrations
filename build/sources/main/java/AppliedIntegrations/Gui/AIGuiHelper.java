package AppliedIntegrations.Gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public final class AIGuiHelper {
    public static final AIGuiHelper INSTANCE = new AIGuiHelper();

    public static final int MOUSE_BUTTON_LEFT = 0;

    private AIGuiHelper() {

    }

    public final boolean isPointInGuiRegion(	final int top, final int left, final int height, final int width, final int pointX, final int pointY,
                                                final int guiLeft, final int guiTop ) {
        return this.isPointInRegion( top, left, height, width, pointX - guiLeft, pointY - guiTop );
    }

    public final boolean isPointInRegion( final int top, final int left, final int height, final int width, final int pointX, final int pointY )
    {
        return ( pointX >= left ) && ( pointX <= ( left + width ) ) && ( pointY >= top ) && ( pointY <= ( top + height ) );
    }
}