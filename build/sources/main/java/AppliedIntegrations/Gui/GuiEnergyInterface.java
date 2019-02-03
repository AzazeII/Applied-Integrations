package AppliedIntegrations.Gui;

import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Entities.TileEnergyInterface;
import AppliedIntegrations.Gui.Buttons.GuiButtonAETab;
import AppliedIntegrations.Gui.Buttons.InterfaceEnergyButtons;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySlot;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketGuiChange;
import AppliedIntegrations.Parts.EnergyInterface.PartEnergyInterface;
import AppliedIntegrations.Utils.AILog;
import appeng.api.config.RedstoneMode;
import appeng.client.gui.implementations.GuiPriority;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static appeng.api.config.RedstoneMode.*;
import static net.minecraft.util.EnumFacing.*;

/**
 * @Author Azazell
 */
public class GuiEnergyInterface extends PartGui implements IFilterGUI,IWidgetHost {
	private static final ResourceLocation GuiOmega = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/omegaSpring.png");
	private static RedstoneMode RSMode = LOW_SIGNAL;
	private IEnergyInterface Einterface;

	private List<String> mMouseHover = Lists.newArrayList();
	private String string[] = new String[6];
	private ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/InterfaceTile.png");
	private ResourceLocation texturePart = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/InterfacePart.png");
	private ResourceLocation energybar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/RFBar.png");

	public PartEnergyInterface part;
	private TileEnergyInterface tile;

	protected final List<String> tooltip = new ArrayList<String>();
	private List<WidgetEnergySlot> EnergySlotList = new ArrayList<WidgetEnergySlot>();

	private GuiButtonAETab priority;

	private InterfaceEnergyButtons[] buttons;
	private EntityPlayer player;
	private int hash;

	private ContainerEnergyInterface LinkedContainer;
	private static final int BORDER_OFFSET = 8;
	private List<String> EnergyBar = new ArrayList<String>();
	private List<String> buttonTooltip = new ArrayList<String>();

	public int id;
	public LiquidAIEnergy LinkedMetric = RF;
	private WidgetEnergySlot energySlot;
	@resetData
	public LiquidAIEnergy LinkedFilter;
	@resetData
	public int storage;

	public GuiEnergyInterface(ContainerEnergyInterface CEI, PartEnergyInterface part, World w, int x, int y, int z, EnumFacing side, EntityPlayer player) {
		super(CEI);
		this.player = player;

		if (side != null) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dir = side;
			this.w = w;

		}

		this.Einterface = part;
		this.part = (PartEnergyInterface) Einterface;

