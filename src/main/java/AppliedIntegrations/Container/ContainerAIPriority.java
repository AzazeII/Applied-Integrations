package AppliedIntegrations.Container;

import appeng.container.implementations.ContainerPriority;
import appeng.helpers.IPriorityHost;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAIPriority extends ContainerPriority {
    public ContainerAIPriority(InventoryPlayer ip, IPriorityHost te) {
        super(ip, te);
    }
}
