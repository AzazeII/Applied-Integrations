package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Gui.IWidgetHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.List;

public abstract class EnergyWidget extends AIWidget {
    private LiquidAIEnergy currentEnergy;

    public EnergyWidget(IWidgetHost hostGUI, int xPos, int yPos) {
        super(hostGUI, xPos, yPos);
    }

    public LiquidAIEnergy getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(LiquidAIEnergy currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    protected void drawEnergy()
    {
        // Check not null
        if( currentEnergy == null ) {
            return;
        }

        // Bind energies texture
        Minecraft.getMinecraft().renderEngine.bindTexture(currentEnergy.getImage());

        // Draw energy
        drawTexturedModalRect(this.xPosition+1,this.yPosition+1,1,1,16,16);
    }

    @Override
    public void drawWidget() {

    }

    @Override
    public void getTooltip(List<String> tooltip) {
        // Check not null
        if(currentEnergy == null)
            return;

        // Add energy name
        tooltip.add(currentEnergy.getEnergyName());
    }
}
