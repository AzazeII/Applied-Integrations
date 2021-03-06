package AppliedIntegrations.Items;


import AppliedIntegrations.Parts.AIPart;
import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public abstract class ItemPartAIBase<EnergyPart extends AIPart> extends AIItemRegistrable implements IPartItem<EnergyPart> {
	public ItemPartAIBase(String registry) {
		super(registry);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return AEApi.instance().partHelper().placeBus(player.getHeldItem(hand), pos, side, player, hand, world);
	}
}
