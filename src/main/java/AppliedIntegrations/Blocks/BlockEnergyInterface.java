package AppliedIntegrations.Blocks;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Entities.TileEnergyInterface;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @Author Azazell
 */
public class BlockEnergyInterface extends BlockContainer implements ITileEntityProvider {
	private static IIcon BasicIcon;
	private static IIcon altIcon;
	private IIcon altArrowIconDown;
	private IIcon altArrowIconUp;
	private IIcon altArrowIconRight;
	private IIcon altArrowIconLeft;

	private TileEnergyInterface associatedInterface;

	private ForgeDirection Forward = ForgeDirection.UNKNOWN;
	private boolean isThirdClick = false;

	public BlockEnergyInterface() {
		super(Material.rock);
		this.setBlockName("ME Energy Interface");
		this.setCreativeTab(AppliedIntegrations.AI);
		this.setHardness(5F);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		int valuedMeta = meta-1;
		int sideOposite = ForgeDirection.getOrientation(side).getOpposite().ordinal();
		if(meta == 0){
			return this.BasicIcon;
		}else{
			if(side == valuedMeta || sideOposite == valuedMeta){
				return this.altIcon;
			}else{
				if (valuedMeta == 0){
					return this.altArrowIconDown;
				}else if(valuedMeta == 1){
					return this.altArrowIconUp;
				}else if(valuedMeta == 2){
					if (side == 1){
						return this.altArrowIconUp;
					}else if(side == 0){
						return this.altArrowIconUp;
					}else if(side == 4){
						return this.altArrowIconLeft;
					}else if(side == 5){
						return this.altArrowIconRight;
					}
				}else if(valuedMeta == 3){
					if (side == 1){
						return this.altArrowIconDown;
					}else if(side == 0){
						return this.altArrowIconDown;
					}else if(side == 4){
						return this.altArrowIconRight;
					}else if(side == 5){
						return this.altArrowIconLeft;
					}
				}else if(valuedMeta == 4){
					if(side == 0){
						return this.altArrowIconLeft;
					}else if(side == 1){
						return this.altArrowIconLeft;
					}else if(side == 2){
						return this.altArrowIconRight;
					}else if(side == 3){
						return this.altArrowIconLeft;
					}
				}else if(valuedMeta == 5){
					if(side == 0){
						return this.altArrowIconRight;
					}else if(side == 1){
						return this.altArrowIconRight;
					}else if(side == 2){
						return this.altArrowIconLeft;
					}else if(side == 3){
						return this.altArrowIconRight;
					}
				}

			}
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister IconRegistry) {
		BasicIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":energy.Interface");
		altIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":energy.Interface.Alt");
		altArrowIconDown = IconRegistry.registerIcon(AppliedIntegrations.modid+":energy.Interface.Alt.Arrow.Down");
		altArrowIconUp = IconRegistry.registerIcon(AppliedIntegrations.modid+":energy.Interface.Alt.Arrow.Up");
		altArrowIconRight = IconRegistry.registerIcon(AppliedIntegrations.modid+":energy.Interface.Alt.Arrow.Right");
		altArrowIconLeft = IconRegistry.registerIcon(AppliedIntegrations.modid+":energy.Interface.Alt.Arrow.Left");
	}
	/*
	   @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon("furnace_side");
        this.field_149936_O = p_149651_1_.registerIcon(this.field_149932_b ? "furnace_front_on" : "furnace_front_off");
        this.field_149935_N = p_149651_1_.registerIcon("furnace_top");
    }
	 */

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {
		if (stack.stackTagCompound != null) {
			TileEnergyInterface tile = (TileEnergyInterface) world.getTileEntity(x, y, z);
			this.associatedInterface = tile;
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEnergyInterface();
	}
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float par7, float par8, float par9) {
				Block block = world.getBlock(x, y, z);
				TileEntity entity = world.getTileEntity(x, y, z);
				if (Platform.isWrench(p,p.inventory.getCurrentItem(),x,y,z)) {
					if(p.isSneaking()){
						final List<ItemStack> list = Lists.newArrayList( Platform.getBlockDrops(world,x,y,z) );
						Platform.spawnDrops( world, x, y, z,list);
						world.setBlockToAir( x, y, z );
						return false;
					}
					if(!isThirdClick) {
						if (ForgeDirection.getOrientation(side).getOpposite().ordinal() != world.getBlockMetadata(x, y, z) - 1) {
							world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(side).getOpposite().ordinal() + 1, 0);
						} else {
							world.setBlockMetadataWithNotify(x, y, z, side + 1, 0);
							this.isThirdClick = true;
						}
					}else{
						world.setBlockMetadataWithNotify(x, y, z, 0, 0);
						isThirdClick = false;
					}
					if(world.getTileEntity(x,y,z) instanceof TileEnergyInterface) {
						((TileEnergyInterface)world.getTileEntity(x, y, z)).setWorkMode(ForgeDirection.getOrientation(side).getOpposite());
					}
					return true;
				}else if (block != null && entity instanceof TileEntity && p != null) {
					// if not sneaking open gui
					if (!p.isSneaking()) {
						p.openGui(AppliedIntegrations.instance, 2, world, x, y, z);
						return true;
					}
				}

					return false;
				}
	}



