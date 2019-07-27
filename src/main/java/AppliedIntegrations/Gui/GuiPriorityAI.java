package AppliedIntegrations.Gui;


import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import AppliedIntegrations.Network.Packets.PacketPriorityChange;
import appeng.client.gui.implementations.GuiPriority;
import appeng.client.gui.widgets.GuiNumberBox;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.GuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @Author Azazell
 */
public class GuiPriorityAI extends GuiPriority {
	private IPriorityHostExtended host;

	private GuiButton originalTab;

	public GuiPriorityAI(InventoryPlayer inventory, IPriorityHostExtended priorityHost) {

		super(inventory, priorityHost);

		// Owner of original gui
		this.host = priorityHost;
	}

	public void initGui() {
		// Pass call to super
		super.initGui();
		// Add original tab button to button list
		this.buttonList.add(this.originalTab = new GuiTabButton(this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender));
	}

	@Override
	protected void keyTyped(final char character, final int key) throws IOException {

		super.keyTyped(character, key);

		try {
			// Get private field and make it accessible
			Field f = GuiPriority.class.getDeclaredField("priority");

			// Make field accessible
			f.setAccessible(true);

			// Get private priority field
			GuiNumberBox priority = (GuiNumberBox) f.get(this);

			// Send packet
			NetworkHandler.sendToServer(new PacketPriorityChange(priority.getText(), host));
		} catch (Exception ignored) {
		}
	}

	@Override
	protected void actionPerformed(final GuiButton btn) throws IOException {

		super.actionPerformed(btn);

		// Check if button is original tab
		if (btn == originalTab) {
			// Switch gui to original gui
			NetworkHandler.sendToServer(new PacketGuiShift(host.getGui(), host));
		}
	}
}
