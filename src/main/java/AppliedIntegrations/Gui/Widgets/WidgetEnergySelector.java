package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class WidgetEnergySelector extends EnergyWidget {
    // True by default
    private boolean shouldRender = true;

    public WidgetEnergySelector(IEnergySelectorGui hostGUI, int xPos, int yPos) {
        super(hostGUI, xPos, yPos);
    }

    @Override
    public void drawWidget() {
        if( shouldRender ) {
            // Disable lighting
            GL11.glDisable( GL11.GL_LIGHTING );

            // Full white
            GL11.glColor3f( 1.0F, 1.0F, 1.0F );

            // Bind to the gui texture
            Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation( AppliedIntegrations.modid, "textures/gui/energy.io.bus.png" ) );

            // Draw this slot just like the center slot of the gui
            this.drawTexturedModalRect( this.xPosition, this.yPosition, 79, 39, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );

            // Check not null
            if( getCurrentStack() != null ) {
                // Draw the Energy
                this.drawEnergy();

                // Check if energy of stack isn't null
                if( getCurrentStack().getEnergy() != null ){
                    // Bind to the gui texture
                    Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation( AppliedIntegrations.modid, "textures/gui/slots/selection.png" ) );

                    // Get tesselator
                    Tessellator tessellator = Tessellator.getInstance();

                    // Get buffered renderer
                    BufferBuilder helper = tessellator.getBuffer();

                    // Start drawing quads
                    helper.begin(7, DefaultVertexFormats.POSITION_TEX);

                    // Width of overlay edges
                    final float width = 1.5F;

                    // Draw first edge
                    helper.pos(this.xPosition, this.yPosition, this.zLevel);
                    helper.pos(this.xPosition, this.yPosition + WIDGET_SIZE, this.zLevel);
                    helper.pos(this.xPosition + width, this.yPosition + WIDGET_SIZE, this.zLevel);
                    helper.pos(this.xPosition + width, this.yPosition, this.zLevel);

                    // Draw second edge
                    helper.pos(this.xPosition + WIDGET_SIZE, this.yPosition, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE, this.yPosition + WIDGET_SIZE, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE - width, this.yPosition + WIDGET_SIZE, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE - width, this.yPosition, this.zLevel);

                    // Draw third edge
                    helper.pos(this.xPosition, this.yPosition, this.zLevel);
                    helper.pos(this.xPosition, this.yPosition + width, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE, this.yPosition + width, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE, this.yPosition, this.zLevel);

                    // Draw fourth edge
                    helper.pos(this.xPosition, this.yPosition + WIDGET_SIZE, this.zLevel);
                    helper.pos(this.xPosition, this.yPosition + WIDGET_SIZE + width, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE, this.yPosition + WIDGET_SIZE + width, this.zLevel);
                    helper.pos(this.xPosition + WIDGET_SIZE, this.yPosition + WIDGET_SIZE, this.zLevel);

                    // End drawing quads
                    tessellator.draw();
                }
            }

            // Re-enable lighting
            GL11.glEnable( GL11.GL_LIGHTING );
        }

    }

    @Override
    public void onMouseClicked(@Nonnull EnergyStack energy) {
        // Get widget host
        IEnergySelectorGui selector = (IEnergySelectorGui)hostGUI;

        // Update energy of selector
        selector.setSelectedEnergy(energy.getEnergy());

        // Update energy amount of selector
        selector.setAmount(energy.getStackSize());
    }
}
