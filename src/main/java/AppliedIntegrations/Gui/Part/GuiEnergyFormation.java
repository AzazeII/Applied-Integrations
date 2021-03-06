package AppliedIntegrations.Gui.Part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyFormation;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Parts.Energy.PartEnergyFormation;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import static AppliedIntegrations.Gui.Part.GuiEnergyStoragePart.WIDGET_X_POS;
import static AppliedIntegrations.Gui.Part.GuiEnergyStoragePart.WIDGET_Y_POS;
import static AppliedIntegrations.Helpers.Energy.Utils.getEnergyFromItemStack;

/**
 * @Author Azazell
 */
public class GuiEnergyFormation extends AIGui {
	private PartEnergyFormation plane;

	public GuiEnergyFormation(ContainerEnergyFormation container, EntityPlayer player) {
		super(container, player);
		this.xSize = GuiEnergyStoragePart.GUI_WIDTH_NO_TOOL;
		this.ySize = 251;
	}

	private ContainerEnergyFormation getContainer() {
		return (ContainerEnergyFormation) inventorySlots;
	}

	@Override
	public void initGui() {
		super.initGui();
		addPriorityButton();

		for (int row = 0; row < GuiEnergyStoragePart.WIDGET_COLUMNS; row++) {
			for (int column = 0; column < GuiEnergyStoragePart.WIDGET_ROWS; column++) {
				getContainer().energySlotList.add(new WidgetEnergySlot(this, 0, WIDGET_X_POS + (AIWidget.WIDGET_SIZE * column) - 6,
						WIDGET_Y_POS + (AIWidget.WIDGET_SIZE * row) - 1, true));
			}
		}

		for (int i = 0; i < PartEnergyStorage.FILTER_SIZE; i++) {
			getContainer().energySlotList.get(i).id = i;
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
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.storage.bus.png"));

		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
		this.drawTexturedModalRect(this.guiLeft + 179, this.guiTop, 179, 0, 32, 32);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		this.fontRenderer.drawString(I18n.translateToLocal("ME Energy Formation Plane"),
				GuiEnergyStoragePart.TITLE_X_POS,
				GuiEnergyStoragePart.TITLE_Y_POS, 0x000000);
		this.fontRenderer.drawString(I18n.translateToLocal("Inventory"), 8, this.ySize - 96 + 3, 4210752);

		WidgetEnergySlot slotUnderMouse = null;
		for (int i = 0; i < getContainer().energySlotList.size(); i++) {
			WidgetEnergySlot currentWidget = getContainer().energySlotList.get(i);

			if ((slotUnderMouse == null) && (currentWidget.shouldRender) && (currentWidget.isMouseOverWidget(mouseX, mouseY))) {
				slotUnderMouse = currentWidget;
			}

			currentWidget.drawWidget();
		}

		if (slotUnderMouse != null) {
			slotUnderMouse.getTooltip(this.tooltip);
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for (WidgetEnergySlot energySlot : getContainer().energySlotList) {
			if (energySlot.isMouseOverWidget(mouseX, mouseY)) {
				LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack(), plane.getHostWorld());
				if (energySlot.getCurrentStack() == null || energySlot.getCurrentStack().getEnergy() == itemEnergy) {
					return;
				}

				energySlot.onMouseClicked(new EnergyStack(itemEnergy, 0));

				break;
			}
		}
	}
}
