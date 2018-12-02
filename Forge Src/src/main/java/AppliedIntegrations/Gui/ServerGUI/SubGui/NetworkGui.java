package AppliedIntegrations.Gui.ServerGUI.SubGui;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Utils.AILog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class NetworkGui extends AIGuiButton {

    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Network.png");
    private ResourceLocation lightOff = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/NetworkAdapter_Off.png");
    private ResourceLocation lightOn = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/NetworkAdapter_On.png");

    public int LinkedServer;
    public ForgeDirection dir;
    public ServerPacketTracer root;

    public boolean isLinked;
    public float zoom;

    public NetworkGui(ServerPacketTracer rootGUI,int ID, ForgeDirection side, int linkedTo) {
        super(ID, 0,0,null);

        this.dir = side;
        this.LinkedServer = linkedTo;
        this.root = rootGUI;
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

        this.drawTexturedRect( texture, x, y, 0, 0, 16,16,16,16,this.zoom );

        if(!isLinked)
            this.drawTexturedRect(lightOff,x,y-14,0,0,16,16,16,16,this.zoom);
        else
            this.drawTexturedRect(lightOn,x,y-14,0,0,16,16,16,16,this.zoom);

        GL11.glPopMatrix();
    }

    public void renderGui(float zoom,int x,int y) {
        this.zoom = zoom;

        drawButton(Minecraft.getMinecraft(),x,y);
    }
}
