package AppliedIntegrations.Items.Botania;

import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.implementations.items.IGrowableCrystal;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaItem;

import java.util.List;

public class MaterialEncorium extends AIItemRegistrable implements IBotaniaIntegrated {

    private String percent;
    public MaterialEncorium(String registry, String damage) {
        super(registry);
        this.percent = damage;
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void addInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips ) {
        if(!percent.equals("100%"))
            lines.add(percent);
    }
}
