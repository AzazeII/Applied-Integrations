package AppliedIntegrations.Blocks.MultiController;


import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import appeng.util.Platform;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * @Author Azazell
 */
public class BlockMultiControllerTerminal extends BlockAIRegistrable implements ITileEntityProvider {
	public BlockMultiControllerTerminal(String reg, String unloc) {
		super(reg, unloc);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		// Make block invisible, and give all render handling to TESR
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public TileEntity createNewTileEntity(@Nullable World p_149915_1_, int p_149915_2_) {
		return new TileMultiControllerTerminal();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, p, hand, facing, hitX, hitY, hitZ);

		if (!p.isSneaking()) {
			ItemStack stack = p.getHeldItem(hand);
			TileMultiControllerTerminal tile = (TileMultiControllerTerminal) world.getTileEntity(pos);

			if (Platform.isWrench(p, stack, pos)) {
				tile.rotateForward(facing);
			} else {
				if (world.isRemote) {
					return false;
				}

				if (world.getTileEntity(pos) != null) {
					AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiServerTerminal, p, INTERNAL, pos);
					tile.updateRequested = true;
					return true;
				}
			}
		}
		return false;
	}
}
