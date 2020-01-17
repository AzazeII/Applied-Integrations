package AppliedIntegrations.tile.HoleStorageSystem;


import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class TimeHandler {
	private long lastTime = -1L;

	public boolean hasTimePassed(World w, int passedTime) {
		long worldTime = w.getTotalWorldTime();

		if (worldTime < lastTime) {
			updateData(w);
			return false;
		}

		if (lastTime + passedTime <= worldTime) {
			updateData(w);
			return true;
		}

		return false;
	}

	public void updateData(World w) {
		lastTime = w.getTotalWorldTime();
	}
}
