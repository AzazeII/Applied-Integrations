package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import appeng.api.networking.IGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class NetworkGui extends AIGuiButton {
    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Network.png");

    private IGrid nWork;

    public NetworkGui(int ID, int xPosition, int yPosition) {
        super(ID, xPosition, yPosition, 16, 16,null);
    }

    public NetworkGui(int id, int xPosition, int yPosition, IGrid net) {
        this(id,xPosition,yPosition);
        this.nWork = net;
    }

    @Override
    public void getTooltip(List<String> tooltip) {

    }
    @Override
    public void drawButton(final Minecraft minecraftInstance, final int x, final int y )
    {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        GL11.glPushMatrix();
        GL11.glTranslatef( this.xPosition, this.yPosition, 0.0F );
        GL11.glScalef( 0.5f, 0.5f, 0.5f );

        if( this.enabled )
        {
            GL11.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
        }
        else
        {
            GL11.glColor4f( 0.5f, 0.5f, 0.5f, 1.0f );
        }

        minecraftInstance.renderEngine.bindTexture( texture );
        this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;


        this.drawTexturedModalRect( 0, 0, 0, 0, 16, 16 );
        this.drawTexturedModalRect( 0, 0, 0, 0, 16, 16 );
        this.mouseDragged( minecraftInstance, x, y );

        GL11.glPopMatrix();
    }
}
