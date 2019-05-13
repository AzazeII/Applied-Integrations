package AppliedIntegrations.tile.Server;

import AppliedIntegrations.tile.AIServerMultiBlockTile;
import appeng.api.networking.IGrid;
import appeng.api.storage.*;
import appeng.api.util.AEPartLocation;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author Azazell
 */
public class TileServerPort extends AIServerMultiBlockTile implements ICellContainer {

    private AEPartLocation side = AEPartLocation.INTERNAL;

    public void onNeighborChange() {
        // Check if port has master
        if(hasMaster()){
            // Check not null
            if(gridNode == null)
                return;

            // Get grid
            IGrid network = gridNode.getGrid();

            // Get core
            TileServerCore core = (TileServerCore)getMaster();

            // Check if network has more than one node and core networks not contains this network
            if(network.getNodes().size() > 1 && !core.portNetworks.containsValue(network)) {
                // Check if network not equal to main network
                if(network != core.mainNetwork) {
                    // Map network by side
                    core.portNetworks.put(side, network);

                    // Map id by network
                    core.networkIDMap.put(network, core.getNextNetID());
                }
            }else{
                // Remove network from maps
                core.portNetworks.remove(side); // (1)
                core.networkIDMap.remove(network); // (2)/[
            }

            // Notify all networks
            core.postNetworkCellEvents();
        }
    }


    public void setDir(EnumFacing side) {
        this.side = AEPartLocation.fromFacing(side);
    }

    public AEPartLocation getSideVector() {
        return side;
    }

    @Override
    public void validate(){
        this.onNeighborChange();
    }

    /* -----------------------------Drive Methods----------------------------- */
    @Override
    public void blinkCell(int slot) {
       // Ignored
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
        // Check not null
        if (getMaster() == null)
            // Empty list
            return new ArrayList<>();

        // Pass call to master
        return ((TileServerCore)getMaster()).getSidedCellArray(side, channel);
    }

    @Override
    public int getPriority() {
        // Ignored
        return 0;
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> cellInventory) {
        ((TileServerCore)getMaster()).saveSidedChanges(cellInventory, side);
    }
    /* -----------------------------Drive Methods----------------------------- */
}
