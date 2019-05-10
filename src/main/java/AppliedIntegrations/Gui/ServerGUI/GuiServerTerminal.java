package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiListTypeButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.ServerGUI.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketServerFeedback;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.config.SecurityPermissions;
import appeng.client.gui.implementations.GuiSecurityStation;
import appeng.container.implementations.ContainerSecurityStation;
import appeng.core.localization.GuiText;
import appeng.fluids.util.AEFluidInventory;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


// TODO Rewrite this GUI, now it will be similar to normal security terminal.
// Instead of biometric cards it will accept networks cards.
/**
 * @Author Azazell
 */
public class GuiServerTerminal extends AIBaseGui implements IWidgetHost {

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
     * Contains maps of lists of 27 widgets linked to given storage channel from given security permission.
     * Each widget represent filter for material in given storage channel.
     */
    private LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>>> permissionChannelWidgetMap = new LinkedHashMap<>();

    /**
     * Contains maps of modes linked to given storage channel from given security permissions
     */
    private LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> permissionChannelModeMap = new LinkedHashMap<>();

    private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_terminal.png");

    public TileServerCore mInstance;
    public EntityPlayer player;

    public GuiServerTerminal(ContainerServerTerminal container, EntityPlayer player) {
        super(container, player);

        this.player = player;
    }

    private ItemStack getCardStack() {
        // Check if container has card
        if (((ContainerServerTerminal)inventorySlots).hasCard()) {
            // Get card stack from container
            return ((ContainerServerTerminal) inventorySlots).getCard();
        }

        return null;
    }

    public boolean isCardValid() {
        // Check if container has card
        if (((ContainerServerTerminal)inventorySlots).hasCard()){
            // Get card stack from container
            ItemStack stack = getCardStack();

            // Return true if stack has NBT tag with record of network
            return Platform.openNbtData(stack).getBoolean(NetworkCard.NBT_KEY_HAS_NET);
        }

        return false;
    }

