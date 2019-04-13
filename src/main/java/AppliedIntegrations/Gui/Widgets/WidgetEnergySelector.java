package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import AppliedIntegrations.Gui.IWidgetHost;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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
            if( getCurrentEnergy() != null ) {
                // Draw the Energy
                this.drawEnergy();
            }

            // Re-enable lighting
            GL11.glEnable( GL11.GL_LIGHTING );
        }

    }

    @Override
    public void onMouseClicked(LiquidAIEnergy energy) {
        // Get widget host
        IEnergySelectorGui selector = (IEnergySelectorGui)hostGUI;

        // Update energy of selector
        selector.setSelectedEnergy(energy);
    }
}
