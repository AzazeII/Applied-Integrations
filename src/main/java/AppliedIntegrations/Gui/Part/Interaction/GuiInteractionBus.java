package AppliedIntegrations.Gui.Part.Interaction;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerInteractionBus;
import AppliedIntegrations.Container.slot.SlotFilter;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.Part.GuiEnergyIO;
import AppliedIntegrations.Gui.Part.Interaction.Buttons.GuiClickModeButton;
import AppliedIntegrations.Gui.Widgets.WidgetGuiTab;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketEnum;
import AppliedIntegrations.api.IEnumHost;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static AppliedIntegrations.Parts.Interaction.PartInteraction.EnumInteractionPlaneTabs;

/**
 * @Author Azazell
 */
public class GuiInteractionBus extends AIGui {
	private static final ResourceLocation TEXTURE_INVENTORY = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/interaction.bus.inventory.png");
	private static final ResourceLocation TEXTURE_FILTER = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/interaction.bus.png");
	public EnumInteractionPlaneTabs currentTab = EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER;
	private List<WidgetGuiTab> tabs = new ArrayList<>();

	public GuiInteractionBus(ContainerInteractionBus container, EntityPlayer player) {
		super(container, player);
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

	private void playClickSound() {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		// Transfer click on button under mouse
		if (btn == getContainer().shiftClickButton && currentTab == EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER) {
			getContainer().shiftClickButton.cycleMode();
		}
	}

	@Override
	public ISyncHost getSyncHost() {
		return getContainer().getSyncHost();
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		getContainer().setSyncHost(host);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.tabs.add(new WidgetGuiTab(this, 0, -28, 4,true,
				EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_FILTER, "Interaction Bus Filters", ItemEnum.ITEMPARTINTERACTIONBUS.getItem(), itemRender, fontRenderer));
		this.tabs.add(new WidgetGuiTab(this, 29, -28, 1,false,
				EnumInteractionPlaneTabs.PLANE_FAKE_PLAYER_INVENTORY, "Interaction Bus Inventory", Blocks.CHEST, itemRender, fontRenderer));
		this.buttonList.add(getContainer().shiftClickButton = new GuiClickModeButton(this,0,
				this.guiLeft - 18, this.guiTop + 8, 16, 16, ""));
		getContainer().redstoneControlButton = new GuiImgButton(this.guiLeft - 18, this.guiTop + 28,
						Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
		getContainer().fuzzyModeButton = new GuiImgButton(this.guiLeft - 18, this.guiTop + 48,
						Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
		getContainer().craftingModeButton = new GuiImgButton(this.guiLeft - 18, this.guiTop + 68,
						Settings.CRAFT_ONLY, YesNo.NO);
		getContainer().redstoneControlButton.setVisibility(false);
		getContainer().fuzzyModeButton.setVisibility(false);
		getContainer().craftingModeButton.setVisibility(false);
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// Click tab(s) under mouse
		for (WidgetGuiTab tab : tabs) {
			if (tab.isMouseOverWidget(mouseX, mouseY)) {
				currentTab = (EnumInteractionPlaneTabs) tab.tabEnum;
				playClickSound();
				tab.mouseClicked();

				// Make other tabs unselected
				for (WidgetGuiTab unselectedTab : tabs) {
					if (unselectedTab != tab) {
						unselectedTab.isTabSelected = false;
					}
				}
			}
		}

		if(getContainer().redstoneControlButton.isMouseOver()) {
			// Switch mode and sync with server
			short ordinal = (short) getContainer().redstoneControlButton.getCurrentValue().ordinal();
			getContainer().redstoneControlButton.set(ordinal == 3 ? RedstoneMode.IGNORE : RedstoneMode.values()[ordinal + 1]);
			NetworkHandler.sendToServer(new PacketEnum(getContainer().redstoneControlButton.getCurrentValue(),
					(IEnumHost) getContainer().getSyncHost()));
			playClickSound();
		}

		if(getContainer().fuzzyModeButton.isMouseOver()) {
			// Switch mode and sync with server
			short ordinal = (short) getContainer().fuzzyModeButton.getCurrentValue().ordinal();
			getContainer().fuzzyModeButton.set(ordinal == 4 ? FuzzyMode.IGNORE_ALL : FuzzyMode.values()[ordinal + 1]);
			NetworkHandler.sendToServer(new PacketEnum(getContainer().fuzzyModeButton.getCurrentValue(),
					(IEnumHost) getContainer().getSyncHost()));
			playClickSound();
		}

		if(getContainer().craftingModeButton.isMouseOver()) {
			// Switch mode and sync with server
			getContainer().craftingModeButton.set(getContainer().craftingModeButton.getCurrentValue() == YesNo.NO ? YesNo.YES : YesNo.NO);
			NetworkHandler.sendToServer(new PacketEnum(getContainer().craftingModeButton.getCurrentValue(),
					(IEnumHost) getContainer().getSyncHost()));
			playClickSound();
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

			getContainer().redstoneControlButton.drawButton(mc, mouseX, mouseY, partialTicks);
			getContainer().fuzzyModeButton.drawButton(mc, mouseX, mouseY, partialTicks);
			getContainer().craftingModeButton.drawButton(mc, mouseX, mouseY, partialTicks);
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

			// Add tip from buttons
			if (getContainer().redstoneControlButton.isMouseOver()) {
				tooltip.addAll(Arrays.asList(getContainer().redstoneControlButton.getMessage().split("\n")));
			}

			if (getContainer().fuzzyModeButton.isMouseOver()) {
				tooltip.addAll(Arrays.asList(getContainer().fuzzyModeButton.getMessage().split("\n")));
			}

			if (getContainer().craftingModeButton.isMouseOver()) {
				tooltip.addAll(Arrays.asList(getContainer().craftingModeButton.getMessage().split("\n")));
			}
		}

		// Draw tabs
		tabs.forEach(WidgetGuiTab::drawWidget);
	}
}
