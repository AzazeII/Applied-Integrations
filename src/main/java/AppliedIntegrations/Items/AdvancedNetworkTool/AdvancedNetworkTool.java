package AppliedIntegrations.Items.AdvancedNetworkTool;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.implementations.items.IAEWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 */
public class AdvancedNetworkTool extends AIItemRegistrable implements IAEWrench {
	public AdvancedNetworkTool(String reg) {

		super(reg);

		this.setCreativeTab(AppliedIntegrations.AI);

		this.setMaxStackSize(1);
	}

	@Override
	public boolean canWrench(ItemStack wrench, EntityPlayer player, BlockPos pos) {
		return true;
	}
}
