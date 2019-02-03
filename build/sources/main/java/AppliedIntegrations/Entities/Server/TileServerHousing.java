package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.util.AEPartLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

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
