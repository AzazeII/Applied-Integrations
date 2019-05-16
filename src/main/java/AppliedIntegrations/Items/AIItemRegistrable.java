package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * @Author Azazell
 */
public class AIItemRegistrable extends Item {
	public String reg;

	public AIItemRegistrable(String registry) {
		this.setRegistryName(registry);
		this.setUnlocalizedName(registry);
		this.setCreativeTab(AppliedIntegrations.AI);

		this.reg = registry;
	}

	void registerModel() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}
}
