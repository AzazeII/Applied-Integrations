package AppliedIntegrations.Gui.SubGui;

import appeng.helpers.IPriorityHost;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;

@SideOnly(Side.CLIENT)
public class GuiPriority
        extends appeng.client.gui.implementations.GuiPriority
{
    public GuiPriority(InventoryPlayer inventoryPlayer, IPriorityHost te) {
        super(inventoryPlayer, te);
    }

}
