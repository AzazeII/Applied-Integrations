package AppliedIntegrations.Gui.Part;

import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Buttons.GuiButtonAETab;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.IWidgetHost;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.TileEnergyInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;
import static net.minecraft.util.EnumFacing.*;

/**
 * @Author Azazell
 */
public class GuiEnergyInterface extends AIBaseGui implements IFilterGUI, IWidgetHost {
	private IEnergyInterface Einterface;

	private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.interface.tile.png");
	private ResourceLocation texturePart = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.interface.part.png");
	private ResourceLocation energybar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.rf.bar.png");

	public PartEnergyInterface part;
	private TileEnergyInterface tile;

	protected final List<String> tooltip = new ArrayList<String>();
	private List<WidgetEnergySlot> EnergySlotList = new ArrayList<WidgetEnergySlot>();

	private GuiButtonAETab priority;

	private EntityPlayer player;

	private ContainerEnergyInterface LinkedContainer;
	private List<String> buttonTooltip = new ArrayList<String>();

	public int id;
	public LiquidAIEnergy LinkedMetric = RF;
	private WidgetEnergySlot energySlot;

	public int storage;

	public GuiEnergyInterface(ContainerEnergyInterface CEI, PartEnergyInterface part, EntityPlayer player) {
		super(CEI);
		this.player = player;

		this.Einterface = part;
		this.part = (PartEnergyInterface) Einterface;

		this.LinkedContainer = CEI;

		this.guiLeft = this.guiLeft - 51;
	}

