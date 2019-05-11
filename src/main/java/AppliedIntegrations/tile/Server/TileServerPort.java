package AppliedIntegrations.tile.Server;

import AppliedIntegrations.tile.AIMultiBlockTile;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;


/**
 * @Author Azazell
 */
public class TileServerPort extends AIMultiBlockTile implements ICellContainer {

    private AEPartLocation side = null;

    public void updateGrid() {
        if(hasMaster()){
            if(gridNode == null)
                return;
            gridNode.getGrid();
            IGrid Network = gridNode.getGrid();

            TileServerCore core = (TileServerCore)getMaster();

            if(Network.getNodes().size() > 1 && !core.portNetworks.containsValue(Network)) {
                if(Network != core.mainNetwork) {
                    core.portNetworks.put(side.getFacing(), Network);
                    core.networkIDMap.put(Network, core.getNextNetID());
                }
            }else{
                core.portNetworks.remove(side.getFacing());
                if(core.networkIDMap.get(Network) != null)
                    core.networkIDMap.remove(core.networkIDMap.get(Network));
            }
        }
    }


    public void setDir(EnumFacing side) {
        this.side = AEPartLocation.fromFacing(side);
    }

    public AEPartLocation getSideVector() {
        return side;
    }

    public IGrid getOuterGrid() {
        // Check not null
        if (getGridNode() == null)
            return null;

        // Get grid
        return getGridNode().getGrid();
    }

    public IMEInventory<?> getOuterInventory(IStorageChannel<? extends IAEStack<?>> channel) {
        // Check not null
        if (getOuterGrid() == null)
            return null;

        // Get storage grid
        IStorageGrid storageGrid = getOuterGrid().getCache(IStorageGrid.class);

        // Return inventory of grid
        return storageGrid.getInventory(channel);
    }

    @Override
    public void validate(){
        this.updateGrid();
    }

    /* -----------------------------Drive Methods----------------------------- */
    @Override
    public void blinkCell(int slot) {
       // Ignored
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
        return ((TileServerCore)getMaster()).getSidedCellArray(side);
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
