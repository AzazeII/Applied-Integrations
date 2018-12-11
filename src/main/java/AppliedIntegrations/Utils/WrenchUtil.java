package AppliedIntegrations.Utils;

import appeng.api.implementations.items.IAEWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
/**
 * @Author Azazell
 */
public class WrenchUtil {

    public static boolean canWrench(ItemStack wrench, EntityPlayer player,
                                    int x, int y, int z) {
        if (wrench == null || wrench.getItem() == null)
            return false;
        try {

        } catch (Throwable e) {}
        if (wrench.getItem() instanceof IAEWrench) {
            IAEWrench w = (IAEWrench) wrench.getItem();
            return w.canWrench(wrench, player, x, y, z);
        }
        return false;
    }

    public static void wrenchUsed(ItemStack wrench, EntityPlayer player, int x,
                                  int y, int z) {
        if (wrench == null || wrench.getItem() == null)
            return;
        try {
        } catch (Throwable e) {}
    }
}
