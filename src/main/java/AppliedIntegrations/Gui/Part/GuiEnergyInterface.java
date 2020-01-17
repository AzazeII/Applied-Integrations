package AppliedIntegrations.Gui.Part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.Gui.AIGuiHandler.GuiEnum.GuiAIPriority;
import static AppliedIntegrations.grid.Implementation.AIEnergy.*;
import static appeng.api.util.AEPartLocation.*;

/**
 * @Author Azazell
 */
public class GuiEnergyInterface extends AIGui implements IWidgetHost {
	private static ResourceLocation textureTile = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.interface.tile.png");
	private static ResourceLocation texturePart = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.interface.part.png");
	private ResourceLocation energybar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.rf.bar.png");

	protected final List<String> tooltip = new ArrayList<>();
	private List<String> buttonTooltip = new ArrayList<>();

	private IEnergyInterface energyInterface;
	private EntityPlayer player;

	public GuiEnergyInterface(ContainerEnergyInterface container, EntityPlayer player) {
		super(container, player);

		this.player = player;
		this.energyInterface = container.energyInterface;
		this.guiLeft = this.guiLeft - 51;
	}

	public ContainerEnergyInterface getContainer() {
		return (ContainerEnergyInterface) inventorySlots;
	}

	@Override
	public void initGui() {
		super.initGui();

		if (energyInterface instanceof PartEnergyInterface) {
			getContainer().energySlotList.add(new WidgetEnergySlot(this, 0, 79, 111, true));
		} else if (energyInterface instanceof TileEnergyInterface) {
			getContainer().energySlotList.add(new WidgetEnergySlot(this, DOWN.ordinal(), 34, 111, true));
			getContainer().energySlotList.add(new WidgetEnergySlot(this, UP.ordinal(), 52, 111, true));
			getContainer().energySlotList.add(new WidgetEnergySlot(this, NORTH.ordinal(), 70, 111, true));
			getContainer().energySlotList.add(new WidgetEnergySlot(this, SOUTH.ordinal(), 88, 111, true));
			getContainer().energySlotList.add(new WidgetEnergySlot(this, WEST.ordinal(), 106, 111, true));
			getContainer().energySlotList.add(new WidgetEnergySlot(this, EAST.ordinal(), 124, 111, true));
		}

		addPriorityButton();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		drawDefaultBackground();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (this.energyInterface instanceof PartEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(texturePart);
		} else if (this.energyInterface instanceof TileEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(textureTile);
		}

		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize + 75);
		this.drawTexturedModalRect(this.guiLeft + GUI_MAIN_WIDTH, this.guiTop, GUI_MAIN_WIDTH, 0, GUI_UPGRADES_WIDTH, GUI_UPGRADES_HEIGHT);
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		if ( !(energyInterface instanceof PartEnergyInterface) ) {
			return;
		}

