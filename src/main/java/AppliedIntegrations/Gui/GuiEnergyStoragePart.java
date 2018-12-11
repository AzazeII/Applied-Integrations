package AppliedIntegrations.Gui;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerEnergyStorage;
import AppliedIntegrations.Gui.Buttons.GuiButtonAETab;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;

import AppliedIntegrations.Parts.EnergyStorageBus.PartEnergyStorage;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiEnergyStoragePart
        extends AIBaseGui
        implements IFilterGUI,IPartGui
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
    private List<WidgetEnergySlot> EnergyWidgetList = new ArrayList<WidgetEnergySlot>();

    /**
     * Filter Energy list
     */
    private List<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>();

    /**
     * Does the player have a network tool?
     */
    private boolean hasNetworkTool;

    /**
     * Storage bus associated with this gui
     */
    public PartEnergyStorage storageBus;

    /**
     * Title of the gui
     */
    private final String guiTitle = StatCollector.translateToLocal("ME Energy Storage Bus");

    /**
     * Is the storage buss allowed to void excess Energy?
     */
    private boolean isVoidAllowed = false;

    public int x;
    public int y;
    public int z;
    public World world;
    public ForgeDirection side;



    /**
     * Creates the GUI.
     *
     * @param storageBus
     * The part associated with the gui.
     * @param player
     * The inventory container.
     */
    public GuiEnergyStoragePart( ContainerEnergyStorage CEI,final PartEnergyStorage storageBus, final EntityPlayer player,int x,int y,int z,ForgeDirection side )
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

        if(side != null) {
            this.x = x;
            this.y = y;
            this.z = z;

            this.world = player.getEntityWorld();
            this.side = side;

            AILog.chatLog(x+"");
            AILog.chatLog(y+"");
            AILog.chatLog(z+"");

        }
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
        Minecraft.getMinecraft().renderEngine.bindTexture( GuiTextureManager.ENERGY_STORAGE_BUS.getTexture() );

        // Draw AppliedIntegrations gui
        this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, xSize, ySize );

        // Draw upgrade slot
        this.drawTexturedModalRect( this.guiLeft + 179, this.guiTop, 179, 0, 32, 32 );

        if( this.hasNetworkTool )
        {
            this.drawTexturedModalRect( this.guiLeft + 179, this.guiTop + 93, 178, 93, 68, 68 );
        }

        // Call super
        super.drawAEToolAndUpgradeSlots( alpha, mouseX, mouseY );

    }

    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY )
    {
        // Call super
        super.drawGuiContainerForegroundLayer( mouseX, mouseY );

        // Draw the title
        this.fontRendererObj.drawString( this.guiTitle, GuiEnergyStoragePart.TITLE_X_POS, GuiEnergyStoragePart.TITLE_Y_POS, 0x000000 );

        WidgetEnergySlot slotUnderMouse = null;

        // Draw widgets
        for( WidgetEnergySlot currentWidget : this.EnergyWidgetList )
        {
            if( ( slotUnderMouse == null ) && ( currentWidget.shouldRender ) && ( currentWidget.isMouseOverWidget( mouseX, mouseY ) ) )
            {
                // Draw the underlay
                currentWidget.drawMouseHoverUnderlay();

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
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Call super
        super.mouseClicked( mouseX, mouseY, mouseButton );

        for( WidgetEnergySlot EnergySlot : this.EnergyWidgetList )
        {
            if( EnergySlot.isMouseOverWidget( mouseX, mouseY ) )
            {
                // Get the Energy of the currently held item
                LiquidAIEnergy itemEnergy = EnergySlot.getEnergy();

                // Is there an Energy?
                if( itemEnergy != null )
                {
                    // Are we already filtering for this Energy?
                    if( this.filteredEnergies.contains( itemEnergy ) )
                    {
                        // Ignore
                        return;
                    }

                }

                EnergySlot.mouseClicked( itemEnergy );

                break;
            }
        }
    }

    /**
     * Called when a button is clicked.
     */
    @Override
    protected void onButtonClicked(final GuiButton button, final int mouseButton )
    {
        // Was the priority button clicked?
        if( button.id == GuiEnergyStoragePart.BUTTON_PRIORITY_ID )
        {
            // Get the storage buses host
            TileEntity host = this.storageBus.getHostTile();

            // Get the side the storage bus is attached to
            ForgeDirection side = this.storageBus.getSide();


        }

    }

    @Override
    public void initGui()
    {
        super.initGui();

        // Create the widgets
        for( int row = 0; row < GuiEnergyStoragePart.WIDGET_COLUMNS; row++ )
        {
            for( int column = 0; column < GuiEnergyStoragePart.WIDGET_ROWS; column++ )
            {
                this.EnergyWidgetList.add( new WidgetEnergySlot( this, this.player,x,y,z,side,world,
                        ( row * this.WIDGET_COLUMNS ) + column,
                        this.WIDGET_X_POS + ( AIWidget.WIDGET_SIZE * column )-6,
                        this.WIDGET_Y_POS + ( AIWidget.WIDGET_SIZE * row ) -1,true));
            }
        }

        // Create the priority tab button
        this.buttonList.add( new GuiButtonAETab( GuiEnergyStoragePart.BUTTON_PRIORITY_ID, this.guiLeft +
                GuiEnergyStoragePart.BUTTON_PRIORITY_X_POSITION, this.guiTop-3, AEStateIconsEnum.WRENCH,
                "gui.appliedenergistics2.Priority" ) );

    }


    @Override
    public void updateEnergies( final LiquidAIEnergy energy, int index )
    {
        this.EnergyWidgetList.get(index).setEnergy(energy,0);
    }

    @Override
    public AIContainer getNodeContainer() {
        return null;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public ForgeDirection getSide() {
        return this.side;
    }

    @Override
    public World getWorld() {
        return this.world;
    }
}