		this.LinkedContainer = (ContainerEnergyInterface) CEI;


	}

	public GuiEnergyInterface(ContainerEnergyInterface container, IEnergyInterface Einterface, EntityPlayer player) {
		super(container);
		this.player = player;
		this.Einterface = Einterface;

		this.LinkedContainer = (ContainerEnergyInterface) container;
		if (this.Einterface instanceof TileEnergyInterface) {
			this.tile = (TileEnergyInterface) Einterface;
		}
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
		this.drawTexturedModalRect(this.guiLeft + this.GUI_MAIN_WIDTH, this.guiTop, this.GUI_MAIN_WIDTH, 0,
				this.GUI_UPGRADES_WIDTH, this.GUI_UPGRADES_HEIGHT);

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
			if (energySlot.getEnergy() != null) {
				tip.add(this.energySlot.getEnergy().getEnergyName());
				drawHoveringText(tip, MouseX, MouseY, fontRenderer);
			}
		}

	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Call super
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (this.priority.isMouseOverButton(mouseX, mouseY)) {
			NetworkHandler.sendToServer(new PacketGuiChange(new GuiPriority(this.player.inventory, this.part),
					getX(),getY(),getZ(),player));
		}
		if (this.energySlot.isMouseOverWidget(mouseX, mouseY)) {
			if (player.inventory.getItemStack() == null) {
				energySlot.mouseClicked(null);
				return;
			}
			LiquidAIEnergy EnergyItem = Utils.getEnergyFromItemStack(this.player.inventory.getItemStack());
			if (EnergyItem == null)
				return;
			try {
				energySlot.mouseClicked(EnergyItem);

			} catch (Exception e) {
				AILog.debug(e + "");
			}
		}
		if (AIGuiHelper.INSTANCE.isPointInGuiRegion(8, -37, 16, 16, mouseX, mouseY, this.guiLeft, this.guiTop)) {
			this.CycleRedstoneMode(this.RSMode);
		}

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);


		//binding correct Gui
		if (LinkedMetric == RF || LinkedMetric == J || LinkedMetric == EU)
			this.energybar = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/" + LinkedMetric.getTag() + "Bar.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(energybar);
		// Drawing Name
		this.fontRenderer.drawString(I18n.translateToLocal("ME Energy Interface"), 9, 3, 4210752);
		// Drawing Tooltips
		if (this.Einterface instanceof TileEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(this.energybar);
			drawPower(10, 11, mouseX - 10, mouseY - 10, 11, SOUTH);
			drawPower(36, 11, mouseX - 10, mouseY - 10, 11, NORTH);
			drawPower(65, 11, mouseX - 10, mouseY - 10, 11, WEST);
			drawPower(100, 11, mouseX - 10, mouseY - 10, 11, EAST);
			drawPower(129, 11, mouseX - 10, mouseY - 10, 11, UP);
			drawPower(155, 11, mouseX - 10, mouseY - 10, 11, DOWN);

		} else if (this.Einterface instanceof PartEnergyInterface) {
			Minecraft.getMinecraft().renderEngine.bindTexture(this.energybar);

			if( this.energySlot.d == null){
				if( getSide() != null ){
					this.energySlot.x = getX();
					this.energySlot.y = getY();
					this.energySlot.z = getZ();

					this.energySlot.d = getSide();
					this.energySlot.w = getWorld();
				}
			}
			if (this.LinkedMetric != WA) {
				drawPower(83, 13, mouseX - 10, mouseY - 10, 10, null);
			} else {
				//int springValue = 0;
				//drawSpring(83,UNKNOWN,springValue);
				//springValue+=1;
			}
			this.energySlot.drawWidget();
			this.energySlot.setEnergy(this.LinkedFilter, 1);
		}

	}

	@Override
	public void onGuiClosed() {
		this.resetData();
	}

	protected void resetData() {
		for (Field f : this.getClass().getFields()) {
			try {
				if (f.isAnnotationPresent(resetData.class)) {
					Class<?> type = f.getType();
					try {
						if (type == Integer.class || type == int.class) {
							f.setInt(this, 0);
						} else if (type == boolean.class || type == Boolean.class) {
							f.setBoolean(this, false);
						} else {
							f.set(this, null);
						}
					} catch (IllegalAccessException e) {

					}
				}
			} catch (NullPointerException e) {

			}
		}
	}

	@Override
	public void updateEnergies(@Nonnull LiquidAIEnergy energy, int index) {
		this.LinkedFilter = energy;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.energySlot = new WidgetEnergySlot(this, this.player, 4, 79, 111, true);

		this.EnergySlotList.add(this.energySlot);


		priority = new GuiButtonAETab(0, this.guiLeft +
				GuiEnergyStoragePart.BUTTON_PRIORITY_X_POSITION, this.guiTop - 3, AEStateIconsEnum.WRENCH,
				"gui.appliedenergistics2.Priority");
		this.buttonList.add(priority);

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
				int height = this.getStorage(LinkedMetric, side) / (tile.getMaxEnergyStored(null) / 83);
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
							this.tile.getMaxEnergyStored(null),
							this.LinkedMetric.getEnergyName());
					drawMouseOver(str);
				}
			}
		} catch (Exception e) {

		}
	}

	private void drawSpring(int center, EnumFacing direction, int currentAngle) {
		GL11.glPushMatrix();

		GL11.glTranslatef(233f, 127f, 0);

		GL11.glRotatef(currentAngle, 0, 0, 45);
		GL11.glTranslatef(-233f, -127f, 0f);

		this.mc.getTextureManager().bindTexture(GuiOmega);
		this.drawTexturedModalRect(center - 16, center - 16, 1, 1, 32, 32);
		GL11.glPopMatrix();
	}

	public void drawMouseOver(String pText) {
		if (pText != null) {
			//Collections.addAll(mMouseHover, pText.split("\n"));
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

	public void CycleRedstoneMode(RedstoneMode mode) {
		if (mode == IGNORE) {
			this.RSMode = LOW_SIGNAL;
		}
		if (mode == LOW_SIGNAL) {
			this.RSMode = HIGH_SIGNAL;
		}
		if (mode == HIGH_SIGNAL) {
			this.RSMode = SIGNAL_PULSE;
		}
		if (mode == SIGNAL_PULSE) {
			this.RSMode = IGNORE;
		}
	}

	public void onFilterChangeConstructs() { }

	public void onStorageReceive(int storage, PartEnergyInterface sender) {
		if (this.LinkedContainer.onStorageReceive(sender)) this.storage = storage;
	}

	@Override
	public AIContainer getNodeContainer() {
		return this.LinkedContainer;
	}

}

