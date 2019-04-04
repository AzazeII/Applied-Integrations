package AppliedIntegrations.tile.Server;

import AppliedIntegrations.tile.AIMultiBlockTile;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

/**
 * @Author Azazell
 */
public class TileServerHousing extends AIMultiBlockTile {

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        if(hasMaster()) {
            EnumSet<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);

            for (EnumFacing side : EnumFacing.values()) {
                if (world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX(), getPos().getY() + side.getFrontOffsetY(), getPos().getZ() + side.getFrontOffsetZ())) instanceof TileServerCore ||
                        world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX(), getPos().getY() + side.getFrontOffsetY(), getPos().getZ() + side.getFrontOffsetZ())) instanceof TileServerRib ||
                        world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX(), getPos().getY() + side.getFrontOffsetY(), getPos().getZ() + side.getFrontOffsetZ())) instanceof TileServerHousing) {
                    set.add(side);
                }
            }

            return set;
        }
        return null;
    }
}
