package AppliedIntegrations.tile.entities;
import AppliedIntegrations.Blocks.BlocksEnum;
import appeng.util.Platform;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class EntityBlackHole extends EntitySnowball {
	private final double startX;
	private final double startY;
	private final double startZ;

	public EntityBlackHole(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.startX = x;
		this.startY = y;
		this.startZ = z;
	}

	public EntityBlackHole(World hostWorld, BlockPos hostPos) {
		this(hostWorld, hostPos.getX(), hostPos.getY(), hostPos.getZ());
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult map) {
		if (this.startX == this.posX && this.startY == this.posY && this.startZ == this.posZ) {
			return;
		}

		// Add black hole block
		world.setBlockState(map.getBlockPos(), BlocksEnum.BlackHole.b.getDefaultState());

		if (Platform.isServer()) {
			setDead();
		}
	}
}