    public IncludeExclude getIncludeExcludeMode() {
        return permissionChannelModeMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel());
    }

    public void setIncludeExcludeMode(IncludeExclude mode) {
        permissionChannelModeMap.get(securityPermissionButton.getCurrentPermissions()).put(storageChannelButton.getChannel(), mode);
    }

    private void syncWithServer(NBTTagCompound tag) {
        NetworkHandler.sendToServer(new PacketServerFeedback(tag));
    }

    private void encodeCardTag() {
        // Create/get existing tag of card stack
        NBTTagCompound tag = Platform.openNbtData(getCardStack());

        // Now the fun begins
        // Iterate for each permission
        GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
            // Iterate for each storage channel
            GuiStorageChannelButton.getChannelList().forEach((chan) -> {
                // Create channel sub-nbt
                NBTTagCompound channelNBT = new NBTTagCompound();

                // Serialize black/white list mode
                channelNBT.setInteger("#SECURITY_ORDINAL", permissionChannelModeMap.get(securityPermissions).get(chan).ordinal());

                // Write size of list
                channelNBT.setInteger("#LIST_SIZE", permissionChannelWidgetMap.get(securityPermissions).get(chan).size());

                // Iterate for each filter
                permissionChannelWidgetMap.get(securityPermissions).get(chan).forEach((widget -> {
                    try {
                        // Check not null
                        if (widget.getAEStack() != null)
                            // Serialize stack with Api
                            Objects.requireNonNull(AIApi.instance()).getStackEncoder(chan).encode(channelNBT, widget.getAEStack());
                        else
                            // Encode "NaN"
                            channelNBT.setLong( "Cnt", -1);
                    } catch (IOException e) {
                        throw new IllegalStateException("Unexpected error");
                    }
                }));

                // Encode sub-tag
                tag.setTag("#SUB_TAG" + chan.hashCode() + securityPermissions.hashCode(), channelNBT);
            });
        }));

        syncWithServer(tag);
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
        buttonList.add(securityPermissionButton = new GuiSecurityPermissionsButton( this,0,
                this.guiLeft + 72 + 32, this.guiTop + 86, 16, 16, ""));

        // Add new storage channel button to button list
        buttonList.add(storageChannelButton = new GuiStorageChannelButton( this,0,
                this.guiLeft + 72 + 18 + 32, this.guiTop + 86, 16, 16, ""));

        // Add new black/white list button to button list
        buttonList.add(listTypeButton = new GuiListTypeButton( this, 0,
                this.guiLeft + 72 + 36 + 32, this.guiTop + 86, 16, 16, ""));

        // ************# Add Filter Slots #************ //
        // Iterate for each security permission
        GuiSecurityPermissionsButton.getPermissionList().forEach( (permissions -> {
            // Temp map
            LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IChannelWidget<?>>> tempMap = new LinkedHashMap<>();

            // Iterate for each storage channel is list
            GuiStorageChannelButton.getChannelList().forEach( (chan) -> {
                // Get widget from API
                Constructor<? extends IChannelWidget> channelWidgetConstructor = Objects.requireNonNull(AIApi.instance()).getWidgetFromChannel(chan);

                // Check not null
                if (channelWidgetConstructor != null) {
                    // List of widget for this channel
                    List<IChannelWidget<?>> widgetList = new LinkedList<>();

                    // Iterate for each row as X
                    for( int x = 0; x < SLOT_COLUMNS; x++ ) {
                        // Iterate for each column as Y
                        for (int y = 0; y < SLOT_ROWS; y++) {
                            try {
                                // Try to construct with item slot constructor:
                                // I.E: public WidgetItemSlot(IWidgetHost host, int x, int y)
                                widgetList.add(channelWidgetConstructor.newInstance(this, SLOT_X + 18 * x, SLOT_Y + 18 * y));

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
                                IChannelWidget<?> widget = channelWidgetConstructor.newInstance(this, x + y + SLOT_ROWS, SLOT_X + 18 * x, SLOT_Y + 18 * y, true);

                                // Check if widget is WidgetEnergySlot
                                if (widget instanceof WidgetEnergySlot){
                                    // Add shifted widget
                                    widgetList.add(channelWidgetConstructor.newInstance(this, x + y + SLOT_ROWS, SLOT_X - 1 + 18 * x, SLOT_Y - 1 + 18 * y, true));
                                } else {
                                    // Add original widget
                                    widgetList.add(widget);
                                }
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException ignore) {

                            }
                        }
                    }

                    // Map list to permissionChannelWidgetMap
                    tempMap.put(chan, widgetList);
                }
            });

            // Add temp map to main map
            permissionChannelWidgetMap.put(permissions, tempMap);
        }));
        // ************# Add Filter Slots #************ //

        // Iterate for each security permissions
        GuiSecurityPermissionsButton.getPermissionList().forEach((securityPermissions -> {
            // Temp map
            LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude> tempMap = new LinkedHashMap<>();

            // Iterate for each storage channel
            AEApi.instance().storage().storageChannels().forEach((chan -> {
                // Add default list mode to map
                tempMap.put(chan, AIConfig.defaultListMode);
            }));

            // Put temp map in permission channel map
            permissionChannelModeMap.put(securityPermissions, tempMap);
        }));
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        // Call super
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Iterate for each widget of current storage channel
        permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach((widget) -> {
            // Check if mouse is over widget
            if (widget.isMouseOverWidget(mouseX, mouseY)) {
                // Update stack
                widget.setAEStack(Objects.requireNonNull(AIApi.instance()).getAEStackFromItemStack(storageChannelButton.getChannel(), player.inventory.getItemStack()));

                // Encode card tag
                encodeCardTag();
            }
        });
    }

    @Override
    public void onButtonClicked(final GuiButton btn, final int mouseButton) {
        // Check if container has no network tool in slot
        if (!isCardValid())
            return;

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

            // Encode card tag
            encodeCardTag();
        }

        // Check not null
        if (getCardStack() == null)
            return;
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
    public void drawScreen(int mX, int mY, float pOpacity) {
        // Call parent class
        super.drawScreen(mX, mY, pOpacity);

        // Check if container has no network tool in slot
        if (!isCardValid())
            return;

        // Iterate for each element of list from current channel from map from current permission
        // Draw widget
        permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach((slot) -> {
            // Check if mouse over widget
            if (slot.isMouseOverWidget(mX, mY)) {
                // Create tooltip list
                List<String> tip = new ArrayList<String>();

                // Check if slot has energy stack
                if (slot.getAEStack() != null) {
                    // Add entry in list
                    tip.add(slot.getStackTip());

                    // Draw tooltip
                    drawHoveringText(tip, mX, mY, fontRenderer);
                }
            }
        });
    }

    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY ) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // Draw gui strings
        this.fontRenderer.drawString("Server Security Terminal", 8, 6, 4210752); // (Name)
        this.fontRenderer.drawString("Network Card Editor", 8, this.ySize - 96 + 3, 4210752); // (Editor)
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 36, 4210752); // (Player inv.

        // Check if container has no network tool in slot
        if (!isCardValid())
            return;

        // Iterate for each element of list from current channel from map from current permission
        // Draw widget
        permissionChannelWidgetMap.get(securityPermissionButton.getCurrentPermissions()).get(storageChannelButton.getChannel()).forEach(IChannelWidget::drawWidget);
    }
}