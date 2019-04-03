package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.IWidgetHost;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketClientToServerFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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
    public void drawWidget()
    {
        if( shouldRender )
        {
            // Disable lighting
            GL11.glDisable( GL11.GL_LIGHTING );

            // Full white
            GL11.glColor3f( 1.0F, 1.0F, 1.0F );

            // Bind to the gui texture
            Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation( AppliedIntegrations.modid, "textures/gui/energy.io.bus.png" ) );

            // Draw this slot just like the center slot of the gui
            this.drawTexturedModalRect( this.xPosition, this.yPosition, 79, 39, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );

            // Check not null
            if( getCurrentEnergy() != null )
            {
                // Draw the Energy
                this.drawEnergy();
            }

            // Re-enable lighting
            GL11.glEnable( GL11.GL_LIGHTING );
        }

    }

    public void mouseClicked( final LiquidAIEnergy energy ) {
        // change energy
        setCurrentEnergy(energy);

        // Check not null
        if(hostGUI.getSyncHost() == null)
            // Return
            return;

        // Notify server
        NetworkHandler.sendToServer(new PacketClientToServerFilter(hostGUI.getSyncHost(), energy, id));
    }

    @Override
    public void onMouseClicked()
    {
        // Ignored
    }
}