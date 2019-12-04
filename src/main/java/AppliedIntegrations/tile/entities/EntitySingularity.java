package AppliedIntegrations.tile.entities;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import appeng.util.Platform;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import static java.lang.String.format;

/**
 * @Author Azazell
 */
public class EntitySingularity extends EntitySnowball {
	private final double startX;
	private final double startY;
	private final double startZ;
	private final BlocksEnum singularityBlock;
	private EntitySingularity linkedSingularity;
	private ISingularity bornTile;

	public EntitySingularity(World world, double x, double y, double z, BlocksEnum singularityBlock) {
		super(world, x, y, z);
		this.startX = x;
		this.startY = y;
		this.startZ = z;

		// Don't allow to define singularities other than inherited from ISingularity
		if (!ISingularity.class.isAssignableFrom(singularityBlock.tileEnum.clazz)) {
			throw new IllegalStateException(format("Can't use %s as ISingularity", singularityBlock.tileEnum.clazz.getName()));
		}

		this.singularityBlock = singularityBlock;
	}

	public EntitySingularity(World hostWorld, BlockPos hostPos, BlocksEnum singularityType) {
		this(hostWorld, hostPos.getX(), hostPos.getY(), hostPos.getZ(), singularityType);
	}

	public void setLinked(EntitySingularity singularity) {
		this.linkedSingularity = singularity;
	}

	public ISingularity getBornSingularity() {
		return bornTile;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult map) {
		if (this.startX == this.posX && this.startY == this.posY && this.startZ == this.posZ) {
			return;
		}

		// Setting block on impact
		world.setBlockState(map.getBlockPos(), singularityBlock.b.getDefaultState());
		if (Platform.isServer()) {
			setDead();
		}

		// Get tile on position
		TileEntity tile = world.getTileEntity(map.getBlockPos());
		if (tile instanceof ISingularity) {
			// Passing linked singularity to tile
			this.bornTile = (ISingularity) tile;
			((ISingularity)tile).setEntangledHoleEntity(linkedSingularity);
		}
	}
}
