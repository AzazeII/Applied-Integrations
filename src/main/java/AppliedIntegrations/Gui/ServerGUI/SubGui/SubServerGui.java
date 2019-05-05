package AppliedIntegrations.Gui.ServerGUI.SubGui;

import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public abstract class SubServerGui extends AIGuiButton {
    protected final GuiServerTerminal root;

    public SubServerGui(int ID, GuiServerTerminal rootGUI, int xPosition, int yPosition, String text) {
        super(ID, xPosition, yPosition, text);
        this.root = rootGUI;
    }

    protected void renderOverlay(boolean renderOverlay) {
        // Check if gui should render
        if (renderOverlay) {
            // Get tessellator
            Tessellator tessellator = Tessellator.getInstance();

            // Get buffered builder
            BufferBuilder builder = tessellator.getBuffer();

            // Configure these options
            GL11.glEnable(GL11.GL_BLEND); // (1)
            GL11.glDisable(GL11.GL_TEXTURE_2D); // (2)

            // Configure blend function
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            // Change color
            GL11.glColor4f(1, 1, 0, 1);

            // Start drawing quads
            builder.begin(GL_QUADS, POSITION_TEX);

            // Draw quad
            builder.pos(x, y, 0).endVertex(); // (1) x, y, 0
            builder.pos(x + width, y, 0); // (2) x + width, y, 0
            builder.pos(x + width, y + height, 0); // (3) x + width, y + height, 0
            builder.pos(x, y + height, 0); // (4) x, y + height, 0

            // End drawing
            tessellator.draw();

            // Reverse states
            GL11.glEnable(GL11.GL_TEXTURE_2D); // (1)
            GL11.glDisable(GL11.GL_BLEND); // (2)
        }
    }

    @Override
    public boolean isMouseOverButton( final int mouseX, final int mouseY ) {
        return AIGuiHelper.INSTANCE.isPointInGuiRegion( this.y, this.x, 16, 16, mouseX, mouseY, root.getLeft(), root.getTop() );
    }
}
