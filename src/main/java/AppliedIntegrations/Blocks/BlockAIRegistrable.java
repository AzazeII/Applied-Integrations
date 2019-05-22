package AppliedIntegrations.Blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
public abstract class BlockAIRegistrable extends BlockContainer {

	public BlockAIRegistrable(String registryName, String unlocalizedName) {
		super(Material.IRON, null);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(registryName);
		this.setHardness(5F);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {

		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {

		return BlockRenderLayer.SOLID;
	}
}
