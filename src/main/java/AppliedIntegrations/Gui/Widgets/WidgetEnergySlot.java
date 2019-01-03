package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.AppliedCoord;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Gui.GuiTextureManager;
import AppliedIntegrations.Gui.IWidgetHost;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketClientFilter;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class WidgetEnergySlot
        extends EnergyWidgetBase
{

    public int id;
    private EntityPlayer player;
    public boolean shouldRender;

    public int x,y,z;

    public ForgeDirection d;
    public World w;

    public WidgetEnergySlot(final IWidgetHost hostGui, final EntityPlayer player,final int id, final int posX,
                            final int posY, final boolean shouldRender){
        this(hostGui,player,0,0,0,null,null,id,posX,posY,true);
    }
    public WidgetEnergySlot(final IWidgetHost hostGui, final EntityPlayer player,int x,int y,int z,ForgeDirection d,World w, final int id, final int posX,
                            final int posY, final boolean shouldRender){
        super(hostGui,null,posX,posY,player);
        this.player = player;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.d = d;
        this.w = w;


        this.shouldRender = shouldRender;
    }

    public WidgetEnergySlot(final IWidgetHost hostGui, final EntityPlayer player, final IEnergyMachine part, final int id, final int posX,
                            final int posY, final boolean shouldRender)
    {
        this( hostGui, player, 0,0,0,null,null ,id,posX, posY, true );
    }



    @Override
    public void drawWidget()
    {
        if( shouldRender )
        {
            // Disable lighting
            GL11.glDisable( GL11.GL_LIGHTING );

            // Enable blending
            GL11.glEnable( GL11.GL_BLEND );

            // Set the blend mode
            GL11.glBlendFunc( 770, 771 );

            // Full white
            GL11.glColor3f( 1.0F, 1.0F, 1.0F );

            // Bind to the gui texture
            Minecraft.getMinecraft().renderEngine.bindTexture( GuiTextureManager.ENERGY_IO_BUS.getTexture() );

            // Draw this slot just like the center slot of the gui
            this.drawTexturedModalRect( this.xPosition, this.yPosition, 79, 39, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );

            // Do we have an Energy?
            if( this.getEnergy() != null )
            {
                // Draw the Energy
                this.drawEnergy();
            }

            // Re-enable lighting
            GL11.glEnable( GL11.GL_LIGHTING );

            // Re-disable blending
            GL11.glDisable( GL11.GL_BLEND );

        }

    }

    public void mouseClicked( final LiquidAIEnergy energy ) {
        if(this.d != null) {
            setEnergy(energy, 1);
            NetworkHandler.sendToServer(new PacketClientFilter(x, y, z, d, w, energy, id));
        }
    }

    @Override
    public void onMouseClicked()
    {
        // Ignored
    }
}