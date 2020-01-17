package AppliedIntegrations.api;


import appeng.api.util.AEPartLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 * Any machine marked with this interface can be used as host for syncing between client and server
 */
public interface ISyncHost {
	default boolean compareTo(ISyncHost host, boolean ignoreWorld) {

		if (!ignoreWorld) {
			return host.getHostPos().equals(host.getHostPos()) && getHostWorld().equals(host.getHostWorld()) && getHostSide().equals(host.getHostSide());
		} else {
			return host.getHostPos().equals(host.getHostPos()) && getHostSide().equals(host.getHostSide());
		}
	}

	@Nonnull
	BlockPos getHostPos();

	@Nonnull
	World getHostWorld();

	@Nonnull
	AEPartLocation getHostSide();
}
