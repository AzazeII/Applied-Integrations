package AppliedIntegrations.Gui.Part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import AppliedIntegrations.Network.Packets.PartGUI.PacketSyncReturn;
import AppliedIntegrations.Parts.Energy.PartEnergyExport;
import AppliedIntegrations.Parts.Energy.PartEnergyImport;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

import static AppliedIntegrations.Gui.AIGuiHandler.GuiEnum.GuiAIPriority;
import static AppliedIntegrations.Helpers.Energy.Utils.getEnergyFromItemStack;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class GuiEnergyIO extends AIBaseGui {
	private static final int FILTER_GRID_SIZE = 3;

	private static final int WIDGET_X_POSITION = 61;

	private static final int WIDGET_Y_POSITION = 21;

	private static final int GUI_MAIN_WIDTH = 176;

	private static final int GUI_UPGRADES_WIDTH = 35;

	private static final int GUI_UPGRADES_HEIGHT = 86;

	public EntityPlayer player;

	String stringName;

	private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.io.bus.png");

	public GuiEnergyIO(Container container, EntityPlayer player) {
		super(container, player);
		this.player = player;
	}

	public ContainerPartEnergyIOBus getContainer() {
		return (ContainerPartEnergyIOBus) inventorySlots;
	}

	@Override
	public void initGui() {

		super.initGui();

		// Add priority button
		addPriorityButton();

		// Add redstone control button
		getContainer().redstoneControlBtn = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);

		// Set visible to false
		getContainer().redstoneControlBtn.setVisibility(false);

		// Add to button list
		buttonList.add(getContainer().redstoneControlBtn);

		// Don't add slots if energy slot list isn't empty
		if (!getContainer().energySlotList.isEmpty()) {
			return;
		}

		// Calculate the index
		int index = 0;
		for (int row = 0; row < FILTER_GRID_SIZE; row++) {
			for (int column = 0; column < FILTER_GRID_SIZE; column++) {
				// Calculate the x position
				int xPos = WIDGET_X_POSITION + (column * AIWidget.WIDGET_SIZE);

				// Calculate the y position
				int yPos = WIDGET_Y_POSITION + (row * AIWidget.WIDGET_SIZE);

				getContainer().energySlotList.add(new WidgetEnergySlot(this, index, xPos, yPos, getContainer().configMatrix[index]));

				index++;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize + 75);

		// Draw upgrade slots
		this.drawTexturedModalRect(this.guiLeft + GUI_MAIN_WIDTH, this.guiTop, GUI_MAIN_WIDTH, 0, GUI_UPGRADES_WIDTH, GUI_UPGRADES_HEIGHT);
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
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		// Avoid null pointer exception in packet
		if (getContainer().part == null) {
			return;
		}

		// Check if click was performed on priority button
		if (btn == priorityButton) {
			// Send packet to client
			NetworkHandler.sendToServer(new PacketGuiShift(GuiAIPriority, getContainer().part));
		}
	}

	@Override
	public void drawScreen(int MouseX, int MouseY, float pOpacity) {
		super.drawScreen(MouseX, MouseY, pOpacity);

		renderHoveredToolTip(MouseX, MouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// Call super
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Should overlay be rendered
		boolean hoverUnderlayRendered = false;

		// Current widget under mouse
		WidgetEnergySlot slotUnderMouse = null;

		// Iterate over widgets
		for (int i = 0; i < getContainer().energySlotList.size(); i++) {
			// Get slot on this index
			WidgetEnergySlot slotWidget = getContainer().energySlotList.get(i);

			// Check if overlay not rendering, slot widget should render and mouse is under widget
			if ((!hoverUnderlayRendered) && (slotWidget.shouldRender) && (slotWidget.isMouseOverWidget(mouseX, mouseY))) {
				// Draw widget's underlay
				slotWidget.drawMouseHoverUnderlay();

				// Update slot under mouse
				slotUnderMouse = slotWidget;

				// trigger boolean
				hoverUnderlayRendered = true;
			}

			// Draw widget
			slotWidget.drawWidget();
		}

		// Should we get the tooltip from the slot?
		if (slotUnderMouse != null) {
			// Add the tooltip from the widget
			slotUnderMouse.getTooltip(this.tooltip);
		}

		// Add tooltip to redstone control button
		// Check if mouse over redstone control button
		if (getContainer().redstoneControlBtn.isMouseOver()) {
			// Split messages using regex "\n"
			tooltip.addAll(Arrays.asList(getContainer().redstoneControlBtn.getMessage().split("\n")));
		}

		if (getContainer().part instanceof PartEnergyExport) {
			this.stringName = I18n.translateToLocal("ME Energy Export Bus");
		}

		if (getContainer().part instanceof PartEnergyImport) {
			this.stringName = I18n.translateToLocal("ME Energy Import Bus");
		}

		this.fontRenderer.drawString(stringName, 9, 3, 4210752);
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Call super
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for (WidgetEnergySlot slot : getContainer().energySlotList) {
			if (slot.isMouseOverWidget(mouseX, mouseY)) {
				// Get the Energy of the currently held item
				LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack());

				// Check if item energy not equal to energy in slot
				if (slot.getCurrentStack() != null && slot.getCurrentStack().getEnergy() == itemEnergy) {
					return;
				}

				// Call mouse click function
				slot.onMouseClicked(new EnergyStack(itemEnergy, 0));

				break;
			}
		}

		// Check if mouse over redstone control button
		if (getContainer().redstoneControlBtn.isMouseOver()) {
			// Get current mode ordinal
			short ordinal = (short) getContainer().redstoneControlBtn.getCurrentValue().ordinal();

			// Switch to next mode
			getContainer().redstoneControlBtn.set(ordinal == 3 ? RedstoneMode.IGNORE : RedstoneMode.values()[ordinal + 1]);

			// Send packet to client
			NetworkHandler.sendToServer(new PacketSyncReturn(getContainer().redstoneControlBtn.getCurrentValue(), getContainer().part));
		}
	}
}
