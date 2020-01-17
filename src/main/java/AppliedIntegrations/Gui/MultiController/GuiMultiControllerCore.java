package AppliedIntegrations.Gui.MultiController;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Gui.AIGui;
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
public class GuiMultiControllerCore extends AIGui implements ISortSource, IConfigManagerHost {
	private static final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid,
			"textures/gui/multi_controller_card_storage.png");
	private WidgetScrollbar.GuiScrollbar scroll;

	private IConfigManager configSource = new ConfigManager(this);

	private GuiImgButton sortModeButton;
	private GuiImgButton sortDirButton;
	private GuiImgButton viewModeButton;
	private ISyncHost core;

	public GuiMultiControllerCore(ContainerMultiControllerCore container, EntityPlayer p) {
		super(container, p);
		this.configSource.registerSetting(Settings.SORT_BY, SortOrder.NAME); // Sort order (Name, amount, invTweaks, mod)
		this.configSource.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING); // Sort direction (ascending, descending)
		this.configSource.registerSetting(Settings.VIEW_MODE, ViewItems.ALL); // View mode (all, craft-able, stored)
	}

	private ContainerMultiControllerCore getContainer() {
		return (ContainerMultiControllerCore) inventorySlots;
	}

	public WidgetScrollbar.GuiScrollbar getScroll() {
		return scroll;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(this.guiLeft, this.guiTop - 15, 0, 0, 195, 200);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add( sortModeButton = new GuiImgButton( this.guiLeft - 18, this.guiTop + 8,
						Settings.SORT_BY, configSource.getSetting( Settings.SORT_BY ) ) );

		this.buttonList.add( viewModeButton = new GuiImgButton( this.guiLeft - 18, this.guiTop + 28,
						Settings.VIEW_MODE, configSource.getSetting( Settings.VIEW_MODE ) ) );

		this.buttonList.add( sortDirButton = new GuiImgButton( this.guiLeft - 18, this.guiTop + 48,
						Settings.SORT_DIRECTION, configSource.getSetting( Settings.SORT_DIRECTION)));

		this.scroll = new WidgetScrollbar.GuiScrollbar(this, 175, 3);
		this.scroll.setRange((ContainerMultiControllerCore.CARD_SLOT_ROWS - ContainerMultiControllerCore.CARD_SLOT_VIEW_ROWS)
				* ContainerMultiControllerCore.CARD_SLOT_COLUMNS, ContainerMultiControllerCore.CARD_SLOT_COLUMNS);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		final int scroll = Mouse.getEventDWheel();
		if (scroll != 0) {
			getContainer().scrollTo(this.scroll.wheel(scroll));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		this.fontRenderer.drawString(GuiText.inventory.getLocal(), 7, this.ySize - 71, 4210752); // (Player inv.)
		this.fontRenderer.drawString("ME Network Card Drive", 7, -12, 4210752); // (Server drive inv)
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
		sortModeButton.set( configSource.getSetting( Settings.SORT_BY ) );
		sortDirButton.set( configSource.getSetting( Settings.SORT_DIRECTION ) );
		viewModeButton.set( configSource.getSetting( Settings.VIEW_MODE ) );

		// Update view of item storage repo
		//getContainer().itemStorage.updateView();
	}
}
