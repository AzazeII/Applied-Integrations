package AppliedIntegrations.Items.Part.Energy;


import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartEnergyStorage extends ItemPartAIBase<PartEnergyStorage> {
	public ItemPartEnergyStorage(String id) {

		super(id);
	}

	@Nullable
	@Override
	public PartEnergyStorage createPartFromItemStack(ItemStack itemStack) {

		return new PartEnergyStorage();
	}
}
