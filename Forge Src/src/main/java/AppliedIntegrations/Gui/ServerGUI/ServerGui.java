package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ServerGui extends AIGuiButton {
    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Server.png");

    public int pX,pY;

    public ServerGui(final int ID, final String text )
    {
        super(ID,0,0,text);

    }

    @Override
    public void getTooltip(List<String> tooltip) {

    }

    @Override
    public void drawButton( final Minecraft minecraftInstance, final int x, final int y )
    {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        GL11.glPushMatrix();

        GL11.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );

        minecraftInstance.renderEngine.bindTexture( texture );

        this.drawTexturedModalRect( x, y, 0, 0, 260,260 );

        GL11.glPopMatrix();
    }

    public void renderGui() {
        drawButton(Minecraft.getMinecraft(),pX,pY);
    }
}
