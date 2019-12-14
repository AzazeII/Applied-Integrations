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
	private static ContainerEnergyTerminal linkedContainer;

	private ResourceLocation mainTexture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.terminal.png");

	private EntityPlayer player;

	private PartEnergyTerminal part;

	public GuiEnergyTerminalDuality(ContainerEnergyTerminal container, PartEnergyTerminal partEnergyTerminal, EntityPlayer player) {

		super(container, player);

		linkedContainer = container;

		this.player = player;
		this.part = partEnergyTerminal;

		this.xSize = 195;
		this.ySize = 204;

		// Rows
		for (int y = 0; y < WIDGET_ROWS_PER_PAGE; y++) {
			// Columns
			for (int x = 0; x < WIDGETS_PER_ROW; x++) {
				// Update widget in array
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
		// Full white
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Set the texture to the gui's texture
		Minecraft.getMinecraft().renderEngine.bindTexture(this.mainTexture);

		// Draw the gui
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

		// Check if click was performed on sort mode button
		if (btn == castContainer().sortButton) {
			// Get current mode ordinal
			byte ordinal = (byte) castContainer().sortButton.getCurrentValue().ordinal();

			// Switch to next mode
			castContainer().sortButton.set(ordinal == 3 ? SortOrder.NAME : SortOrder.values()[ordinal + 1]);

			// Change sorting mode
			castContainer().sortMode = (SortOrder) castContainer().sortButton.getCurrentValue();

			// Create sorted list from current list
			List<IAEEnergyStack> sorted = castContainer().sorter.sortedCopy(castContainer().list);

			// Clear current list
			castContainer().list = new EnergyList();

			// Iterate for each entry of sorted copy of list
			// Add entry in order of list
			sorted.forEach(castContainer().list::add);

			// Call update function
			castContainer().updateStacksPrecise(sorted);

			// Send packet
			NetworkHandler.sendToServer(new PacketEnum(castContainer().sortButton.getCurrentValue(), this.part));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// Check not null
		final LiquidAIEnergy energy = castContainer().selectedStack.getEnergy();
		if (energy != null && energy.getEnergyName() != null) {
			// Draw energy name
			this.fontRenderer.drawString("Energy: " + energy.getEnergyName(), 45, 101, 0);
		}

		// Check stack size greater than zero
		if (castContainer().selectedStack.amount > 0) {
			// Draw energy amount
			this.fontRenderer.drawString("Amount: " + castContainer().selectedStack.amount, 45, 91, 0);
		}

		// Iterate for each widget
		// Draw each widget
		castContainer().widgetEnergySelectors.forEach((WidgetEnergySelector::drawWidget));

		// Draw name of GUI
		fontRenderer.drawString(I18n.translateToLocal("ME Energy Terminal"), 9, 3, 4210752);

		// Add tooltip to sort button
		// Check if mouse over sort button
		if (castContainer().sortButton.isMouseOver()) {
			// Split messages using regex "\n"
			tooltip.addAll(Arrays.asList(castContainer().sortButton.getMessage().split("\n")));
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Call super
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Iterate for each selector
		castContainer().widgetEnergySelectors.forEach((widgetEnergySelector -> {
			// Check if mouse is over widget
			if (widgetEnergySelector.isMouseOverWidget(mouseX, mouseY)) {
				// Check not null
				if (widgetEnergySelector.getCurrentStack() == null) {
					return;
				}

				// Update current energy stack
				castContainer().selectedStack = widgetEnergySelector.getCurrentStack();
			}
		}));
	}
}
