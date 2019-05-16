package AppliedIntegrations.Items.Part.Energy;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyFormation;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartEnergyFormation extends ItemPartAIBase<PartEnergyFormation> {
	public ItemPartEnergyFormation(String id) {
		super(id);
	}

	@Nullable
	@Override
	public PartEnergyFormation createPartFromItemStack(ItemStack itemStack) {
		return new PartEnergyFormation();
	}
}
