package AppliedIntegrations.Items.Part.Energy;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyExport;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartEnergyExport extends ItemPartAIBase<PartEnergyExport> {
	public ItemPartEnergyExport(String id) {
		super(id);
	}

	@Nullable
	@Override
	public PartEnergyExport createPartFromItemStack(ItemStack itemStack) {
		return new PartEnergyExport();
	}
}
