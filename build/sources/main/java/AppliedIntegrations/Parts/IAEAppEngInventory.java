package AppliedIntegrations.Parts;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public interface IAEAppEngInventory
{

    void saveChanges();

    void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack );
}
