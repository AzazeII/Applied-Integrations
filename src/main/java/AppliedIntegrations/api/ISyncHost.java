package AppliedIntegrations.api;


import appeng.api.util.AEPartLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 * <p>
 * Any machine marked with this interface can be used as host for syncing between client and server
 */
public interface ISyncHost {
	// Compare this to any other sync host; True if objects equal
	default boolean compareTo(ISyncHost host, boolean ignoreWorld) {

		if (!ignoreWorld) {
			// Check if all three components of sync host are equal
			return host.getHostPos().equals(host.getHostPos()) && getHostWorld().equals(host.getHostWorld()) && getHostSide().equals(host.getHostSide());
		} else {
			// Check if two components of sync host are equal
			return host.getHostPos().equals(host.getHostPos()) && getHostSide().equals(host.getHostSide());
		}
	}

	// Pos of host
	@Nonnull
	BlockPos getHostPos();

	// World of host
	@Nonnull
	World getHostWorld();

	// Relative side to center of block
	@Nonnull
	AEPartLocation getHostSide(); // Use INTERNAL for tiles
}
