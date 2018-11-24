package AppliedIntegrations.Items.StorageCells;

import AppliedIntegrations.AppliedIntegrations;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import java.util.List;

import static AppliedIntegrations.Items.StorageCells.EnergyStorageCell.suffixes;

public class EnergyStorageComponent
        extends Item
{

    /**
     * Component icons.
     */
    private IIcon[] icons;
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
    public IIcon getIconFromDamage( final int damage )
    {
        // Return icon
        int j = MathHelper.clamp_int(damage, 0, suffixes.length);
        return this.icons[j];
    }

    @Override
    public EnumRarity getRarity(final ItemStack itemStack )
    {
        // Return the rarity
        return EnumRarity.epic;
    }

    @Override
    public void getSubItems(final Item item, final CreativeTabs creativeTab, final List itemList )
    {
        for (int i = 0; i < suffixes.length-1; ++i) {
            itemList.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName()
    {
        return AppliedIntegrations.modid + ".item.storage.component";
    }

    @Override
    public String getUnlocalizedName( final ItemStack itemStack )
    {
        return "ME " + suffixes[itemStack.getItemDamage()] + " Component";
    }
    @Override
    public void registerIcons( final IIconRegister iconRegister )
    {
        // Create the icon array
        this.icons = new IIcon[suffixes.length];

        // Add each type
        for( int i = 0; i < this.icons.length-1; i++ )
        {
                this.icons[i] = iconRegister.registerIcon( AppliedIntegrations.modid + ":" + this.TextureNames[i]);
        }
    }

}
