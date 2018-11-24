package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.item.Item;
/**
 * @Author Azazell
 */
public class itemEnergyFmCore extends Item {
	public itemEnergyFmCore() {
		super();
		this.setCreativeTab(AppliedIntegrations.AI);
		this.setTextureName(AppliedIntegrations.modid+":itemEnergyFmCore");
		this.setUnlocalizedName("FMCore");
	}
}
