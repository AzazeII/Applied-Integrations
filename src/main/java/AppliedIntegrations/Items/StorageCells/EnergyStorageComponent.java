package AppliedIntegrations.Items.StorageCells;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.AIItemRegistrable;

/**
 * @Author Azazell
 */
public class EnergyStorageComponent extends AIItemRegistrable {
	public EnergyStorageComponent(String regName) {
		super(regName);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(AppliedIntegrations.AI);
	}
}
