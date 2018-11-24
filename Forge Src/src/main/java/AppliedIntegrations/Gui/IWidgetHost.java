package AppliedIntegrations.Gui;

import AppliedIntegrations.Container.AIContainer;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;
/**
 * @Author Azazell
 */
public interface IWidgetHost
{
    /**
     * Gets the font renderer for the GUI.
     *
     * @return
     */
    @Nonnull
    FontRenderer getFontRenderer();

    /**
     * Return the left of the GUI.
     *
     * @return
     */
    int guiLeft();

    /**
     * Return the top of the GUI.
     *
     * @return
     */
    int guiTop();

    AIContainer getNodeContainer();
}
