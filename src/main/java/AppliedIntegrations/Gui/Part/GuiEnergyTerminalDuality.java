package AppliedIntegrations.Gui.Part;


/**
 * @Author Azazell
 */
import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySelector;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import appeng.api.storage.data.IItemList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.RF;

@SideOnly(Side.CLIENT)
public class GuiEnergyTerminalDuality extends AIBaseGui implements IEnergySelectorGui {
    private ResourceLocation mainTexture = new ResourceLocation(AppliedIntegrations.modid,"textures/gui/energy.terminal.png");

    @Nonnull
    private static ContainerEnergyTerminal LinkedContainer;

    private EntityPlayer player;

    @Nullable
    private LiquidAIEnergy selectedEnergy = null;
    private Long amount = 0L;

    private PartEnergyTerminal part;
    public IItemList<IAEEnergyStack> list;

    private static final int WIDGETS_PER_ROW = 9;

    private static final int WIDGET_ROWS_PER_PAGE = 4;

    private final List<WidgetEnergySelector> widgetEnergySelectors = new ArrayList<>();

    public GuiEnergyTerminalDuality(ContainerEnergyTerminal container,PartEnergyTerminal partEnergyTerminal, EntityPlayer player) {
        super(container);

        this.LinkedContainer = container;

        this.player = player;
        this.part = partEnergyTerminal;

        this.xSize = 195;
        this.ySize = 204;

        // Rows
        for(int y = 0; y < WIDGET_ROWS_PER_PAGE; y++ ) {
            // Columns
            for(int x = 0; x < WIDGETS_PER_ROW; x++ ) {
                // Update widget in array
                this.widgetEnergySelectors.add(new WidgetEnergySelector( this,
                        7 + ( x * 18 ),
                        17 + ( y * 18 )));

            }
        }
    }

    @Nonnull
    @Override
    public IEnergySelectorContainer getContainer() {
        return LinkedContainer;
    }

    @Nullable
    @Override
    public LiquidAIEnergy getSelectedEnergy() {
        return this.selectedEnergy;
    }

    @Override
    public void setSelectedEnergy(@Nullable LiquidAIEnergy energy) {
        this.selectedEnergy = energy;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        // Set the texture to the gui's texture
        Minecraft.getMinecraft().renderEngine.bindTexture( this.mainTexture );

        // Draw the gui
        this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX,int mouseY) {
        // Check not null
        if (this.selectedEnergy != null)
            // Draw energy name
            this.fontRenderer.drawString("Energy: " + this.selectedEnergy.getEnergyName(),
                    45, 101, 0);

        // Check stack size greater than zero
        if (this.amount > 0)
            // Draw energy amount
            this.fontRenderer.drawString("Amount: " + this.amount,
                    45, 91, 0);

        // Iterate for each widget
        // Draw each widget
        widgetEnergySelectors.forEach((WidgetEnergySelector::drawWidget));
    }

    @Override
    public ISyncHost getSyncHost() {
        return part;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof PartEnergyTerminal)
            part = (PartEnergyTerminal)host;
    }
}
