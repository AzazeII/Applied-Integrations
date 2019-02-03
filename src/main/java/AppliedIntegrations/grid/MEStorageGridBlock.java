package AppliedIntegrations.grid;

import AppliedIntegrations.Entities.Server.TileServerCore;
import appeng.api.networking.*;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class MEStorageGridBlock implements IGridBlock {

    protected IGrid grid;
    protected int usedChannels = 0;
    private TileServerCore host;

    @Override
    public double getIdlePowerUsage() {
        return host.getIdlePowerUsage();
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL, GridFlags.DENSE_CAPACITY);
    }

    @Override
    public boolean isWorldAccessible() {
        return true;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(host.getLocation());
    }

    @Override
    public AEColor getGridColor() {
        return  AEColor.TRANSPARENT;
    }

    @Override
    public void onGridNotification(GridNotification notification) {

    }

    @Override
    public void setNetworkStatus(IGrid grid, int channelsInUse) {
        this.grid = grid;
        this.usedChannels = channelsInUse;
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.allOf(EnumFacing.class);
    }

    @Override
    public IGridHost getMachine() {
        return host;
    }

    @Override
    public void gridChanged() {

    }

    @Override
    public ItemStack getMachineRepresentation() {
        DimensionalCoord location = this.getLocation();
        if (location == null)
            return null;
        return new ItemStack(location.getWorld().getBlock(location.x, location.y, location.z), 1, location.getWorld().getBlockMetadata(location.x, location.y, location.z));
    }
}
