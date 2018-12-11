package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class TileServerHousing extends AIMultiBlockTile {

    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        if(hasMaster()) {
            EnumSet<ForgeDirection> set = EnumSet.noneOf(ForgeDirection.class);

            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                if (worldObj.getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ) instanceof TileServerCore ||
                        worldObj.getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ) instanceof TileServerRib ||
                        worldObj.getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ) instanceof TileServerHousing) {
                    set.add(side);
                }
            }

            return set;
        }
        return null;
    }
}
