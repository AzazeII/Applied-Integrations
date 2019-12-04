package AppliedIntegrations.Blocks.Additions;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class BlockMETurret extends BlockAIRegistrable {

	// Does this block enabled in config?
	public static boolean METurret_Enabled = AIConfig.enableBlackHoleStorage;

	public BlockMETurret(String registryName, String unloc) {

		super(registryName, unloc);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		return new TileMETurretFoundation();
	}

	@Override
	public boolean isFullCube(IBlockState iBlockState) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState iBlockState) {

		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		// Pass activated to tile entity ( nothing new except we don't ignore sneaking:) )
		if (tile instanceof TileMETurretFoundation) {
			return ((TileMETurretFoundation) tile).activate(hand, p);
		}
		return false;
	}
}
