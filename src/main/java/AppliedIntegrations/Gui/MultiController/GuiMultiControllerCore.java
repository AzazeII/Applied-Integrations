package AppliedIntegrations.Gui.MultiController;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Widgets.WidgetScrollbar;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.util.IConfigManager;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.ISortSource;
import appeng.core.localization.GuiText;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * @Author Azazell
 */
public class GuiMultiControllerCore extends AIBaseGui implements ISortSource, IConfigManagerHost {
	private static final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid,
			"textures/gui/multi_controller_card_storage.png");

	private WidgetScrollbar scroll;

	private IConfigManager configSource = new ConfigManager(this);

	private GuiImgButton sortModeButton;
	private GuiImgButton sortDirButton;
	private GuiImgButton viewModeButton;
	private ISyncHost core;

	public GuiMultiControllerCore(ContainerMultiControllerCore container, EntityPlayer p) {
		super(container, p);

		// Put default settings into config source
		configSource.registerSetting(Settings.SORT_BY, SortOrder.NAME); // Sort order (Name, amount, invTweaks, mod)
		configSource.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING); // Sort direction (ascending, descending)
		configSource.registerSetting(Settings.VIEW_MODE, ViewItems.ALL); // View mode (all, craft-able, stored)
	}

	private ContainerMultiControllerCore getContainer() {
		return (ContainerMultiControllerCore) inventorySlots;
	}

	public WidgetScrollbar getScroll() {
		return scroll;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		// Draw default
		drawDefaultBackground();

		// Set color
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Bind texture
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		// Draw texture
		drawTexturedModalRect(this.guiLeft, this.guiTop - 15, 0, 0, 195, 200);
	}

	@Override
	public void initGui() {
		super.initGui();

		// Init sort mode, sort dir and view mode buttons
		this.buttonList // 1. Sort mode
				.add( sortModeButton = new GuiImgButton( this.guiLeft - 18, this.guiTop + 8,
						Settings.SORT_BY, configSource.getSetting( Settings.SORT_BY ) ) );

		this.buttonList // 2. View mode
				.add( viewModeButton = new GuiImgButton( this.guiLeft - 18, this.guiTop + 28,
						Settings.VIEW_MODE, configSource.getSetting( Settings.VIEW_MODE ) ) );

		this.buttonList // 3. Sort direction
				.add( sortDirButton = new GuiImgButton( this.guiLeft - 18, this.guiTop + 48,
						Settings.SORT_DIRECTION, configSource.getSetting( Settings.SORT_DIRECTION)));

		this.scroll = new WidgetScrollbar(this, 175, 3);
		this.scroll.setMaxScroll(88);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		// Get current wheel value
		final int scroll = Mouse.getEventDWheel();

		// Check if wheel is scrolled
		if (scroll != 0) {
			// Pass call to scrollbar
			getContainer().scrollTo(this.scroll.onWheel(scroll));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Draw string
		this.fontRenderer.drawString(GuiText.inventory.getLocal(), 7, this.ySize - 71, 4210752); // (Player inv.)
		this.fontRenderer.drawString("ME Network Card Drive", 7, -12, 4210752); // (Server drive inv)

		// Draw scroll bar
		this.scroll.drawWidget();
	}

	@Override
	public Enum getSortBy() {
		return configSource.getSetting(Settings.SORT_BY);
	}

	@Override
	public Enum getSortDir() {
		return configSource.getSetting(Settings.SORT_DIRECTION);
	}

	@Override
	public Enum getSortDisplay() {
		return configSource.getSetting(Settings.VIEW_MODE);
	}

	@Override
	public ISyncHost getSyncHost() {
		return core;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		this.core = host;
	}

	@Override
	public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
		// Update settings of buttons
		sortModeButton.set( configSource.getSetting( Settings.SORT_BY ) ); // 1. Sort mode button
		sortDirButton.set( configSource.getSetting( Settings.SORT_DIRECTION ) ); // 2. Sort direction button
		viewModeButton.set( configSource.getSetting( Settings.VIEW_MODE ) ); // 3. View mode button

		// Update view of item storage repo
		//getContainer().itemStorage.updateView();
	}
}
