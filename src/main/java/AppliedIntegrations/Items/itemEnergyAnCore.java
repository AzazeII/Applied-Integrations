package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;

import net.minecraft.item.Item;
/**
 * @Author Azazell
 */
public class itemEnergyAnCore extends Item{
	public itemEnergyAnCore() {
		super();
		this.setCreativeTab(AppliedIntegrations.AI);
		this.setTextureName(AppliedIntegrations.modid+":itemEnergyAnCore");
		this.setUnlocalizedName("ANCore");
	}
}
