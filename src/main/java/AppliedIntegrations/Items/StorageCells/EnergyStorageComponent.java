package AppliedIntegrations.Items.StorageCells;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.AIItemRegistrable;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class EnergyStorageComponent
        extends AIItemRegistrable
{
    public EnergyStorageComponent(String regName)
    {
        super(regName);
        // No damage
        this.setMaxDamage( 0 );

        // Has subtypes
        this.setHasSubtypes( true );

        this.setCreativeTab(AppliedIntegrations.AI);
    }
}
