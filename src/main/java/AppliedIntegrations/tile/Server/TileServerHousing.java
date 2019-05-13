package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.api.IInventoryHost;
import appeng.api.AEApi;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author Azazell
 */
public class TileServerHousing extends AIServerMultiBlockTile {
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

        return EnumSet.noneOf(EnumFacing.class);
    }
}
