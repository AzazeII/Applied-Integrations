package AppliedIntegrations.Utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public final class AIUtils {

	private static final double SQUARED_REACH = 64.0D;

	/**
	 * Returns true if the tile still exists and the player is within reach range.
	 *
	 * @param player
	 * @param tile
	 * @return
	 */
	public static final boolean canPlayerInteractWith(@Nonnull final EntityPlayer player, @Nonnull final TileEntity tile) {
		TileEntity tileAtCoords = tile.getWorld().getTileEntity(tile.getPos());

		// Null check
		if (tileAtCoords == null) {
			return false;
		}

		// Range check
		return (player.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= AIUtils.SQUARED_REACH);

	}
}
