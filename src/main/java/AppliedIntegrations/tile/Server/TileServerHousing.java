package AppliedIntegrations.tile.Server;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

/**
 * @Author Azazell
 */
public class TileServerHousing extends AIServerMultiBlockTile {
	@Override
	public EnumSet<EnumFacing> getValidSides() {
		if (hasMaster()) {
			EnumSet<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);

			for (EnumFacing side : EnumFacing.values()) {
				TileEntity tile = world.getTileEntity(new BlockPos(
						getPos().getX() + side.getFrontOffsetX(),
						getPos().getY() + side.getFrontOffsetY(),
						getPos().getZ() + side.getFrontOffsetZ()));

				if (tile instanceof TileServerCore ||
					tile instanceof TileServerRib ||
					tile instanceof TileServerHousing) {
					set.add(side);
				}
			}

			return set;
		}

		return EnumSet.noneOf(EnumFacing.class);
	}
}
