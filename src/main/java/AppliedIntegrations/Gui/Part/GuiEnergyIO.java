package AppliedIntegrations.Gui.Part;

import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AEStateIconsEnum;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Buttons.GuiButtonAETab;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.PartEnergyExport;
import AppliedIntegrations.Parts.Energy.PartEnergyImport;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.Utils.getEnergyFromItemStack;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class GuiEnergyIO
        extends AIBaseGui implements IFilterGUI {


    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.io.bus.png");


    private static final int FILTER_GRID_SIZE = 3;

    private static final int WIDGET_X_POSITION = 61;
    private static final int WIDGET_Y_POSITION = 21;

    private static final int GUI_MAIN_WIDTH = 176;

    private static final int GUI_UPGRADES_WIDTH = 35;
    private static final int GUI_UPGRADES_HEIGHT = 86;

    private List<WidgetEnergySlot> energySlotList = new ArrayList<WidgetEnergySlot>();

    public AIOPart part;

    String stringName;
    public EntityPlayer player;
    private boolean[] configMatrix = {false,false,false,
                                    false,true,false,
                                    false,false,false};
    public GuiEnergyIO(Container container, EntityPlayer player) {
        super(container);
        this.player = player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize,this.ySize+75);

        // Draw upgrade slots
        this.drawTexturedModalRect( this.guiLeft + GUI_MAIN_WIDTH, this.guiTop, GUI_MAIN_WIDTH, 0,
                GUI_UPGRADES_WIDTH, GUI_UPGRADES_HEIGHT );

    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Call super
        super.drawGuiContainerForegroundLayer( mouseX, mouseY );

        // Should overlay be rendered
        boolean hoverUnderlayRendered = false;

        // Current widget under mouse
        WidgetEnergySlot slotUnderMouse = null;

        // Iterate over widgets
        for( WidgetEnergySlot slotWidget : energySlotList ) {
            // Check if overlay not rendering, slot widget should render and mouse is under widget
            if( ( !hoverUnderlayRendered ) && ( slotWidget.shouldRender ) && ( slotWidget.isMouseOverWidget( mouseX, mouseY ) ) ) {
                // Draw widget's underlay
                slotWidget.drawMouseHoverUnderlay();

                // Update slot under mouse
                slotUnderMouse = slotWidget;

                // trigger boolean
                hoverUnderlayRendered = true;
            }

            // Draw widget
            slotWidget.drawWidget();
        }
        //
        // Should we get the tooltip from the slot?
        if( slotUnderMouse != null ) {
            // Add the tooltip from the widget
            slotUnderMouse.getTooltip( this.tooltip );
        }

        if(part instanceof PartEnergyExport)
            this.stringName = I18n.translateToLocal("ME Energy Export Bus");
        if(part instanceof PartEnergyImport)
            this.stringName = I18n.translateToLocal("ME Energy Import Bus");

        this.fontRenderer.drawString(stringName, 9, 3, 4210752);
    }

    public void initGui() {
        super.initGui();

        // Add priority button
        this.buttonList.add( new GuiButtonAETab( 0, this.guiLeft +
                GuiEnergyStoragePart.BUTTON_PRIORITY_X_POSITION, this.guiTop-3, AEStateIconsEnum.WRENCH,
                "gui.appliedenergistics2.Priority" ) );

        // Calculate the index
        int index = 0;
        for( int row = 0; row < this.FILTER_GRID_SIZE; row++ )
        {
            for( int column = 0; column < this.FILTER_GRID_SIZE; column++ )
            {


                // Calculate the x position
                int xPos = this.WIDGET_X_POSITION + ( column * AIWidget.WIDGET_SIZE );

                // Calculate the y position
                int yPos = this.WIDGET_Y_POSITION + ( row * AIWidget.WIDGET_SIZE );

                this.energySlotList.add( new WidgetEnergySlot( this,
                        index, xPos, yPos, this.configMatrix[index]) );
                index++;
            }
        }

    }

    @Override
    public void drawScreen(int MouseX, int MouseY, float pOpacity) {
        super.drawScreen(MouseX, MouseY, pOpacity);

        renderHoveredToolTip(MouseX, MouseY);
    }

    @Override
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Call super
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (WidgetEnergySlot EnergySlot : this.energySlotList) {
            if (EnergySlot.isMouseOverWidget(mouseX, mouseY)) {
                // Get the Energy of the currently held item
                LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack());

                if (EnergySlot.getCurrentStack().getEnergy() == itemEnergy)
                    return;

                EnergySlot.onMouseClicked(new EnergyStack(itemEnergy, 0));

                break;
            }
        }
    }

    @Override
    public void updateEnergy(@Nonnull LiquidAIEnergy energy, int index) {
        this.energySlotList.get( index ).setCurrentStack( new EnergyStack(energy, 0) );
    }

    @Override
    public ISyncHost getSyncHost() {
        return part;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof AIOPart)
            part = (AIOPart)host;
    }

    public void updateState(boolean redstoneControl, byte upgradeCount, byte filterSize) {
        // Set filter matrix, from filter size
        if (filterSize == 0)
            // Update matrix
            configMatrix = new boolean[]{false, false, false,
                                         false, true,  false,
                                         false, false, false};
        if (filterSize == 1)
            // Update matrix
            configMatrix = new boolean[]{false, true, false,
                                         true,  true, true,
                                         false, true, false};

        if (filterSize == 2)
            // Update matrix
            configMatrix = new boolean[]{true, true, true,
                                         true, true, true,
                                         true, true, true};

        // Iterate for i until it equal to cM.length
        for(int i = 0; i < configMatrix.length; i++){
            // Get slot and update it to value from config matrix
            energySlotList.get(i).shouldRender = configMatrix[i];
        }

    }
}
