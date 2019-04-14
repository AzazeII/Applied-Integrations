package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.IWidgetHost;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketClientToServerFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class WidgetEnergySlot
        extends EnergyWidget
{

    public int id;
    public boolean shouldRender;

    public WidgetEnergySlot(final IWidgetHost hostGui,final int id, final int posX,
                            final int posY, final boolean shouldRender){
        super(hostGui, posX, posY);
        this.id = id;

        this.shouldRender = shouldRender;
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
    public void onMouseClicked( @Nonnull final EnergyStack stack ) {
        // Check if slot is currently rendering
        if( !shouldRender )
            return;

        // Change stack
        setCurrentStack(stack);

        // Check not null
        if(hostGUI.getSyncHost() == null)
            // Return
            return;

        // Notify server
        NetworkHandler.sendToServer(new PacketClientToServerFilter(hostGUI.getSyncHost(), stack.getEnergy(), id));
    }
}