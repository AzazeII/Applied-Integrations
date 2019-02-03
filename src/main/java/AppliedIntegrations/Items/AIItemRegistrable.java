package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.item.Item;

public class AIItemRegistrable extends Item {
    public AIItemRegistrable(String registry){
        this.setRegistryName(registry+"Register");
        this.setUnlocalizedName(registry);
        this.setCreativeTab(AppliedIntegrations.AI);
    }
}
