package AppliedIntegrations.Gui;

import appeng.client.gui.implementations.GuiPriority;
import appeng.helpers.IPriorityHost;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPriorityAI extends GuiPriority {
    public GuiPriorityAI(InventoryPlayer inventory, IPriorityHost priorityHost) {
        super(inventory, priorityHost);
    }
}
