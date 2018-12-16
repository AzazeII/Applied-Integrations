package AppliedIntegrations.Gui;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Gui.Buttons.GuiButtonAETab;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;
import AppliedIntegrations.Parts.IO.PartEnergyExport;
import AppliedIntegrations.Parts.IO.PartEnergyImport;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class GuiEnergyIO
        extends PartGui implements IFilterGUI {
    ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.io.bus.png");


    private static final int FILTER_GRID_SIZE = 3;


    private byte filterSize;
    protected byte upgradeSpeedCount = 0;


    private static final int WIDGET_X_POSITION = 61;
    private static final int WIDGET_Y_POSITION = 21;

    private static final int GUI_HEIGHT = 184;

    private static final int GUI_WIDTH_NO_TOOL = 211;

    private static final int GUI_WIDTH_WITH_TOOL = 246;

    private static final int GUI_MAIN_WIDTH = 176;

    private static final int GUI_UPGRADES_WIDTH = 35;
    private static final int GUI_UPGRADES_HEIGHT = 86;

    private static final int TITLE_POS_X = 6, TITLE_POS_Y = 5;
    private List<WidgetEnergySlot> energySlotList = new ArrayList<WidgetEnergySlot>();

    public volatile AIOPart part;


    /**
     * Redstone Control button placement
     */
    private static final int REDSTONE_CONTROL_BUTTON_POS_Y = 2, REDSTONE_CONTROL_BUTTON_POS_X = -18, REDSTONE_CONTROL_BUTTON_SIZE = 16,
            REDSTONE_CONTROL_BUTTON_ID = 0;

    private List<LiquidAIEnergy> filter = new ArrayList<LiquidAIEnergy>();
    String stringName;
    public EntityPlayer player;
    private boolean[] configMatrix = {false,false,false,
                                    false,true,false,
                                    false,false,false};
    public GuiEnergyIO(Container container, EntityPlayer player) {
        super(container);
        this.player = player;

        if(part instanceof PartEnergyExport)
            this.stringName = StatCollector.translateToLocal("ME Energy Export Bus");
        if(part instanceof PartEnergyImport)
            this.stringName = StatCollector.translateToLocal("ME Energy Import Bus");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize,this.ySize+75);

        // Draw upgrade slots
        this.drawTexturedModalRect( this.guiLeft + this.GUI_MAIN_WIDTH, this.guiTop, this.GUI_MAIN_WIDTH, 0,
                this.GUI_UPGRADES_WIDTH, this.GUI_UPGRADES_HEIGHT );

    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(stringName, 9, 3, 4210752);
        // Call super
        super.drawGuiContainerForegroundLayer( mouseX, mouseY );

        boolean hoverUnderlayRendered = false;

        WidgetEnergySlot slotUnderMouse = null;
        for( int i = 0; i < 9; i++ )
        {
            WidgetEnergySlot slotWidget = this.energySlotList.get( i );

            if( ( !hoverUnderlayRendered ) && ( slotWidget.shouldRender ) && ( slotWidget.isMouseOverWidget( mouseX, mouseY ) ) )
            {
                AILog.chatLog(x+"");
                AILog.chatLog(y+"");
                AILog.chatLog(z+"");


                if(slotWidget.d == null){
                    AILog.chatLog("dir is null");
                    slotWidget.x = part.getX();
                    slotWidget.y = part.getY();
                    slotWidget.z = part.getZ();

                    slotWidget.w = this.player.worldObj;
                    slotWidget.d = part.getSide();
                }

                slotWidget.drawMouseHoverUnderlay();

                slotUnderMouse = slotWidget;

                hoverUnderlayRendered = true;
            }

            slotWidget.drawWidget();
        }
        // Should we get the tooltip from the slot?
        if( slotUnderMouse != null )
        {
            // Add the tooltip from the widget
            slotUnderMouse.getTooltip( this.tooltip );
        }
    }

    public void initGui() {
        super.initGui();
        this.buttonList.add( new GuiButtonAETab( 0, this.guiLeft +
                GuiEnergyStoragePart.BUTTON_PRIORITY_X_POSITION, this.guiTop-3, AEStateIconsEnum.WRENCH,
                "gui.appliedenergistics2.Priority" ) );
        for( int row = 0; row < this.FILTER_GRID_SIZE; row++ )
        {
            for( int column = 0; column < this.FILTER_GRID_SIZE; column++ )
            {
                // Calculate the index
                int index = ( row * this.FILTER_GRID_SIZE ) + column;

                // Calculate the x position
                int xPos = this.WIDGET_X_POSITION + ( column * AIWidget.WIDGET_SIZE );

                // Calculate the y position
                int yPos = this.WIDGET_Y_POSITION + ( row * AIWidget.WIDGET_SIZE );

                this.energySlotList.add( new WidgetEnergySlot( this, this.player,
                        index, xPos, yPos, this.configMatrix[index]) );
            }
        }

    }
    @Override
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Call super
        super.mouseClicked( mouseX, mouseY, mouseButton );

        // Loop over all widgets
        for( WidgetEnergySlot currentWidget : this.energySlotList )
        {
            if(this.part != null){
                if(currentWidget.w == null){
                    currentWidget.x = part.getX();
                    currentWidget.y = part.getY();
                    currentWidget.z = part.getZ();

                    currentWidget.w = part.getHostTile().getWorldObj();
                    currentWidget.d = part.getSide();
                }
            }

            // Is the mouse over this widget?
            if( currentWidget.isMouseOverWidget( mouseX, mouseY ) )
            {
                AILog.chatLog("called");

                // Get the Energy of the currently held item
                LiquidAIEnergy itemEnergy = Utils.getEnergyFromItemStack(player.inventory.getItemStack());

                // Is there an Energy?
                if( itemEnergy != null )
                {
                    // Are we already filtering for this Energy?
                    if( this.filter.contains( itemEnergy ) )
                    {

                        // Ignore
                        return;
                    }

                }else{
                    currentWidget.mouseClicked(null);
                }
                // Inform the slot it was clicked
                currentWidget.mouseClicked( itemEnergy );
                //part.updateFilter(0,itemEnergy);
                // Stop searching
                break;
            }
        }
    }

    @Override
    public void updateEnergies(@Nonnull LiquidAIEnergy energy, int index) {
        this.energySlotList.get( index ).setEnergy( energy, 1 );
    }
    public void onReceiveFilterSize( final byte filterSize )
    {
        // Inform our part
        this.part.onReceiveFilterSize( filterSize );

        this.filterSize = filterSize;

        for( int i = 0; i < this.energySlotList.size(); i++ )
        {
            WidgetEnergySlot slot = this.energySlotList.get( i );

            if( !slot.shouldRender )
            {
                slot.setEnergy( null );
            }

        }
    }

    @Override
    public AIContainer getNodeContainer() {
        return null;
    }


    @Override
    public void setX(int x){
        this.x = x;
        AILog.chatLog(x+"");
    }
}
