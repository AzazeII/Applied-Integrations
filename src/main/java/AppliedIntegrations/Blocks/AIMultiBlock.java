package AppliedIntegrations.Blocks;


import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * @Author Azazell
 */
public abstract class AIMultiBlock extends BlockAIRegistrable implements ITileEntityProvider {

	protected AIMultiBlock(String registry, String unlocalizedName) {

		super(registry, unlocalizedName);
		this.setHardness(5F);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (Platform.isServer()) {
			if (Platform.isWrench(p, p.inventory.getCurrentItem(), pos)) {
				if (!p.isSneaking()) {
					((IAIMultiBlock) world.getTileEntity(pos)).tryConstruct(p);
					return true;
				} else {
					final List<ItemStack> list = Lists.newArrayList(Platform.getBlockDrops(world, pos));
					Platform.spawnDrops(world, pos, list);
					world.setBlockToAir(pos);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState blockState) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World w, int p_149915_2_) {
		for (BlocksEnum b : BlocksEnum.values()) {
			if (b.b == this) {
				try {
					return (TileEntity) b.tileEnum.clazz.newInstance();
				} catch (Exception ignored) {

				}
			}
		}
		return null;
	}
}
