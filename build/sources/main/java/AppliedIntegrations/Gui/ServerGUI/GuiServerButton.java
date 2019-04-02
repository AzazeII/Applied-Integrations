package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHelper;
import appeng.client.gui.widgets.GuiToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiServerButton extends GuiToggleButton {

    private final ResourceLocation background = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Buttons/BackgroundLayer.png");

    private NetworkPermissions type;
    public boolean isActive;
    public ButtonAction action;

    private ServerPacketTracer rootGui;

    public GuiServerButton(int x, int y, NetworkPermissions type, ServerPacketTracer root) {
        super(x, y, 0, 0, "", "");
        this.type = type;
        rootGui = root;
    }
    public boolean isMouseOverButton( final int mouseX, final int mouseY )
    {
        return AIGuiHelper.INSTANCE.isPointInGuiRegion( this.y, this.x, 16, 16, mouseX, mouseY,rootGui.getLeft(),rootGui.getTop() );
    }
    //@Override
    public void drawButton( final Minecraft minecraftInstance, final int x, final int y )
    {
        if(visible) {
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);

            this.drawTexturedRect(background, x, y, 0, 0, 16, 16, 16, 16, 1F);
            this.drawTexturedRect(getTextureFromType(), x, y, 0, 0, 16, 16, 16, 16, 1F);
        }
    }

    private ResourceLocation getTextureFromType() {
        if(isActive) {
            return new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Buttons/"+type.name()+"On.png");
        }else{
            return new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/Buttons/"+type.name()+"Off.png");
        }
    }

    public void doAction(){
        if(action != null)
            this.action.run();
    }

    public void setAction(ButtonAction action){
        this.action = action;
    }

    public void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight, double scale) {
        /*Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        double minU = (double)u / (double)imageWidth;
        double maxU = (double)(u + width) / (double)imageWidth;
        double minV = (double)v / (double)imageHeight;
        double maxV = (double)(v + height) / (double)imageHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + scale*(double)width, y + scale*(double)height, 0, maxU, maxV);
        tessellator.addVertexWithUV(x + scale*(double)width, y, 0, maxU, minV);
        tessellator.addVertexWithUV(x, y, 0, minU, minV);
        tessellator.addVertexWithUV(x, y + scale*(double)height, 0, minU, maxV);
        tessellator.draw();*/
    }

    public List<String> getTip() {
        List<String> list = new ArrayList<>();

        list.add(this.type.name());

        if(type != NetworkPermissions.EnergyGrid) {
            list.add("Allow user to manage ");
            list.add(type.name() + " In network");
        }else{
            list.add("Allow user to use energy");
            list.add("From network");
        }

        return list;
    }
}
