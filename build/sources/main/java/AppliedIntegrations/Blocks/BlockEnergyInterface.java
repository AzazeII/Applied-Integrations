package AppliedIntegrations.Blocks;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
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
public class BlockEnergyInterface extends BlockAIRegistrable {
	private boolean isThirdClick = false;

	public BlockEnergyInterface() {
		super("EInterface", "ME Energy Interface");
		this.setCreativeTab(AppliedIntegrations.AI);
		this.setHardness(5F);
	}
	@Override
	public TileEnergyInterface createNewTileEntity(World world, int metadata) {
		return new TileEnergyInterface();
	}

	@Override
	public boolean hasTileEntity(IBlockState blockState) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = world.getBlockState(pos).getBlock();
		TileEntity entity = world.getTileEntity(pos);

		if(p.isSneaking()) {
			if (entity instanceof TileEnergyInterface) {
				((TileEnergyInterface) entity).onActivate(p);
			}
		}

		// TODO: 2019-02-19 Fix GUI opening crash
		if(!world.isRemote) {
			if (Platform.isWrench(p, p.inventory.getCurrentItem(), pos)) {
				if (p.isSneaking()) {
					final List<ItemStack> list = Lists.newArrayList(Platform.getBlockDrops(world, pos));
					Platform.spawnDrops(world, pos, list);
					world.setBlockToAir(pos);
					return false;
				}
			} else if (block != null && entity instanceof TileEntity && p != null) {
				// if not sneaking open gui
				if (!p.isSneaking()) {
					//p.openGui(AppliedIntegrations.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
					return true;
				}
			}
		}
		return false;
	}
}


