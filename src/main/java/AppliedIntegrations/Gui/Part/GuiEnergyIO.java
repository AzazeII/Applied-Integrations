package AppliedIntegrations.Gui.Part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketEnum;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
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
public class GuiEnergyIO extends AIGui {
	private static final int FILTER_GRID_SIZE = 3;
	private static final int WIDGET_X_POSITION = 61;
	private static final int WIDGET_Y_POSITION = 21;
	private static final int GUI_MAIN_WIDTH = 176;
	private static final int GUI_UPGRADES_WIDTH = 35;
	public static final int GUI_UPGRADES_HEIGHT = 86;
	public EntityPlayer player;
	private String stringName;
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

		addPriorityButton();
		this.buttonList.add(getContainer().redstoneControlBtn = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8,
				Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE));
		getContainer().redstoneControlBtn.setVisibility(false);
		if (!getContainer().energySlotList.isEmpty()) {
			return;
		}

		int index = 0;
		for (int row = 0; row < FILTER_GRID_SIZE; row++) {
			for (int column = 0; column < FILTER_GRID_SIZE; column++) {
				int xPos = WIDGET_X_POSITION + (column * AIWidget.WIDGET_SIZE);
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
		if (getContainer().part == null) {
			return;
		}

		if (btn == priorityButton) {
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
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		boolean hoverUnderlayRendered = false;
		WidgetEnergySlot slotUnderMouse = null;

		for (int i = 0; i < getContainer().energySlotList.size(); i++) {
			WidgetEnergySlot slotWidget = getContainer().energySlotList.get(i);

			if ((!hoverUnderlayRendered) && (slotWidget.shouldRender) && (slotWidget.isMouseOverWidget(mouseX, mouseY))) {
				slotWidget.drawMouseHoverUnderlay();
				slotUnderMouse = slotWidget;
				hoverUnderlayRendered = true;
			}

			slotWidget.drawWidget();
		}

		if (slotUnderMouse != null) {
			slotUnderMouse.getTooltip(this.tooltip);
		}

		if (getContainer().redstoneControlBtn.isMouseOver()) {
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
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for (WidgetEnergySlot slot : getContainer().energySlotList) {
			if (slot.isMouseOverWidget(mouseX, mouseY)) {
				LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack(), getContainer().part.getHostWorld());

				if (slot.getCurrentStack() != null && slot.getCurrentStack().getEnergy() == itemEnergy) {
					return;
				}

				slot.onMouseClicked(new EnergyStack(itemEnergy, 0));
				break;
			}
		}

		if (getContainer().redstoneControlBtn.isMouseOver()) {
			short ordinal = (short) getContainer().redstoneControlBtn.getCurrentValue().ordinal();
			getContainer().redstoneControlBtn.set(ordinal == 3 ? RedstoneMode.IGNORE : RedstoneMode.values()[ordinal + 1]);
			NetworkHandler.sendToServer(new PacketEnum(getContainer().redstoneControlBtn.getCurrentValue(), getContainer().part));
		}
	}
}
