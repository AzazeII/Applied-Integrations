package AppliedIntegrations.Gui.Part.Interaction;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerInteractionBus;
import AppliedIntegrations.Container.slot.SlotFilter;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.Part.GuiEnergyIO;
import AppliedIntegrations.Gui.Part.Interaction.Buttons.GuiClickModeButton;
import AppliedIntegrations.Gui.Widgets.WidgetGuiTab;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import AppliedIntegrations.api.ISyncHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.Parts.Interaction.PartInteraction.EnumInteractionPlaneTabs;

/**
 * @Author Azazell
 */
public class GuiInteractionBus extends AIGui {
	private static final ResourceLocation TEXTURE_INVENTORY = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/interaction.bus.inventory.png");
	private static final ResourceLocation TEXTURE_FILTER = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/interaction.bus.png");
	public PartInteraction interaction;
	public EnumInteractionPlaneTabs currentTab = EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER;
	private List<WidgetGuiTab> tabs = new ArrayList<>();
	private GuiClickModeButton shiftClickButton;

	public GuiInteractionBus(ContainerInteractionBus container, EntityPlayer player, PartInteraction interaction) {
		super(container, player);
		this.interaction = interaction;
	}

	public ContainerInteractionBus getContainer() {
		return (ContainerInteractionBus) this.inventorySlots;
	}

	private void drawFilterSlotsBackground() {
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_FILTER);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Make slot background highlighted if it's enabled
		for (SlotFilter filter : getContainer().filters) {
			int x = filter.xPos - 1;
			int y = filter.yPos - 1;

			if (filter.isEnabled()) {
				drawTexturedModalRect(x, y, 79, 39, 18, 18);
			}
		}
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		// Transfer click on button under mouse
		if (btn == shiftClickButton && currentTab == EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER) {
			shiftClickButton.cycleMode();
		}
	}

	@Override
	public ISyncHost getSyncHost() {
		return interaction;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		this.interaction = (PartInteraction) host;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.tabs.add(new WidgetGuiTab(this, 0, -28, 4,true,
				EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER, "Interaction Bus Filters", ItemEnum.ITEMPARTINTERACTIONPLANE.getItem(), itemRender, fontRenderer));
		this.tabs.add(new WidgetGuiTab(this, 29, -28, 1,false,
				EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_INVENTORY, "Interaction Bus Inventory", Blocks.CHEST, itemRender, fontRenderer));
		this.buttonList.add(this.shiftClickButton = new GuiClickModeButton(this,0,
				this.guiLeft - 18, this.guiTop, 16, 16, ""));
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// Click tab(s) under mouse
		for (WidgetGuiTab tab : tabs) {
			if (tab.isMouseOverWidget(mouseX, mouseY)) {
				currentTab = (EnumInteractionPlaneTabs) tab.tabEnum;
				tab.mouseClicked();

				// Make other tabs unselected
				for (WidgetGuiTab unselectedTab : tabs) {
					if (unselectedTab != tab) {
						unselectedTab.isTabSelected = false;
					}
				}
			}
		}
	}

	@Override
	protected boolean hasClickedOutside(int mouseX, int mouseY, int guiLeft, int guiTop) {
		// Don't drop item stack if it is over tab
		for (WidgetGuiTab tab : tabs) {
			if (tab.isMouseOverWidget(mouseX, mouseY)) {
				return false;
			}
		}

		return mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float pOpacity) {
		super.drawScreen(mouseX, mouseY, pOpacity);

		// Draw tab names over them
		for (WidgetGuiTab tab : tabs) {
			if (tab.isMouseOverWidget(mouseX, mouseY)) {
				List<String> tip = new ArrayList<>();
				tip.add(tab.getTabName());

				drawHoveringText(tip, mouseX, mouseY, fontRenderer);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Draw background depending on current tab
		if (currentTab == EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER) {
			Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_FILTER);

			drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize + 75);
			drawTexturedModalRect(this.guiLeft + GUI_MAIN_WIDTH, this.guiTop, GUI_MAIN_WIDTH, 0, GUI_UPGRADES_WIDTH, GuiEnergyIO.GUI_UPGRADES_HEIGHT);
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_INVENTORY);

			drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Draw foreground depending on current tab
		if (currentTab == EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER) {
			this.fontRenderer.drawString(I18n.translateToLocal("ME Interaction Bus"), 9, 3, 4210752);
			this.drawFilterSlotsBackground();
		}

		// Draw tabs
		tabs.forEach(WidgetGuiTab::drawWidget);
	}
}
