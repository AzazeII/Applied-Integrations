package AppliedIntegrations.api;

import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.util.AEPartLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @Author Azazell
 */
public interface IEnergyInterface extends IEnergyInterfaceDuality, INetworkManipulator, ISyncHost {
    void initEnergyStorage(LiquidAIEnergy energy, AEPartLocation side);

    IGridNode getGridNode();

    TileEntity getFacingTile(EnumFacing side);

    /**
     * @return Outer grid inventory of this host. Used by AppliedIntegrations.Inventory.Handlers#HandlerEnergyStorageBusInterface
     */
    default IMEInventory<IAEEnergyStack> getOuterGridInventory() {
        // Check not null
        if (getGridNode() == null)
            return null;

        // Create grid
        IGrid grid = getGridNode().getGrid(); // check grid node

        // Create storage grid
        IStorageGrid storage = grid.getCache(IStorageGrid.class);

        return storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));
    }

    LiquidAIEnergy getFilter(int index);
}
