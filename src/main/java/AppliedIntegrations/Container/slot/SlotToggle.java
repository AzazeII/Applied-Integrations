package AppliedIntegrations.Container.slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
public class SlotToggle extends Slot {
	public boolean isEnabled = true;

	public SlotToggle(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@SideOnly(Side.CLIENT)
	public boolean isEnabled() {
		return isEnabled;
	}
}
