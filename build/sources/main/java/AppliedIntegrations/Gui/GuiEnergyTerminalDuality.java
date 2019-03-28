package AppliedIntegrations.Gui;


/**
 * @Author Azazell
 */
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerEnergyTerminal;
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

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.RF;

@SideOnly(Side.CLIENT)
public class GuiEnergyTerminalDuality
        extends PartGui
        implements IEnergySelectorGui {
    private static WidgetEnergySelector[] ArrayOfWidgets;
    private ResourceLocation mainTexture = new ResourceLocation(AppliedIntegrations.modid,"textures/gui/energy.terminal.png");

    @Nonnull
    private static ContainerEnergyTerminal LinkedContainer;
    private EntityPlayer player;
    private LiquidAIEnergy selectedEnergy = RF;
    private Long amount = 0L;
    private PartEnergyTerminal part;
    public IItemList<IAEEnergyStack> List;

    public GuiEnergyTerminalDuality(ContainerEnergyTerminal container,PartEnergyTerminal partEnergyTerminal, EntityPlayer player) {
        super(container);
        this.LinkedContainer = container;
        this.player = player;
        this.part = partEnergyTerminal;

        this.xSize = 195;
        this.ySize = 204;

        // Create the widgets
        this.ArrayOfWidgets = new WidgetEnergySelector[GlobalWidgetConstants.WIDGETS_PER_PAGE];

        // Rows
        for(int y = 0; y < GlobalWidgetConstants.WIDGET_ROWS_PER_PAGE; y++ )
        {
            // Columns
            for(int x = 0; x < GlobalWidgetConstants.WIDGETS_PER_ROW; x++ )
            {
                WidgetEnergySelector widget = new WidgetEnergySelector( this, null,
                        7 + ( x * 18 ),
                        17 + ( y * 18 ),
                        player );
                this.ArrayOfWidgets[( y * GlobalWidgetConstants.WIDGETS_PER_ROW ) + x] = widget;
            }
        }
    }

    @Nonnull
    @Override
    public IEnergySelectorContainer getContainer() {
        return this.LinkedContainer;
    }

    @Nullable
    @Override
    public LiquidAIEnergy getSelectedEnergy() {
        return this.selectedEnergy;
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
        if (this.selectedEnergy != null) {
            this.fontRenderer.drawString("Energy: " + this.selectedEnergy.getEnergyName(),
                    45, 101, 0);

            // Draw the amount
            this.fontRenderer.drawString("Amount: " + this.amount + "",
                    45, 91, 0);
        }
        WidgetEnergySelector test = new WidgetEnergySelector(this, new EnergyStack(RF, 1), 7, 18, player);
        test.drawWidget();
        for (WidgetEnergySelector widgetEnergySelector : this.ArrayOfWidgets) {
            widgetEnergySelector.drawWidget();
        }
    }

    @Override
    public AIContainer getNodeContainer() {
        return null;
    }
}