	public GuiEnergyInterface(ContainerEnergyInterface container, IEnergyInterface Einterface, EntityPlayer player) {
		super(container);
		this.player = player;
		this.Einterface = Einterface;

		this.LinkedContainer = container;
		if (this.Einterface instanceof TileEnergyInterface) {
			this.tile = (TileEnergyInterface) Einterface;
		}

		this.guiLeft = this.guiLeft - 51;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.Einterface instanceof PartEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(this.texturePart);
		}
		if (this.Einterface instanceof TileEnergyInterface) {

			Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
		}
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize + 75);
		// Draw upgrade slots
		this.drawTexturedModalRect(this.guiLeft + GUI_MAIN_WIDTH, this.guiTop, GUI_MAIN_WIDTH, 0,
				GUI_UPGRADES_WIDTH, GUI_UPGRADES_HEIGHT);

	}

	@Override
	public void drawScreen(int MouseX, int MouseY, float pOpacity) {
		tooltip.clear();
		super.drawScreen(MouseX, MouseY, pOpacity);
		drawHoveringText(this.tooltip, MouseX, MouseY, fontRenderer);
		if (AIGuiHelper.INSTANCE.isPointInGuiRegion(this.guiLeft - 18, this.guiTop + 8, 16, 16, MouseX, MouseY, this.guiLeft, this.guiTop))
			drawHoveringText(this.buttonTooltip, MouseX, MouseY, fontRenderer);
		if (this.energySlot.isMouseOverWidget(MouseX, MouseY)) {
			List<String> tip = new ArrayList<String>();
			if (energySlot.getCurrentEnergy() != null) {
				tip.add(this.energySlot.getCurrentEnergy().getEnergyName());
				drawHoveringText(tip, MouseX, MouseY, fontRenderer);
			}
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Call super
		super.mouseClicked(mouseX, mouseY, mouseButton);
		/*if (this.priority.isMouseOverButton(mouseX, mouseY)) {
			NetworkHandler.sendToServer(new PacketGuiChange(new GuiPriority(this.player.inventory, this.part),
					getX(),getY(),getZ(),player));
		}*/
		if (this.energySlot.isMouseOverWidget(mouseX, mouseY)) {
			// avoid null pointer exception
			if (player.inventory.getItemStack() == null) {
				energySlot.mouseClicked(null);
				return;
			}
			LiquidAIEnergy EnergyItem = Utils.getEnergyFromItemStack(this.player.inventory.getItemStack());

			try {
				energySlot.mouseClicked(EnergyItem);

			} catch (Exception e) {
				AILog.debug(e + "");
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		//binding correct Gui
		if (LinkedMetric == RF || LinkedMetric == J || LinkedMetric == EU)
			this.energybar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy." + LinkedMetric.getTag() + ".bar.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(energybar);
		// Drawing Name
		this.fontRenderer.drawString(I18n.translateToLocal("ME Energy Interface"), 9, 3, 4210752);
		// Drawing Tooltips
		if (this.Einterface instanceof TileEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(this.energybar);
			drawPower(3, 11, mouseX - 10, mouseY - 10, 16, SOUTH);
			drawPower(31, 11, mouseX - 10, mouseY - 10, 16, NORTH);
			drawPower(60, 11, mouseX - 10, mouseY - 10, 16, WEST);
			drawPower(95, 11, mouseX - 10, mouseY - 10, 16, EAST);
			drawPower(124, 11, mouseX - 10, mouseY - 10, 16, UP);
			drawPower(150, 11, mouseX - 10, mouseY - 10, 16, DOWN);

		} else if (this.Einterface instanceof PartEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(this.energybar);

			if (this.LinkedMetric != WA) {
				drawPower(80, 13, mouseX - 10, mouseY - 10, 16, null);
			}
			this.energySlot.drawWidget();
		}

	}

	@Override
	public void updateEnergy(@Nonnull LiquidAIEnergy energy, int index) {
		// Change widget's energy
		this.EnergySlotList.get(index).setCurrentEnergy(energy);
	}

	@Override
	public void initGui() {
		super.initGui();

		this.energySlot = new WidgetEnergySlot(this, 0, 79, 111, true);

		this.EnergySlotList.add(this.energySlot);

		//priority = new GuiButtonAETab(0, this.getLeft +
		//		GuiEnergyStoragePart.BUTTON_PRIORITY_X_POSITION, this.getTop - 3, AEStateIconsEnum.WRENCH,
		//		"gui.appliedenergistics2.Priority");
		//this.buttonList.add(priority);

	}

	private void drawPower(int pLeft, int pTop, int pMouseX, int pMouseY, int width, EnumFacing side) {
		try {
			if (part != null) {
				if (part.getMaxEnergyStored(null, LinkedMetric) != 0) {
					int height = this.getStorage(LinkedMetric, side) / (part.getMaxEnergyStored(null, LinkedMetric) / 83);
					int v = 0, u = v;
					// Draw Bar
					drawTexturedModalRect(pLeft, pTop + (83 - height), v, u, width, height);
					boolean hover = AIGuiHelper.INSTANCE.isPointInGuiRegion(pTop - 9, pLeft - 10, 83, width, pMouseX, pMouseY, this.guiLeft, this.guiTop);
					if (hover) {
						String str = String.format(
								"%s: %,d %s/%,d %s",
								I18n.translateToLocal("Energy Stored"),
								this.getStorage(LinkedMetric, null),
								this.LinkedMetric.getEnergyName(),
								this.part.getMaxEnergyStored(null, LinkedMetric),
								this.LinkedMetric.getEnergyName());
						drawMouseOver(str);
					}
				} else {
					boolean hover = AIGuiHelper.INSTANCE.isPointInGuiRegion(pTop - 9, pLeft - 10, 83, width, pMouseX, pMouseY, this.guiLeft, this.guiTop);
					if (hover) {
						String str = "Energy Stored: 0 / ?";
						drawMouseOver(str);
					}
				}
			} else if (tile != null) {
				int height = this.getStorage(LinkedMetric, side) / (tile.getStorage().getMaxEnergyStored() / 83);
				int v = 6, u = v;
				// Draw Bar
				drawTexturedModalRect(pLeft, pTop + (83 - height), v, u, width, height);
				boolean hover = AIGuiHelper.INSTANCE.isPointInGuiRegion(pTop - 9, pLeft - 10, 83, width, pMouseX, pMouseY, this.guiLeft, this.guiTop);
				if (hover) {
					String str = String.format(
							"%s: %,d %s/%,d %s",
							"Energy Stored",
							this.getStorage(LinkedMetric, side),
							this.LinkedMetric.getEnergyName(),
							this.tile.getStorage().getMaxEnergyStored(),
							this.LinkedMetric.getEnergyName());
					drawMouseOver(str);
				}
			}
		} catch (Exception e) {

		}
	}

	public void drawMouseOver(String pText) {
		if (pText != null) {
			tooltip.clear();
			tooltip.add(pText + "");
		}
	}

	public int getStorage(LiquidAIEnergy energy, EnumFacing side) {
		if (this.part != null) {
			return this.storage;
		} else if (this.tile != null) {
			if (this.LinkedContainer.LinkedTileStorageMap.get(side) != null)
				return this.LinkedContainer.LinkedTileStorageMap.get(side).get(energy);
		}
		return 0;
	}

	@Override
	public ISyncHost getSyncHost() {
		return Einterface;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if(host instanceof IEnergyInterface)
			Einterface = (IEnergyInterface)host;
	}
}
