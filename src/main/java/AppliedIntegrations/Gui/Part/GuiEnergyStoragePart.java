package AppliedIntegrations.Gui.Part;

import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyStorage;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.Utils.getEnergyFromItemStack;

@SideOnly(Side.CLIENT)
public class GuiEnergyStoragePart
        extends AIBaseGui
        implements IFilterGUI
{
    /**
     * The number of columns in the gui.
     */
    private static final int WIDGET_COLUMNS = 2;

    /**
     * The number of rows in the gui.
     */
    private static final int WIDGET_ROWS = 9;

    /**
     * The starting X position of the widgets.
     */
    private static final int WIDGET_X_POS = 13;

    /**
     * The starting Y position of the widgets.
     */
    private static final int WIDGET_Y_POS = 29;

    /**
     * The width of the gui with a network tool.
     */
    private static final int GUI_WIDTH_NETWORK_TOOL = 246;

    /**
     * The width of the gui without a network too
     */
    private static final int GUI_WIDTH_NO_TOOL = 210;

    /**
     * The height of the gui
     */
    private static final int GUI_HEIGHT = 184;

    /**
     * X position of the title string.
     */
    private static final int TITLE_X_POS = 6;

    /**
     * Y position of the title string.
     */
    private static final int TITLE_Y_POS = 5;

    /**
     * ID of the priority button
     */
    private static final int BUTTON_PRIORITY_ID = 0;

    /**
     * X offset position of the priority button
     */
    public static final int BUTTON_PRIORITY_X_POSITION = 151;

    private static final int BUTTON_ALLOW_VOID_ID = 1;

    private static final int BUTTON_ALLOW_VOID_X_POS = -18;

    private static final int BUTTON_ALLOW_VOID_Y_POS = 8;

    /**
     * Player viewing this gui.
     */
    private EntityPlayer player;

    /**
     * Filter widget list
     */
    private List<WidgetEnergySlot> energyWidgetList = new ArrayList<WidgetEnergySlot>();

    /**
     * Does the player have a network tool?
     */
    private boolean hasNetworkTool;

    /**
     * Storage bus associated with this gui
     */
    public volatile PartEnergyStorage storageBus;

    /**
     * Title of the gui
     */
    private final String guiTitle = I18n.translateToLocal("ME Energy Storage Bus");

    /**
     * Creates the GUI.
     *
     * @param storageBus
     * The part associated with the gui.
     * @param player
     * The inventory container.
     */
    public GuiEnergyStoragePart( ContainerEnergyStorage CEI,final PartEnergyStorage storageBus, final EntityPlayer player)
    {
        // Call super
        super( CEI );

        // Set the player
        this.player = player;

        // Set the storage bus
        this.storageBus = storageBus;

        // Set the network tool
        this.hasNetworkTool = ( (ContainerEnergyStorage)this.inventorySlots ).hasNetworkTool();

        this.xSize = ( this.hasNetworkTool ? GuiEnergyStoragePart.GUI_WIDTH_NETWORK_TOOL : GuiEnergyStoragePart.GUI_WIDTH_NO_TOOL );
        this.ySize = 251;


    }

    /**
     * Draws the gui background
     */
    @Override
    protected void drawGuiContainerBackgroundLayer( final float alpha, final int mouseX, final int mouseY )
    {
        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        // Set the texture
        Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation(AppliedIntegrations.modid,"textures/gui/energy.storage.bus.png" ));

        // Draw AppliedIntegrations gui
        this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, xSize, ySize );

        // Draw upgrade slot
        this.drawTexturedModalRect( this.guiLeft + 179, this.guiTop, 179, 0, 32, 32 );

        if( this.hasNetworkTool )
        {
            this.drawTexturedModalRect( this.guiLeft + 179, this.guiTop + 93, 178, 93, 68, 68 );
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY )
    {
        // Call super
        super.drawGuiContainerForegroundLayer( mouseX, mouseY );

        // Draw the title
        this.fontRenderer.drawString( this.guiTitle, GuiEnergyStoragePart.TITLE_X_POS, GuiEnergyStoragePart.TITLE_Y_POS, 0x000000 );

        WidgetEnergySlot slotUnderMouse = null;


        for( WidgetEnergySlot currentWidget : this.energyWidgetList)
        {
            if( ( slotUnderMouse == null ) && ( currentWidget.shouldRender ) && ( currentWidget.isMouseOverWidget( mouseX, mouseY ) ) )
            {
                // Set the slot
                slotUnderMouse = currentWidget;
            }

            // Draw the widget
            currentWidget.drawWidget();
        }

        // Should we get the tooltip from the slot?
        if( slotUnderMouse != null )
        {
            // Add the tooltip from the widget
            slotUnderMouse.getTooltip( this.tooltip );
        }
    }

    @Override
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton ) {
        // Call super
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (WidgetEnergySlot energySlot : this.energyWidgetList) {
            if (energySlot.isMouseOverWidget(mouseX, mouseY)) {
                // Get the Energy of the currently held item
                LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack());

                if (energySlot.getCurrentEnergy() == itemEnergy)
                    return;

                energySlot.mouseClicked(itemEnergy);

                break;
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        // Create widget @for_each row and @for_each column with zero index
        for( int row = 0; row < GuiEnergyStoragePart.WIDGET_COLUMNS; row++ )
        {
            for( int column = 0; column < GuiEnergyStoragePart.WIDGET_ROWS; column++ )
            {
                this.energyWidgetList.add( new WidgetEnergySlot( this, 0, this.WIDGET_X_POS + ( AIWidget.WIDGET_SIZE * column )-6,
                        this.WIDGET_Y_POS + ( AIWidget.WIDGET_SIZE * row ) -1,true));
            }
        }

        // Iterate from 0 to 17 and map all widgets
        for(int i = 0; i < PartEnergyStorage.FILTER_SIZE; i++) {
            // Change index
            energyWidgetList.get(i).id = i;
        }
    }


    @Override
    public void updateEnergy(final LiquidAIEnergy energy, int index )
    {
        this.energyWidgetList.get(index).setCurrentEnergy(energy);
    }

    @Override
    public ISyncHost getSyncHost() {
        return storageBus;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof PartEnergyStorage)
            storageBus = (PartEnergyStorage)host;
    }
}
