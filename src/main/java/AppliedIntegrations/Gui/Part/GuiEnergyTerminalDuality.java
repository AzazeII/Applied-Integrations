package AppliedIntegrations.Gui.Part;


/**
 * @Author Azazell
 */
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySelector;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketEnum;
import AppliedIntegrations.Network.Packets.PartGUI.PacketSelectedStack;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import AppliedIntegrations.api.IEnergySelectorContainer;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.EnergyList;
import appeng.api.config.Settings;
import appeng.api.config.SortOrder;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiEnergyTerminalDuality extends AIGui implements IEnergySelectorGui {
	private static final int WIDGETS_PER_ROW = 9;

	private static final int WIDGET_ROWS_PER_PAGE = 4;

	@Nonnull
	private ContainerEnergyTerminal linkedContainer;

	private ResourceLocation mainTexture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.terminal.png");
	private PartEnergyTerminal part;

	public GuiEnergyTerminalDuality(@Nonnull ContainerEnergyTerminal container, PartEnergyTerminal partEnergyTerminal, EntityPlayer player) {
		super(container, player);
		this.part = partEnergyTerminal;
		this.xSize = 195;
		this.ySize = 204;
		this.linkedContainer = container;

		for (int y = 0; y < WIDGET_ROWS_PER_PAGE; y++) {
			for (int x = 0; x < WIDGETS_PER_ROW; x++) {
				castContainer().widgetEnergySelectors.add(new WidgetEnergySelector(this, 7 + (x * 18), 17 + (y * 18)));
			}
		}
	}

	private ContainerEnergyTerminal castContainer() {
		return (ContainerEnergyTerminal) this.getContainer();
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(castContainer().sortButton = new GuiImgButton(this.guiLeft - 18, this.guiTop, Settings.SORT_BY, castContainer().sortMode));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(this.mainTexture);
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Nonnull
	@Override
	public IEnergySelectorContainer getContainer() {
		return linkedContainer;
	}

	@Nullable
	@Override
	public LiquidAIEnergy getSelectedEnergy() {
		return castContainer().selectedStack.getEnergy();
	}

	@Override
	public void setSelectedEnergy(@Nullable LiquidAIEnergy energy) {
		castContainer().selectedStack.setEnergy(energy);
	}

	@Override
	public void setAmount(long stackSize) {
		castContainer().selectedStack.amount = stackSize;
	}

	@Override
	public ISyncHost getSyncHost() {
		return part;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		if (host instanceof PartEnergyTerminal) {
			part = (PartEnergyTerminal) host;
		}
	}

	@Override
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {
		super.onButtonClicked(btn, mouseButton);

		if (btn == castContainer().sortButton) {
			byte ordinal = (byte) castContainer().sortButton.getCurrentValue().ordinal();
			castContainer().sortButton.set(ordinal == 3 ? SortOrder.NAME : SortOrder.values()[ordinal + 1]);
			castContainer().sortMode = (SortOrder) castContainer().sortButton.getCurrentValue();

			List<IAEEnergyStack> sorted = castContainer().sorter.sortedCopy(castContainer().list);
			castContainer().list = new EnergyList();

			sorted.forEach(castContainer().list::add);
			castContainer().updateStacksPrecise(sorted);

			NetworkHandler.sendToServer(new PacketEnum(castContainer().sortButton.getCurrentValue(), this.part));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final LiquidAIEnergy energy = castContainer().selectedStack.getEnergy();
		if (energy != null && energy.getEnergyName() != null) {
			this.fontRenderer.drawString("Energy: " + energy.getEnergyName(), 45, 101, 0);
		}

		if (castContainer().selectedStack.amount > 0) {
			this.fontRenderer.drawString("Amount: " + castContainer().selectedStack.amount, 45, 91, 0);
		}

		castContainer().widgetEnergySelectors.forEach((WidgetEnergySelector::drawWidget));
		fontRenderer.drawString(I18n.translateToLocal("ME Energy Terminal"), 9, 3, 4210752);
		if (castContainer().sortButton.isMouseOver()) {
			tooltip.addAll(Arrays.asList(castContainer().sortButton.getMessage().split("\n")));
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		castContainer().widgetEnergySelectors.forEach((widgetEnergySelector -> {
			if (widgetEnergySelector.isMouseOverWidget(mouseX, mouseY)) {
				if (widgetEnergySelector.getCurrentStack() == null) {
					return;
				}

				castContainer().selectedStack = widgetEnergySelector.getCurrentStack();
				if (castContainer().selectedStack != null) {
					NetworkHandler.sendToServer(new PacketSelectedStack(castContainer().selectedStack.getEnergy(), this.part));
				} else {
					NetworkHandler.sendToServer(new PacketSelectedStack(null, this.part));
				}
			}
		}));
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		NetworkHandler.sendToServer(new PacketSelectedStack(null, this.part));
	}
}
