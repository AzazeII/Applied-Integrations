package AppliedIntegrations.Gui.Part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyStorage;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketEnum;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
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
public class GuiEnergyStoragePart extends AIGui {

	// Widget constants
	public static final int WIDGET_COLUMNS = 2; // (1)

	public static final int WIDGET_ROWS = 9; // (2)

	public static final int WIDGET_X_POS = 13; // (3)

	public static final int WIDGET_Y_POS = 29; // (4)

	// Network tool constants
	private static final int GUI_WIDTH_NETWORK_TOOL = 246; // (1)

	public static final int GUI_WIDTH_NO_TOOL = 210; // (2)

	// Tittle constants
	public static final int TITLE_X_POS = 6; // (1)

	public static final int TITLE_Y_POS = 5; // (2)

	// Should gui render network tool slots?
	private boolean hasNetworkTool;

	// Owner of this GUI
	private PartEnergyStorage storageBus;

	public GuiEnergyStoragePart(ContainerEnergyStorage CEI, final PartEnergyStorage storageBus, final EntityPlayer player) {
		// Call super
		super(CEI, player);

		// Set the storage bus
		this.storageBus = storageBus;

		// Set the network tool
		this.hasNetworkTool = ((ContainerEnergyStorage) this.inventorySlots).hasNetworkTool();

		this.xSize = (this.hasNetworkTool ? GuiEnergyStoragePart.GUI_WIDTH_NETWORK_TOOL : GuiEnergyStoragePart.GUI_WIDTH_NO_TOOL);
		this.ySize = 251;
	}

	private ContainerEnergyStorage getContainer() {
		return (ContainerEnergyStorage) inventorySlots;
	}

	@Override
	public void initGui() {

		super.initGui();

		// Add priority button
		addPriorityButton();

		// Create widget @for_each row and @for_each column with zero index
		for (int row = 0; row < GuiEnergyStoragePart.WIDGET_COLUMNS; row++) {
			for (int column = 0; column < GuiEnergyStoragePart.WIDGET_ROWS; column++) {
				getContainer().energySlotList.add(new WidgetEnergySlot(this, 0, WIDGET_X_POS + (AIWidget.WIDGET_SIZE * column) - 6, WIDGET_Y_POS + (AIWidget.WIDGET_SIZE * row) - 1, true));
			}
		}

		// Iterate from 0 to 17 and map all widgets
		for (int i = 0; i < PartEnergyStorage.FILTER_SIZE; i++) {
			// Change index
			getContainer().energySlotList.get(i).id = i;
		}

		// Create access mode
		getContainer().accessMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.ACCESS, AccessRestriction.READ_WRITE);

		// Register button in list
		buttonList.add(getContainer().accessMode);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float alpha, final int mouseX, final int mouseY) {
		// Full white
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Set the texture
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.storage.bus.png"));

		// Draw AppliedIntegrations gui
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

		// Draw upgrade slot
		this.drawTexturedModalRect(this.guiLeft + 179, this.guiTop, 179, 0, 32, 32);

		if (this.hasNetworkTool) {
			this.drawTexturedModalRect(this.guiLeft + 179, this.guiTop + 93, 178, 93, 68, 68);
		}
	}

	@Override
	public ISyncHost getSyncHost() {

		return storageBus;
	}

	@Override
	public void setSyncHost(ISyncHost host) {

		if (host instanceof PartEnergyStorage) {
			storageBus = (PartEnergyStorage) host;
		}
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {

		super.onButtonClicked(btn, mouseButton);

		// Check if action performed on access mode button
		if (btn == getContainer().accessMode) {
			AccessRestriction mode = (AccessRestriction) getContainer().accessMode.getCurrentValue();

			// Cycle modes
			if (mode == AccessRestriction.WRITE) {
				getContainer().accessMode.set(AccessRestriction.READ_WRITE);
			} else if (mode == AccessRestriction.READ_WRITE) {
				getContainer().accessMode.set(AccessRestriction.READ);
			} else if (mode == AccessRestriction.READ) {
				getContainer().accessMode.set(AccessRestriction.WRITE);
			}

			// Notify server
			NetworkHandler.sendToServer(new PacketEnum(getContainer().accessMode.getCurrentValue(), storageBus));
		}

		// Avoid null pointer exception in packet
		if (storageBus == null) {
			return;
		}

		// Check if click was performed on priority button
		if (btn == priorityButton) {
			// Send packet to client
			NetworkHandler.sendToServer(new PacketGuiShift(GuiAIPriority, storageBus));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		// Call super
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Draw the title
		this.fontRenderer.drawString(I18n.translateToLocal("ME Energy Storage Bus"), GuiEnergyStoragePart.TITLE_X_POS, GuiEnergyStoragePart.TITLE_Y_POS, 0x000000);
		this.fontRenderer.drawString(I18n.translateToLocal("Inventory"), 8, this.ySize - 96 + 3, 4210752);

		WidgetEnergySlot slotUnderMouse = null;

		// Iterate over widgets
		for (int i = 0; i < getContainer().energySlotList.size(); i++) {
			// Get widget
			WidgetEnergySlot currentWidget = getContainer().energySlotList.get(i);

			if ((slotUnderMouse == null) && (currentWidget.shouldRender) && (currentWidget.isMouseOverWidget(mouseX, mouseY))) {
				// Set the slot
				slotUnderMouse = currentWidget;
			}

			// Draw the widget
			currentWidget.drawWidget();
		}

		// Check if mouse over access mode widget
		if (getContainer().accessMode.isMouseOver()) {
			// Split messages using regex "\n"
			tooltip.addAll(Arrays.asList(getContainer().accessMode.getMessage().split("\n")));
		}

		// Should we get the tooltip from the slot?
		if (slotUnderMouse != null) {
			// Add the tooltip from the widget
			slotUnderMouse.getTooltip(this.tooltip);
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Call super
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for (WidgetEnergySlot energySlot : getContainer().energySlotList) {
			if (energySlot.isMouseOverWidget(mouseX, mouseY)) {
				// Get the Energy of the currently held item
				LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack());

				if (energySlot.getCurrentStack() == null || energySlot.getCurrentStack().getEnergy() == itemEnergy) {
					return;
				}

				energySlot.onMouseClicked(new EnergyStack(itemEnergy, 0));

				break;
			}
		}
	}
}
