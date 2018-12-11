package AppliedIntegrations.API;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
/**
 * @Author Azazell
 */
public class EnergySlot {
    Slot a;
    private final int slotIndex;
    public final IInventory inventory;
    public int slotNumber;
    /** display position of the inventory slot on the screen x axis */
    public int xDisplayPosition;
    /** display position of the inventory slot on the screen y axis */
    public int yDisplayPosition;
    protected IIcon backgroundIcon = null;
    public EnergySlot(IInventory inv, int index, int x, int y)
    {
        this.inventory = inv;
        this.slotIndex = index;
        this.xDisplayPosition = x;
        this.yDisplayPosition = y;
    }


}
