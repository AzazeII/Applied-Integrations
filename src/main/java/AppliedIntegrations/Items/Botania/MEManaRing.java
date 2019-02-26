package AppliedIntegrations.Items.Botania;

import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemEnum;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class MEManaRing extends MEManaMirror implements IManaItem, IManaTooltipDisplay, IBauble, ICosmeticAttachable {

    public MEManaRing(String registry) {
        super(registry);
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return 500000;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public ItemStack getCosmeticItem(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "#cosmeticItem", true);
        if(cmp == null)
            return ItemStack.EMPTY;
        return new ItemStack(cmp);
    }

    @Override
    public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
        NBTTagCompound cmp = new NBTTagCompound();
        if(!cosmetic.isEmpty())
            cmp = cosmetic.writeToNBT(cmp);
        ItemNBTHelper.setCompound(stack, "#cosmeticItem", cmp);
    }

    @Override
    public boolean canReceiveManaFromPool(ItemStack itemStack, TileEntity tileEntity) {
        return true;
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        return itemStack.getItem() == ItemEnum.ITEMMANAWIRELESSRING.getItem();
    }
}
