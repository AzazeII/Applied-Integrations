package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Gui.*;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;
import java.util.List;

import static AppliedIntegrations.API.LiquidAIEnergy.*;

public class PowerUnitsButton extends AIStateButton implements ITooltip {
    private IWidgetHost hostGUI;
    private LiquidAIEnergy currentEnergy;
    private boolean halfSize = false;
    private LinkedHashMap<LiquidAIEnergy, Integer> Index = new LinkedHashMap<LiquidAIEnergy, Integer>();

    public PowerUnitsButton(int ID, final IWidgetHost hostGUI, final int x, final int y, final LiquidAIEnergy val ) {
        super(	ID, x, y, 16, 16, null,64, 64, AEStateIconsEnum.TAB_BUTTON );
        this.hostGUI = hostGUI;
        this.currentEnergy = val;
        this.xPosition = x;
        this.yPosition = y;
        this.width = 16;
        this.height = 16;
        for(LiquidAIEnergy energy : LiquidAIEnergy.energies.values()){
            int i=0;
            Index.put(energy,i);
            i++;
        }
    }
    @Override
    public String getMessage()
    {
        return "PowerUnit";
    }

    @Override
    public int xPos() {
        return xPosition;
    }

    @Override
    public int yPos() {
        return yPosition;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void getTooltip(List<String> tooltip) {
            tooltip.add("PowerUnit: " + currentEnergy.getEnergyName());
            tooltip.add("Changes mode of energy view in the Energy bar");
    }
    public void setCurrentEnergy(LiquidAIEnergy energy){
        this.currentEnergy = energy;
    }
    public LiquidAIEnergy getCurrentEnergy(){
        return this.currentEnergy;
    }
    public boolean isMouseOverWidget( final int mouseX, final int mouseY )
    {
        return AIGuiHelper.INSTANCE.isPointInGuiRegion(
                this.xPosition, this.yPosition,
                AIWidget.WIDGET_SIZE - 1, AIWidget.WIDGET_SIZE - 1,
                mouseX, mouseY,
                this.hostGUI.guiLeft(), this.hostGUI.guiTop() );
    }
    @Override
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3 )
    {
                if( this.enabled )
                {
                    GL11.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
                }
                else
                {
                    GL11.glColor4f( 0.5f, 0.5f, 0.5f, 1.0f );
                }

                par1Minecraft.renderEngine.bindTexture(  new ResourceLocation("appliedintegrations","textures/gui/EnergyStates.png" ));
                this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
                int u;
                if(Index.containsValue(currentEnergy)) {
                    u = Index.get(this.currentEnergy);
                }else{
                    return;
                }

                this.drawTexturedModalRect( this.xPosition, this.yPosition, 0, 1, 16, 16 );
                // tesselator
                Tessellator ts = Tessellator.instance;
                double index = 0;
                if(currentEnergy == RF){
                    index = 0.5;
                }else if(currentEnergy == EU){
                    index = 0.2;
                }else if(currentEnergy == WA){
                    index = 0.4;
                }else if(currentEnergy == AE){
                    index = 0.1;
                }

                ts.startDrawingQuads();

                ts.addVertexWithUV(xPosition,yPosition+16,0,index,0);
                ts.addVertexWithUV(xPosition+16,yPosition+16,0,index,index);
                ts.addVertexWithUV(xPosition+16,yPosition,0,0,index);
                ts.addVertexWithUV(xPosition,yPosition,0,0,0);

                ts.draw();
                this.drawTexturedModalRect( this.xPosition, this.yPosition, u, 1, 16, 16 );
                // draw
                this.mouseDragged( par1Minecraft, par2, par3 );
                GL11.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
            }

    private int getIndex() {
        return 0;
    }

}
