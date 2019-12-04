package AppliedIntegrations.Utils;
import appeng.api.util.AEPartLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * @Author Azazell
 * Utils for vIctor calculations
 */
public class VectorUtils {
	public static AEPartLocation getVectorFacing(Vec3d relativeVec) {
		// Gets enum facing of vecB relative to vecA (already calculated as relativeVec)
		double x = relativeVec.x;
		double y = relativeVec.y;
		double z = relativeVec.z;

		// Get greatest component and check if it's greater than zero (positive facing) of less than zero (negative facing)
		if (x < 0 || y < 0 || z < 0) {
			// Compare by smallest
			double smallest = Math.min(x, Math.min(y, z));

			if (smallest == x) {
				// Negative x
				return AEPartLocation.WEST;
			} else if (smallest == z) {
				// Negative z
				return AEPartLocation.NORTH;
			} else if (smallest == y) {
				// Negative y
				return AEPartLocation.DOWN;
			}
		} else if (x > 0 || y > 0 || z > 0) {
			// Compare by greatest
			double greatest = Math.max(x, Math.max(y, z));

			if (greatest == x) {
				// Positive x
				return AEPartLocation.EAST;
			} else if (greatest == z) {
				// Positive z
				return AEPartLocation.SOUTH;
			} else if (greatest == y) {
				// Positive y
				return AEPartLocation.UP;
			}
		}

		return AEPartLocation.INTERNAL;
	}

	public static Vec3d getFractionalVector(Vec3i pos) {
		return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vec3i toIntegerVector(Vec3d vec) {
		return new Vec3i(vec.x, vec.y, vec.z);
	}

	public static Vec3d getUnitVector(Vec3d vec) {
		// Calculate magnitude from square root of squared vector components
		final double x = vec.x;
		final double y = vec.y;
		final double z = vec.z;
		double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

		return new Vec3d(x / magnitude, y / magnitude, z / magnitude);
	}
}
