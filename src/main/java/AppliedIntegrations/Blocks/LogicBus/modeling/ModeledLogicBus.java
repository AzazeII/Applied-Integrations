package AppliedIntegrations.Blocks.LogicBus.modeling;


import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.tile.LogicBus.TileLogicBusSlave;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
public abstract class ModeledLogicBus extends AIMultiBlock {
	// If valid = true, then logic bus formed
	public static final PropertyBool valid = PropertyBool.create("valid");

	// Property for valid state
	public static final LogicBusStateProperty stateProp = new LogicBusStateProperty();

	protected ModeledLogicBus(String registry, String unlocalizedName) {

		super(registry, unlocalizedName);

		// Set default state to not formed
		this.setDefaultState(this.getDefaultState().withProperty(valid, false));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState iBlockState) {

		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {

		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(valid, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(valid).booleanValue() ? 1 : 0;
	}

	@Override
	public boolean isFullCube(IBlockState iBlockState) {

		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		final float shave = 2.0f / 16.0f;
		// cut out 2 first and 2 final pixels at x, y and z
		return new AxisAlignedBB(shave, shave, shave, 1.0f - shave, 1.0f - shave, 1.0f - shave);
	}

	@Override
	public boolean isOpaqueCube(IBlockState iBlockState) {

		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

		return this.getDefaultState().withProperty(valid, false);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		// Create state
		return new ExtendedBlockState(this, new IProperty[]{valid}, new IUnlistedProperty[]{stateProp});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		IExtendedBlockState extState = (IExtendedBlockState) state;

		TileLogicBusSlave slave = (TileLogicBusSlave) world.getTileEntity(pos);
		if (slave != null) {
			LogicBusState formed = new LogicBusState(slave.getSidesWithSlaves(), slave.isCorner(), slave.hasMaster());
			extState = extState.withProperty(stateProp, formed);
		}

		return extState;
	}

	@Override
	public void breakBlock(final World w, final BlockPos pos, final IBlockState state) {

		TileEntity tile = w.getTileEntity(pos);
		if (tile instanceof TileLogicBusSlave) {
			TileLogicBusSlave slave = (TileLogicBusSlave) tile;

			if (slave.hasMaster()) {
				((TileLogicBusCore) slave.getMaster()).destroyMultiBlock();
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);
		if (!p.isSneaking()) {
			// Pass activated to tile entity ( nothing new :) )
			if (tile instanceof TileLogicBusSlave) {
				// Pass activate to tile
				return ((TileLogicBusSlave) tile).activate(world, pos, state, p, hand);
			}
		}
		return false;
	}
}
