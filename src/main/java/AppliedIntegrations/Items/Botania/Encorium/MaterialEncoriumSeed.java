package AppliedIntegrations.Items.Botania.Encorium;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemEnum;
import appeng.api.implementations.items.IGrowableCrystal;
import appeng.core.localization.ButtonToolTips;
import appeng.entity.EntityGrowingCrystal;
import appeng.util.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @Author Azazell
 * @Design MegaTech
 */
public class MaterialEncoriumSeed extends AIItemRegistrable implements IBotaniaIntegrated, IGrowableCrystal {
	private static final int LEVEL_STEP = 200;
	private static final int SINGLE_STEP = LEVEL_STEP * 3;

	public MaterialEncoriumSeed(String registry) {
		super(registry);
		this.addPropertyOverride(new ResourceLocation("growth"), (stack, worldIn, entityIn) -> {
			double progress = getProgressPercent(stack);

			return (float) (progress / 100);
		});
	}

	private int getProgress( final ItemStack is ) {
		if( is.hasTagCompound() ) {
			return is.getTagCompound().getInteger( "progress" );
		} else {
			int progress = is.getItemDamage();

			NBTTagCompound tag = Platform.openNbtData( is );
			tag.setInteger( "progress", progress );
			is.setItemDamage( ( is.getItemDamage() / SINGLE_STEP ) * SINGLE_STEP );

			return progress;
		}
	}

	private void setProgress( final ItemStack is, final int newDamage ) {
		final NBTTagCompound tag = Platform.openNbtData( is );
		tag.setInteger( "progress", newDamage );
		is.setItemDamage( is.getItemDamage() / SINGLE_STEP * SINGLE_STEP );
	}

	private double getProgressPercent(ItemStack stack) {
		return Math.floor((float) (getProgress( stack ) % SINGLE_STEP) / (float) ( SINGLE_STEP / 100 ));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
		lines.add( ButtonToolTips.DoesntDespawn.getLocal() );
		lines.add( getProgressPercent(stack) + "%" ); // Growth percentage
		lines.add("");
		lines.add(TextFormatting.GOLD + "Design by MegaTech");
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isDamaged( final ItemStack stack ) {
		return false;
	}

	@Override
	public int getMaxDamage( final ItemStack stack ) {
		return SINGLE_STEP * 2;
	}

	@Override
	public ItemStack triggerGrowth(ItemStack is) {
		final int newDamage = getProgress(is) + 1;

		if (newDamage == SINGLE_STEP) {
			return new ItemStack(ItemEnum.ITEMENCORIUM.getItem());
		}

		if (newDamage > SINGLE_STEP * 2) {
			return ItemStack.EMPTY;
		}

		this.setProgress(is, newDamage);
		return is;
	}

	@Override
	public boolean hasCustomEntity( final ItemStack stack ) {
		return true;
	}

	@Override
	public Entity createEntity(final World world, final Entity location, final ItemStack itemstack ) {
		final EntityGrowingCrystal egc = new EntityGrowingCrystal( world, location.posX, location.posY, location.posZ, itemstack );

		egc.motionX = location.motionX;
		egc.motionY = location.motionY;
		egc.motionZ = location.motionZ;
		egc.setPickupDelay( 40 );

		return egc;
	}

	@Override
	public float getMultiplier(Block blk, Material mat) {
		return 0.5f;
	}
}
