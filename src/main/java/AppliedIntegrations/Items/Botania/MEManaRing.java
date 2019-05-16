package AppliedIntegrations.Items.Botania;


import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.ItemEnum;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.core.helper.ItemNBTHelper;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "vazkii.botania.api.mana.IManaItem", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.mana.IManaTooltipDisplay", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.item.ICosmeticAttachable", modid = "botania", striprefs = true), @Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles", striprefs = true), @Optional.Interface(iface = "baubles.api.BaubleType", modid = "baubles", striprefs = true)})
/**
 * @Author Azazell
 */ public class MEManaRing extends MEManaMirror implements IBotaniaIntegrated, IManaItem, IManaTooltipDisplay, IBauble, ICosmeticAttachable {

	public MEManaRing(String registry) {

		super(registry);
	}

	@Override
	public int getMaxMana(ItemStack stack) {

		return 500000;
	}

	@Override
	public boolean canReceiveManaFromPool(ItemStack itemStack, TileEntity tileEntity) {

		return true;
	}

	@Override
	public boolean canHandle(ItemStack itemStack) {

		return itemStack.getItem() == ItemEnum.ITEMMANAWIRELESSRING.getItem();
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {

		return BaubleType.RING;
	}

	@Override
	public ItemStack getCosmeticItem(ItemStack stack) {

		NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "#cosmeticItem", true);
		if (cmp == null) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(cmp);
	}

	@Override
	public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {

		NBTTagCompound cmp = new NBTTagCompound();
		if (!cosmetic.isEmpty()) {
			cmp = cosmetic.writeToNBT(cmp);
		}
		ItemNBTHelper.setCompound(stack, "#cosmeticItem", cmp);
	}
}
