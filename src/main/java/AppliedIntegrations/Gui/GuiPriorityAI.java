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
		this.host = priorityHost;
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(
				this.originalTab =
						new GuiTabButton(this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender));
	}

	@Override
	protected void keyTyped(final char character, final int key) throws IOException {
		super.keyTyped(character, key);

		try {
			Field f = GuiPriority.class.getDeclaredField("priority");
			f.setAccessible(true);

			GuiNumberBox priority = (GuiNumberBox) f.get(this);
			NetworkHandler.sendToServer(new PacketPriorityChange(priority.getText(), host));
		} catch (Exception ignored) {
		}
	}

	@Override
	protected void actionPerformed(final GuiButton btn) throws IOException {
		super.actionPerformed(btn);

		if (btn == originalTab) {
			NetworkHandler.sendToServer(new PacketGuiShift(host.getGui(), host));
		}
	}
}
