package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Buttons.GuiListTypeButton;
import AppliedIntegrations.Gui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.config.SortDir;
import appeng.api.config.ViewItems;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.widgets.IScrollSource;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.me.FluidRepo;
import appeng.client.me.InternalFluidSlotME;
import appeng.client.me.InternalSlotME;
import appeng.client.me.ItemRepo;
import appeng.core.localization.GuiText;
import appeng.fluids.client.gui.GuiFluidIO;
import appeng.fluids.util.AEFluidInventory;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


// TODO Rewrite this GUI, now it will be similar to normal security terminal.
// Instead of biometric cards it will accept networks cards.
/**
 * @Author Azazell
 */
public class GuiServerTerminal extends AIBaseGui implements IWidgetHost, IScrollSource, ISortSource {

    private static final int GUI_WIDTH = 192;
    private static final int GUI_HEIGH = 256;

    private static final int SLOT_Y = 18;
    private static final int SLOT_X = 9;

    private static final int SLOT_ROWS = 3;
    private static final int SLOT_COLUMNS = 9;

    private GuiSecurityPermissionsButton securityPermissionButton;
    private GuiStorageChannelButton storageChannelButton;
    private GuiListTypeButton listTypeButton;
    private final AEFluidInventory tank = new AEFluidInventory( null, 27 );

    /**
     * Get list of 81 widgets linked to given storage channel.
     * Each widget represent filter for material in given storage channel.
     */
    private LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>> channelWidgetMap = new LinkedHashMap<>();

    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_terminal.png");

    public TileServerCore mInstance;
    public EntityPlayer player;

    public GuiServerTerminal(ContainerServerTerminal container, EntityPlayer player) {
        super(container, player);

        this.player = player;
    }

    @Override
    public ISyncHost getSyncHost() {
        return mInstance;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof TileServerCore)
            mInstance = (TileServerCore)host;
    }

    @Override
    public void initGui() {
        super.initGui();

        // Add new security permissions button to button list
        buttonList.add(securityPermissionButton = new GuiSecurityPermissionsButton( 0, this.guiLeft + 72 + 32, this.guiTop + 86, 16, 16, ""));

        // Add new storage channel button to button list
        buttonList.add(storageChannelButton = new GuiStorageChannelButton( 0, this.guiLeft + 72 + 18 + 32, this.guiTop + 86, 16, 16, ""));

        // Add new black/white list button to button list
        buttonList.add(listTypeButton = new GuiListTypeButton( 0, this.guiLeft + 72 + 36 + 32, this.guiTop + 86, 16, 16, ""));


        // ************# Add Filter Slots #************ //
        // Iterate for each storage channel is list
        storageChannelButton.getChannelList().forEach( (chan) -> {
            // Get widget from API
            Constructor<? extends IChannelWidget> channelWidgetConstructor = Objects.requireNonNull(AIApi.instance()).getWidgetFromChannel(chan);

            // Check not null
            if (channelWidgetConstructor != null) {
                // List of widget for this channel
                List<IChannelWidget<?>> widgetList = new LinkedList<>();

                // Iterate for each column as Y
                for( int y = 0; y < SLOT_COLUMNS; y++ ) {
                    // Iterate for each row as X
                    for( int x = 0; x < SLOT_ROWS; x++ ) {
                        try {
                            // Try to construct with item slot constructor:
                            // I.E: public WidgetItemSlot(int x, int y)
                            widgetList.add(channelWidgetConstructor.newInstance(SLOT_X + 18 * x, SLOT_Y + 18 * y));

                            // Skip
                            continue;
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

                        }

                        try {
                            // Try to construct with fluid slot constructor:
                            // I.E: public WidgetFluidSlot(IAEFluidTank fluids, int slot, int id, int x, int y)
                            widgetList.add(channelWidgetConstructor.newInstance(tank, x + y, x + y, SLOT_X + 18 * x, SLOT_Y + 18 * y));

                            // Skip
                            continue;
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

                        }

                        try {
                            // Try to construct with AI constructor:
                            // I.E: public WidgetEnergySlot(final IWidgetHost hostGui,final int id, final int posX, final int posY, final boolean shouldRender)
                            widgetList.add(channelWidgetConstructor.newInstance(this, x + y + SLOT_ROWS, SLOT_X + 18 * x, SLOT_Y + 18 * y, true));
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

                        }
                    }
                }

                // Map list to channelWidgetMap
                channelWidgetMap.put(chan, widgetList);
            }
        });
        // ************# Add Filter Slots #************ //
    }

    @Override
    public void onButtonClicked(final GuiButton btn, final int mouseButton) {
        // Check if button is security permissions button
        if (btn == securityPermissionButton){
            // Cycle mode of button
            securityPermissionButton.cycleMode();

        // Check if button is storage channel button
        } else if (btn == storageChannelButton){
            // Cycle channel of button
            storageChannelButton.cycleChannel();

        // Check if button is black/white list button
        } else if (btn == listTypeButton){
            // Toggle mode of button
            listTypeButton.toggleMode();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY) {
        // Pass call to default function
        drawDefaultBackground();

        // Set color
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Bind our texture
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        // Draw bottom(below cell bar) texture
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);
    }

    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY ) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // Draw gui strings
        this.fontRenderer.drawString("Server Security Terminal", 8, 6, 4210752); // (Name)
        this.fontRenderer.drawString("Network Card Editor", 8, this.ySize - 96 + 3, 4210752); // (Editor)
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 36, 4210752); // (Player inv.)

        // Iterate for each storage channel is list
        storageChannelButton.getChannelList().forEach( (chan) -> {
            // Iterate for each element of list from storage channel map
            // Draw widget
            channelWidgetMap.get(chan).forEach(IChannelWidget::drawWidget);
        });
    }

    @Override
    public int getCurrentScroll() {
        return 0;
    }

    @Override
    public Enum getSortBy() {
        return null;
    }

    @Override
    public Enum getSortDir() {
        return SortDir.ASCENDING;
    }

    @Override
    public Enum getSortDisplay() {
        return ViewItems.STORED;
    }
}