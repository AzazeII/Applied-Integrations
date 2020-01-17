package AppliedIntegrations.Items.Part.Energy;


import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyStorageMonitor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartEnergyStorageMonitor extends ItemPartAIBase<PartEnergyStorageMonitor> {
	public ItemPartEnergyStorageMonitor(String id) {
		super(id);
	}

	@Nullable
	@Override
	public PartEnergyStorageMonitor createPartFromItemStack(ItemStack itemStack) {
		return new PartEnergyStorageMonitor();
	}
}
