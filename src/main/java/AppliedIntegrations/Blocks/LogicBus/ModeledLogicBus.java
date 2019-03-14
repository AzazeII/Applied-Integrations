package AppliedIntegrations.Blocks.LogicBus;

import AppliedIntegrations.Blocks.AIMultiBlock;
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

import static appeng.block.qnb.BlockQuantumBase.FORMED_STATE;

public abstract class ModeledLogicBus extends AIMultiBlock {
    public static final PropertyBool valid = PropertyBool.create( "valid" );
    public static final LogicBusStateProperty stateProp = new LogicBusStateProperty();

    protected ModeledLogicBus(String registry, String unlocalizedName) {
        super(registry, unlocalizedName);
        this.setDefaultState( this.getDefaultState().withProperty( valid, false ) );
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        final float shave = 2.0f / 16.0f;
        return new AxisAlignedBB( shave, shave, shave, 1.0f - shave, 1.0f - shave, 1.0f - shave );
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState( this, new IProperty[] {valid}, new IUnlistedProperty[] { stateProp } );
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos )
    {
        IExtendedBlockState extState = (IExtendedBlockState) state;

        LogicBusState formedState = new LogicBusState( true, false );
        extState = extState.withProperty( stateProp, formedState );

        return extState;
    }

    @Override
    public IBlockState getActualState( IBlockState state, IBlockAccess worldIn, BlockPos pos )
    {
        state = state.withProperty( valid, false );
        return state;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {

        return this.getDefaultState().withProperty(valid, meta == 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {

        return state.getValue(valid).booleanValue()? 0 : 1;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos
            , EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(valid, false);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileLogicBusSlave){
            return ((TileLogicBusSlave)tile).activate(world, pos, state, p, hand);
        }
        return false;
    }
}
