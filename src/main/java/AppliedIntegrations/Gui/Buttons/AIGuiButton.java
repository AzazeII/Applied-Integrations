package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.Gui.AIGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIGuiButton extends GuiButton {

    protected final static int backgroundU = 240;
    protected final static int backgroundV = backgroundU;

    public AIGuiButton(final int ID, final int xPosition, final int yPosition, final int width, final int height, final String text ) {
        super( ID, xPosition, yPosition, width, height, text );
    }

    public AIGuiButton(final int ID, final int xPosition, final int yPosition, final String text ) {
        super( ID, xPosition, yPosition, text );
    }
    // Tooltip on mouse hovered

    public abstract void getTooltip( final List<String> tooltip );
    // Check if mouse is over this widget

    public boolean isMouseOverButton( final int mouseX, final int mouseY ) {
        return AIGuiHelper.INSTANCE.isPointInRegion( this.y, this.x, this.height, this.width, mouseX, mouseY );
    }
}
