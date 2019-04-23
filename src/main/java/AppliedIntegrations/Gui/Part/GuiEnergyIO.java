package AppliedIntegrations.Gui.Part;

import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import AppliedIntegrations.Network.Packets.PacketSyncReturn;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.PartEnergyExport;
import AppliedIntegrations.Parts.Energy.PartEnergyImport;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static AppliedIntegrations.API.Utils.getEnergyFromItemStack;
import static AppliedIntegrations.Gui.AIGuiHandler.GuiEnum.GuiAIPriority;

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
    private GuiImgButton redstoneControlBtn;

    public GuiEnergyIO(Container container, EntityPlayer player) {
        super(container, player);
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

        // Add tooltip to redstone control button
        // Check if mouse over redstone control button
        if(redstoneControlBtn.isMouseOver()){
            // Split messages using regex "\n"
            tooltip.addAll(Arrays.asList(redstoneControlBtn.getMessage().split("\n")));
        }

        if(part instanceof PartEnergyExport)
            this.stringName = I18n.translateToLocal("ME Energy Export Bus");
        if(part instanceof PartEnergyImport)
            this.stringName = I18n.translateToLocal("ME Energy Import Bus");

        this.fontRenderer.drawString(stringName, 9, 3, 4210752);
    }

    @Override
    public void initGui() {
        super.initGui();

        // Add priority button
        addPriorityButton();

        // Add redstone control button
        redstoneControlBtn = new GuiImgButton( this.guiLeft - 18, this.guiTop + 8, Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE );

        // Set visible to false
        redstoneControlBtn.setVisibility(false);

        // Add to button list
        buttonList.add(redstoneControlBtn);

        // Calculate the index
        int index = 0;
        for( int row = 0; row < this.FILTER_GRID_SIZE; row++ ) {
            for( int column = 0; column < this.FILTER_GRID_SIZE; column++ ) {


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
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton ) {
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

        // Check if mouse over redstone control button
        if(redstoneControlBtn.isMouseOver()) {
            // Get current mode ordinal
            short ordinal = (short) redstoneControlBtn.getCurrentValue().ordinal();

            // Switch to next mode
            redstoneControlBtn.set(ordinal == 3 ? RedstoneMode.IGNORE : RedstoneMode.values()[ordinal + 1]);

            // Send packet to client
            NetworkHandler.sendToServer(new PacketSyncReturn(redstoneControlBtn.getCurrentValue(), this.part));
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

    public void updateState(boolean redstoneControl, RedstoneMode redstoneMode, byte filterSize) {
        // Set filter matrix, from filter size
        if (filterSize == 0)
            // Update matrix
            this.configMatrix = new boolean[]{false, false, false,
                                         false, true,  false,
                                         false, false, false};
        if (filterSize == 1)
            // Update matrix
            this.configMatrix = new boolean[]{false, true, false,
                                         true,  true, true,
                                         false, true, false};

        if (filterSize == 2)
            // Update matrix
            this.configMatrix = new boolean[]{true, true, true,
                                         true, true, true,
                                         true, true, true};

        // Iterate for i until it equal to cM.length
        for(int i = 0; i < configMatrix.length; i++){
            // Get slot and update it to value from config matrix
            this.energySlotList.get(i).shouldRender = configMatrix[i];
        }

        // Set redstone control button visibility to redstone control
        this.redstoneControlBtn.setVisibility(redstoneControl);

        // Update redstone mode
        this.redstoneControlBtn.set(redstoneMode);
    }

    @Override
    public void onButtonClicked(final GuiButton btn, final int mouseButton) {
        // Avoid null pointer exception in packet
        if(part == null)
            return;

        // Check if click was performed on priority button
        if (btn == priorityButton){
            // Send packet to client
            NetworkHandler.sendToServer(new PacketGuiShift(GuiAIPriority, part));
        }
    }
}
