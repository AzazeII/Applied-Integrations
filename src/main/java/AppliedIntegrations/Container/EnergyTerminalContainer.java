package AppliedIntegrations.Container;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEFluidStack;
import appeng.me.helpers.BaseActionSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
/**
 * @Author Azazell
 */
public class EnergyTerminalContainer extends Container implements IMEMonitorHandlerReceiver<IAEFluidStack>  {
	private EntityPlayer player;
	public EnergyTerminalContainer(EntityPlayer player) {
		
		bindPlayerInventory(this.player.inventory);
		}
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, i * 18 + 122));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 180));
		}
	}
	@Override
	public boolean isValid(Object verificationToken) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void postChange(IBaseMonitor<IAEFluidStack> iBaseMonitor, Iterable<IAEFluidStack> iterable, IActionSource iActionSource) {

	}

	@Override
	public void onListUpdate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		// TODO Auto-generated method stub
		return true;
	}

}
