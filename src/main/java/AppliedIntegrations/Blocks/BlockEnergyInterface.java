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
	private boolean isThirdClick = false;

	public BlockEnergyInterface() {
		super(Material.ROCK);
		this.setUnlocalizedName("ME Energy Interface");
		this.setRegistryName("EInterface");
		this.setCreativeTab(AppliedIntegrations.AI);
		this.setHardness(5F);
	}
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



