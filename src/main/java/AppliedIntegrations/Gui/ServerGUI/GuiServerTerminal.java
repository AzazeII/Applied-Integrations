package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.config.SecurityPermissions;
import appeng.client.gui.implementations.GuiSecurityStation;
import appeng.client.gui.widgets.GuiToggleButton;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


// TODO Rewrite this GUI, now it will be similar to normal security terminal.
// Instead of biometric cards it will accept networks cards.
/**
 * @Author Azazell
 */
public class GuiServerTerminal extends AIBaseGui {

    private static final int GUI_WIDTH = 192;

    // Height of bottom part(below network card slot) of this GUI
    private static final int GUI_HEIGH_BOTTOM = 64;

    // Height of top part(above network card slot) of this GUI
    private static final int GUI_HEIGH_TOP = 192;
    private static final int SLOT_BAR_HEIGH = 48;

    private ResourceLocation texture = new ResourceLocation(AppEng.MOD_ID , "textures/guis/terminal.png");

    public TileServerCore mInstance;
    public EntityPlayer player;

    private GuiToggleButton inject;
    private GuiToggleButton extract;
    private GuiToggleButton craft;

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

        // Calculate y position for buttons
        final int top = this.guiTop + this.ySize - 116;

        // Add buttons to list:
        this.buttonList.add( this.inject = new GuiToggleButton( this.guiLeft + 56, top, 11 * 16, 12 * 16, SecurityPermissions.INJECT
                .getUnlocalizedName(), SecurityPermissions.INJECT.getUnlocalizedTip() ) ); // (1) Write

        this.buttonList.add( this.extract = new GuiToggleButton( this.guiLeft + 56 + 18, top, 11 * 16 + 1, 12 * 16 + 1, SecurityPermissions.EXTRACT
                .getUnlocalizedName(), SecurityPermissions.EXTRACT.getUnlocalizedTip() ) ); // (2) Read

        this.buttonList.add( this.craft = new GuiToggleButton( this.guiLeft + 56 + 18 * 2, top, 11 * 16 + 2, 12 * 16 + 2, SecurityPermissions.CRAFT
                .getUnlocalizedName(), SecurityPermissions.CRAFT.getUnlocalizedTip() ) ); // (3) Craft
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
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH_BOTTOM);

        // Draw cell bar texture
        drawTexturedModalRect(guiLeft, guiTop + SLOT_BAR_HEIGH, 0, SLOT_BAR_HEIGH, GUI_WIDTH, SLOT_BAR_HEIGH);

        // Draw top(above cell bar) texture
        drawTexturedModalRect(guiLeft, guiTop + GUI_HEIGH_BOTTOM + SLOT_BAR_HEIGH, 0, GUI_HEIGH_BOTTOM, GUI_WIDTH, GUI_HEIGH_TOP);
    }
}