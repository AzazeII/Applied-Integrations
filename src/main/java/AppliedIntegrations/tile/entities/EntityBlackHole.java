package AppliedIntegrations.tile.entities;

import AppliedIntegrations.Blocks.BlocksEnum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class EntityBlackHole extends EntitySnowball {

	public EntityBlackHole(World world) {
		super(world);
	}

	public EntityBlackHole(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	public EntityBlackHole(World world, BlockPos pos) {
		super(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public EntityBlackHole(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult map) {
		// Add black hole block
		world.setBlockState(map.getBlockPos(), BlocksEnum.BlackHole.b.getDefaultState());

		if (!world.isRemote) {
			setDead();
		}
	}
}
