package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import net.minecraft.client.Minecraft;
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
                    // Bind to the overlay texture
                    Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation( AppliedIntegrations.modid, "textures/gui/slots/selection.png" ) );

                    // Draw energy overlay
                    this.drawTexturedModalRect( this.xPosition, this.yPosition, 0, 0, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );
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
