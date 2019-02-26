package AppliedIntegrations.Items.Botania;

import AppliedIntegrations.Items.ItemEnum;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class MEGreaterManaRing extends MEManaRing{
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