		if (btn == priorityButton) {
			NetworkHandler.sendToServer(new PacketGuiShift(GuiAIPriority, (IPriorityHostExtended) energyInterface));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float pOpacity) {
		tooltip.clear();
		super.drawScreen(mouseX, mouseY, pOpacity);

		drawHoveringText(tooltip, mouseX, mouseY, fontRenderer);
		if (AIGuiHelper.INSTANCE.isPointInGuiRegion(this.guiLeft - 18, this.guiTop + 8, 16, 16, mouseX, mouseY, this.guiLeft, this.guiTop)) {
			drawHoveringText(buttonTooltip, mouseX, mouseY, fontRenderer);
		}

		for (WidgetEnergySlot slot : getContainer().energySlotList) {
			if (slot.isMouseOverWidget(mouseX, mouseY)) {
				List<String> tip = new ArrayList<String>();

				if (slot.getCurrentStack() != null && !slot.getStackTip().equals("")) {
					tip.add(slot.getStackTip());
					drawHoveringText(tip, mouseX, mouseY, fontRenderer);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (getContainer().linkedMetric == RF || getContainer().linkedMetric == J || getContainer().linkedMetric == EU) {
			energybar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy." + getContainer().linkedMetric.getTag() + ".bar.png");
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(energybar);
		this.fontRenderer.drawString(I18n.translateToLocal("ME Energy Interface"), 9, 3, 4210752);
		if (this.energyInterface instanceof TileEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(energybar);
			drawPower(35, 14, mouseX - 10, mouseY - 10, 16, DOWN);
			drawPower(53, 14, mouseX - 10, mouseY - 10, 16, UP);
			drawPower(71, 14, mouseX - 10, mouseY - 10, 16, NORTH);
			drawPower(89, 14, mouseX - 10, mouseY - 10, 16, SOUTH);
			drawPower(107, 14, mouseX - 10, mouseY - 10, 16, WEST);
			drawPower(125, 14, mouseX - 10, mouseY - 10, 16, EAST);
		} else if (this.energyInterface instanceof PartEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(this.energybar);

			if (this.getContainer().linkedMetric != WA) {
				drawPower(80, 13, mouseX - 10, mouseY - 10, 16, null);
			}
		}

		for (int i = 0; i < getContainer().energySlotList.size(); i++) {
			WidgetEnergySlot slot = getContainer().energySlotList.get(i);
			slot.drawWidget();
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		/*if (this.priority.isMouseOverButton(mouseX, mouseY)) {
			NetworkHandler.sendToServer(new PacketGuiChange(new GuiPriority(this.player.inventory, this.host),
					getX(),getY(),getZ(),player));
		}*/

		getContainer().energySlotList.forEach((energySlot) -> {
			if (energySlot.isMouseOverWidget(mouseX, mouseY)) {
				LiquidAIEnergy energyItem = Utils.getEnergyFromItemStack(this.player.inventory.getItemStack(), energyInterface.getHostWorld());

				try {
					energySlot.onMouseClicked(new EnergyStack(energyItem, 1));
				} catch (Exception e) {
					AILog.debug(e + "");
				}
			}
		});
	}

	private void drawPower(int pLeft, int pTop, int pMouseX, int pMouseY, int width, AEPartLocation side) {
		int height = this.getStorage(side) / (energyInterface.getMaxEnergyStored(side, getContainer().linkedMetric) / 83);

		int v = 0;

		boolean hover = drawPowerBar(pLeft, pTop, pMouseX, pMouseY, width, height, v, v);
		if (energyInterface != null) {
			if (energyInterface.getMaxEnergyStored(side, getContainer().linkedMetric) != 0) {
				if (hover) {
					addBarTooltip(side);
				}
			} else {
				hover = AIGuiHelper.INSTANCE.isPointInGuiRegion(pTop - 9, pLeft - 10, 83, width, pMouseX, pMouseY, this.guiLeft, this.guiTop);

				if (hover) {
					String str = "Energy Stored: ? / ?";
					drawMouseOver(str);
				}
			}
		}
	}

	private int getStorage(AEPartLocation side) {
		if (energyInterface instanceof PartEnergyInterface) {
			return getContainer().storage == null ? 0 : getContainer().storage.intValue();
		} else {
			return getContainer().sideStorageMap.get(side) == null ? 0 : getContainer().sideStorageMap.get(side).intValue();
		}
	}

	private boolean drawPowerBar(int pLeft, int pTop, int pMouseX, int pMouseY, int width, int height, int v, int u) {
		drawTexturedModalRect(pLeft, pTop + (83 - height), v, u, width, height);
		return AIGuiHelper.INSTANCE.isPointInGuiRegion(pTop - 9, pLeft - 10, 83, width, pMouseX, pMouseY, this.guiLeft, this.guiTop);
	}

	private void addBarTooltip(AEPartLocation side) {
		String str = null;

		if (energyInterface instanceof PartEnergyInterface) {
			str = String.format("%s: %,d %s/%,d %s",
					I18n.translateToLocal("Energy Stored"),
					this.getStorage(side),
					this.getContainer().linkedMetric.getEnergyName(),
					this.energyInterface.getMaxEnergyStored(side, getContainer().linkedMetric),
					this.getContainer().linkedMetric.getEnergyName());
		} else if (energyInterface instanceof TileEnergyInterface) {
			str = String.format("%s\n%s: %,d %s/%,d %s",
					side.name(),
					I18n.translateToLocal("Energy Stored"),
					this.getStorage(side),
					this.getContainer().linkedMetric.getEnergyName(),
					this.energyInterface.getMaxEnergyStored(side, getContainer().linkedMetric),
					this.getContainer().linkedMetric.getEnergyName());
		}

		drawMouseOver(str);
	}

	public void drawMouseOver(String tip) {
		if (tip != null) {
			tooltip.clear();
			tooltip.add(tip);
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
}

