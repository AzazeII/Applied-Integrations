package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemPartP2PTunnel<Tunnel extends PartP2PTunnel<Tunnel>> extends AIItemRegistrable implements IPartItem<Tunnel> {
	public ItemPartP2PTunnel(String registry) {
		super(registry);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return AEApi.instance().partHelper().placeBus(player.getHeldItem(hand), pos, side, player, hand, world);
	}
}
