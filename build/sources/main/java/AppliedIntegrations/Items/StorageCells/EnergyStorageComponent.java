package AppliedIntegrations.Items.StorageCells;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnergyStorageComponent
        extends Item
{

    private String[] TextureNames = {"storage.component.1k","storage.component.4k","storage.component.16k","storage.component.64k","storage.component.256k",
            "storage.component.1024k","storage.component.4096k","storage.component.16384k"};


    public EnergyStorageComponent()
    {
        // No damage
        this.setMaxDamage( 0 );

        // Has subtypes
        this.setHasSubtypes( true );

        // Goes in ThE's creative tab.
        this.setCreativeTab(AppliedIntegrations.AI);
    }

    @Override
    public EnumRarity getRarity(final ItemStack itemStack )
    {
        // Return the rarity
        return EnumRarity.EPIC;
    }

}
