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
	public static final int WIDGET_COLUMNS = 2;
	public static final int WIDGET_ROWS = 9;
	public static final int WIDGET_X_POS = 13;
	public static final int WIDGET_Y_POS = 29;

	private static final int GUI_WIDTH_NETWORK_TOOL = 246;
	public static final int GUI_WIDTH_NO_TOOL = 210;

	public static final int TITLE_X_POS = 6;
	public static final int TITLE_Y_POS = 5;

	private boolean hasNetworkTool;

	private PartEnergyStorage storageBus;

	public GuiEnergyStoragePart(ContainerEnergyStorage CEI, final PartEnergyStorage storageBus, final EntityPlayer player) {
		super(CEI, player);
		this.storageBus = storageBus;
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

		addPriorityButton();
		for (int row = 0; row < GuiEnergyStoragePart.WIDGET_COLUMNS; row++) {
			for (int column = 0; column < GuiEnergyStoragePart.WIDGET_ROWS; column++) {
				getContainer().energySlotList.add(new WidgetEnergySlot(this, 0, WIDGET_X_POS + (AIWidget.WIDGET_SIZE * column) - 6, WIDGET_Y_POS + (AIWidget.WIDGET_SIZE * row) - 1, true));
			}
		}

		for (int i = 0; i < PartEnergyStorage.FILTER_SIZE; i++) {
			getContainer().energySlotList.get(i).id = i;
		}

		getContainer().accessMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.ACCESS, AccessRestriction.READ_WRITE);
		buttonList.add(getContainer().accessMode);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float alpha, final int mouseX, final int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.storage.bus.png"));

		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
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

		if (btn == getContainer().accessMode) {
			AccessRestriction mode = (AccessRestriction) getContainer().accessMode.getCurrentValue();

			if (mode == AccessRestriction.WRITE) {
				getContainer().accessMode.set(AccessRestriction.READ_WRITE);
			} else if (mode == AccessRestriction.READ_WRITE) {
				getContainer().accessMode.set(AccessRestriction.READ);
			} else if (mode == AccessRestriction.READ) {
				getContainer().accessMode.set(AccessRestriction.WRITE);
			}

			NetworkHandler.sendToServer(new PacketEnum(getContainer().accessMode.getCurrentValue(), storageBus));
		}

		if (storageBus == null) {
			return;
		}

		if (btn == priorityButton) {
			NetworkHandler.sendToServer(new PacketGuiShift(GuiAIPriority, storageBus));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.fontRenderer.drawString(I18n.translateToLocal("ME Energy Storage Bus"), GuiEnergyStoragePart.TITLE_X_POS, GuiEnergyStoragePart.TITLE_Y_POS, 0x000000);
		this.fontRenderer.drawString(I18n.translateToLocal("Inventory"), 8, this.ySize - 96 + 3, 4210752);

		WidgetEnergySlot slotUnderMouse = null;

		for (int i = 0; i < getContainer().energySlotList.size(); i++) {
			WidgetEnergySlot currentWidget = getContainer().energySlotList.get(i);

			if ((slotUnderMouse == null) && (currentWidget.shouldRender) && (currentWidget.isMouseOverWidget(mouseX, mouseY))) {
				slotUnderMouse = currentWidget;
			}

			currentWidget.drawWidget();
		}

		if (getContainer().accessMode.isMouseOver()) {
			tooltip.addAll(Arrays.asList(getContainer().accessMode.getMessage().split("\n")));
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

				LiquidAIEnergy itemEnergy = getEnergyFromItemStack(player.inventory.getItemStack(), storageBus.getHostWorld());

				if (energySlot.getCurrentStack() == null || energySlot.getCurrentStack().getEnergy() == itemEnergy) {
					return;
				}

				energySlot.onMouseClicked(new EnergyStack(itemEnergy, 0));

				break;
			}
		}
	}
}
