package AppliedIntegrations.API;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEPartLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @Author Azazell
 */
public interface IEnergyInterface extends IInterfaceDuality, INetworkManipulator{
    IGridNode getGridNode();

    TileEntity getFacingTile(EnumFacing side);

    /**
     * @return Outer grid inventory of this host. Used by AppliedIntegrations.Inventory.Handlers#HandlerEnergyStorageBusInterface
     */
    default IMEInventory<IAEEnergyStack> getOuterGridInventory() {
        // Check not null
        if(getGridNode() == null)
            return null;

        // Create grid
        IGrid grid = getGridNode().getGrid(); // check grid node

        // Check not null
        if (grid == null) {
            AILog.info("Grid cannot be initialized");
            return null;
        }

        // Create storage grid
        IStorageGrid storage = grid.getCache(IStorageGrid.class);

        return storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));
    }

    // Work mode
    enum DualityMode{
        Inject,
        Extract;
    }
    // Packet work mode
    enum FlowMode{
        Gui, // send data from part to gui
        Machine; // send data from gui to machine
    }
         LiquidAIEnergy getFilter(int index);
};
