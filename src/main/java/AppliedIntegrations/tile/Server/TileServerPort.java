package AppliedIntegrations.tile.Server;

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
            // Get core
            TileServerCore core = (TileServerCore)getMaster();

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


    private IGrid requestNetwork() {
        // Check not null
        if(gridNode == null)
            return null;

        // Get grid and return it
        return gridNode.getGrid();
    }

    @Override
    public void update() {
        super.update();

        // Check if port has master
        if(hasMaster()) {
            // Get core
            TileServerCore core = (TileServerCore) getMaster();

            // Get network at side of this port
            IGrid grid = core.getPortNetworks().get(side);

            // Check if grid is null
            if (grid == null) {
                // Update grid
                core.getPortNetworks().put(side, requestNetwork());
            }
        }
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
