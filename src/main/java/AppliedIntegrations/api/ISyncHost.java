package AppliedIntegrations.api;

import appeng.api.util.AEPartLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 * <p>
 * Any machine marked with this interface can be used as host for widget energy slot
 */
public interface ISyncHost {
	// Compare this to any other sync host; True if objects equal
	default boolean compareTo(ISyncHost host, boolean ignoreWorld) {
		if (!ignoreWorld) {
			// Check if all three components of sync host are equal
			return host.getPos().equals(host.getPos()) && getWorld().equals(host.getWorld()) && getSide().equals(host.getSide());
		} else {
			// Check if two components of sync host are equal
			return host.getPos().equals(host.getPos()) && getSide().equals(host.getSide());
		}
	}

	// Pos of host
	BlockPos getPos();

	// World of host
	World getWorld();

	// Relative side to center of block
	AEPartLocation getSide();
}
