package AppliedIntegrations.Gui.SubGui;

import appeng.helpers.IPriorityHost;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPriority
        extends appeng.client.gui.implementations.GuiPriority
{
    public GuiPriority(InventoryPlayer inventoryPlayer, IPriorityHost te) {
        super(inventoryPlayer, te);
    }

}
