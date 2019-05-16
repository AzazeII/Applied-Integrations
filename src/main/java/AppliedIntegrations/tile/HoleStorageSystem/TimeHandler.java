package AppliedIntegrations.tile.HoleStorageSystem;

import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class TimeHandler {

	// Last updated time, used to compare time with given in #hasTimePassed(World, int)
	private long lastTime = -1L;

	public boolean hasTimePassed(World w, int passedTime) {
		// Get world time
		long worldTime = w.getTotalWorldTime();

		// Check if current world time less than last marked
		if (worldTime < lastTime) {
			// update time
			updateData(w);
			// Return false
			return false;
		}

		// Check if last time + passed time <= current time
		if (lastTime + passedTime <= worldTime) {
			// Update mark
			updateData(w);
			return true;
		}

		// Just return false
		return false;
	}

	public void updateData(World w) {
		lastTime = w.getTotalWorldTime();
	}
}
