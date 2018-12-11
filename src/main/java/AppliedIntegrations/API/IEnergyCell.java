package AppliedIntegrations.API;

import java.util.ArrayList;

import appeng.api.storage.ICellWorkbenchItem;
import net.minecraft.item.ItemStack;
/**
 * @Author Azazell
 */
public interface IEnergyCell extends ICellWorkbenchItem {
	public ArrayList<LiquidAIEnergy> getFilter(ItemStack is);

	public int getMaxBytes(ItemStack is);

	public int getMaxTypes(ItemStack is);

} 
