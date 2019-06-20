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
public abstract class AIBaseGui extends GuiContainer implements IWidgetHost {

	protected static final int GUI_MAIN_WIDTH = 176;

	protected static final int GUI_UPGRADES_WIDTH = 35;

	protected static final int GUI_UPGRADES_HEIGHT = 35;

	// Relative x position of priority button
	private static final int BUTTON_PRIORITY_X_POSITION = 154;

	// Used when "AIBaseGui::drawTooltip" is called
	protected final List<String> tooltip = new ArrayList<String>();

	protected GuiTabButton priorityButton;

	private EntityPlayer player;

	public AIBaseGui(final Container container, EntityPlayer player) {

		super(container);

		// Update player
		this.player = player;
	}

	protected void addPriorityButton() {

		priorityButton = new GuiTabButton(this.guiLeft + BUTTON_PRIORITY_X_POSITION, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender);
		this.buttonList.add(priorityButton);
	}

	// Called when LMB is clicked
	@Override
	public final void actionPerformed(final GuiButton button) {

		this.onButtonClicked(button, AIGuiHelper.MOUSE_BUTTON_LEFT);
	}

	// Called when any button is clicked
	public void onButtonClicked(final GuiButton btn, final int mouseButton) {

	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float mouseButton) {
		// Call super
		super.drawScreen(mouseX, mouseY, mouseButton);

		// Empty tooltip?
		if (this.tooltip.isEmpty()) {
			// Get the tooltip from the buttons
			this.addTooltipFromButtons(mouseX, mouseY);
		}

		// Draw the tooltip
		if (!this.tooltip.isEmpty()) {
			// Draw
			this.drawHoveringText(this.tooltip, mouseX, mouseY, this.fontRenderer);

			// Clear the tooltip
			this.tooltip.clear();
		}

		// Draw minecraft tooltip of item
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	private boolean addTooltipFromButtons(final int mouseX, final int mouseY) {
		// Is the mouse over any buttons?
		for (Object obj : this.buttonList) {
			// Is it a base button?
			if (obj instanceof AIGuiButton) {
				// Cast
				AIGuiButton currentButton = (AIGuiButton) obj;

				// Is the mouse over it?
				if (currentButton.isMouseOverButton(mouseX, mouseY)) {
					// Get the tooltip
					currentButton.getTooltip(this.tooltip);

					// And stop searching
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Check not null
		if (priorityButton == null) {
			return;
		}

		// Add tooltip to priority button
		// Check if mouse over priority button
		if (priorityButton.isMouseOver()) {
			// Split messages using regex "\n"
			tooltip.addAll(Arrays.asList(priorityButton.getMessage().split("\n")));
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		// Is this container one that could have a network tool?
		if (this.inventorySlots instanceof ContainerWithUpgradeSlots) {
			// Do we have a network tool?
			if (((ContainerWithUpgradeSlots) this.inventorySlots).hasNetworkTool()) {
				// Get the slot the mouse was clicked over
				Slot slot = this.getSlotAtMousePosition(mouseX, mouseY);

				// Get optional stack
				Optional<ItemStack> stackOptional = AEApi.instance().definitions().items().networkTool().maybeStack(1);

				// Check present
				if (stackOptional.isPresent()) {
					// Was the slot the network tool?
					if ((slot != null) && (slot.getStack().isItemEqual(stackOptional.get()))) {
						// Do not allow any interaction with the network tool slot.
						return;
					}
				}
			}
		}
		try {
			// Pass to super
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException ignored) {

		}
	}

	// Get slot [or null] at specified point
	private final Slot getSlotAtMousePosition(final int x, final int y) {
		// Loop over all slots
		for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
			// Get the slot
			Slot slot = this.inventorySlots.inventorySlots.get(i);

			// Is the point within the slot?
			if (this.isPointWithinSlot(slot, x, y)) {
				// Return the slot
				return slot;
			}
		}

		// Point was not within any slot
		return null;
	}

	// Check if specified
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
