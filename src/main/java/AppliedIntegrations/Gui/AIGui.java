package AppliedIntegrations.Gui;
import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import appeng.api.AEApi;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.GuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIGui extends GuiContainer implements IWidgetHost {
	protected static final int GUI_MAIN_WIDTH = 176;
	protected static final int GUI_UPGRADES_WIDTH = 35;
	protected static final int GUI_UPGRADES_HEIGHT = 35;
	private static final int BUTTON_PRIORITY_X_POSITION = 154;

	protected final List<String> tooltip = new ArrayList<String>();

	protected GuiTabButton priorityButton;

	public EntityPlayer player;

	public AIGui(final Container container, EntityPlayer player) {
		super(container);

		this.player = player;
	}

	protected void addPriorityButton() {
		priorityButton = new GuiTabButton(this.guiLeft + BUTTON_PRIORITY_X_POSITION, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender);
		this.buttonList.add(priorityButton);
	}

	@Override
	public final void actionPerformed(final GuiButton button) {
		this.onButtonClicked(button, AIGuiHelper.MOUSE_BUTTON_LEFT);
	}

	public void onButtonClicked(final GuiButton btn, final int mouseButton) {

	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float mouseButton) {
		super.drawScreen(mouseX, mouseY, mouseButton);

		if (this.tooltip.isEmpty()) {
			this.addTooltipFromButtons(mouseX, mouseY);
		}

		if (!this.tooltip.isEmpty()) {
			this.drawHoveringText(this.tooltip, mouseX, mouseY, this.fontRenderer);
			this.tooltip.clear();
		}

		this.renderHoveredToolTip(mouseX, mouseY);
	}

	private boolean addTooltipFromButtons(final int mouseX, final int mouseY) {
		for (Object obj : this.buttonList) {
			if (obj instanceof AIGuiButton) {
				AIGuiButton currentButton = (AIGuiButton) obj;

				if (currentButton.isMouseOverButton(mouseX, mouseY)) {
					currentButton.getTooltip(this.tooltip);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		if (priorityButton == null) {
			return;
		}

		if (priorityButton.isMouseOver()) {
			tooltip.addAll(Arrays.asList(priorityButton.getMessage().split("\n")));
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		if (this.inventorySlots instanceof ContainerWithUpgradeSlots) {
			if (((ContainerWithUpgradeSlots) this.inventorySlots).hasNetworkTool()) {
				Slot slot = this.getSlotAtMousePosition(mouseX, mouseY);
				Optional<ItemStack> stackOptional = AEApi.instance().definitions().items().networkTool().maybeStack(1);

				if (stackOptional.isPresent()) {
					if ((slot != null) && (slot.getStack().isItemEqual(stackOptional.get()))) {
						return;
					}
				}
			}
		}
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException ignored) {

		}
	}

	// Get slot [or null] at specified point
	private Slot getSlotAtMousePosition(final int x, final int y) {
		for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
			Slot slot = this.inventorySlots.inventorySlots.get(i);

			if (this.isPointWithinSlot(slot, x, y)) {
				return slot;
			}
		}

		return null;
	}

	private final boolean isPointWithinSlot(final Slot slot, final int x, final int y) {
		return AIGuiHelper.INSTANCE.isPointInGuiRegion(slot.yPos, slot.xPos, 16, 16, x, y, this.guiLeft, this.guiTop);
	}

	/**
	 * Gets the starting X position for the Gui.
	 */
	@Override
	public final int getLeft() {
		return this.guiLeft;
	}

	/**
	 * Gets the starting Y position for the Gui.
	 */
	@Override
	public final int getTop() {
		return this.guiTop;
	}
}
