package AppliedIntegrations.Items.Botania;


import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.ItemEnum;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "vazkii.botania.api.mana.IManaItem", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.mana.IManaTooltipDisplay", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.item.ICosmeticAttachable", modid = "botania", striprefs = true), @Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles", striprefs = true), @Optional.Interface(iface = "baubles.api.BaubleType", modid = "baubles", striprefs = true)})
/**
 * @Author Azazell
 */ public class MEGreaterManaRing extends MEManaRing implements IBotaniaIntegrated {
	public MEGreaterManaRing(String registry) {

		super(registry);
	}

	@Override
	public int getMaxMana(ItemStack stack) {

		return 2000000;
	}

	@Override
	public boolean canHandle(ItemStack itemStack) {

		return itemStack.getItem() == ItemEnum.ITEMMANAWIRELESSGREATRING.getItem();
	}
}
