package AppliedIntegrations.tile.Server;

import AppliedIntegrations.tile.AIMultiBlockTile;
import appeng.api.networking.IGrid;
import appeng.api.util.AEPartLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;


/**
 * @Author Azazell
 */
public class TileServerPort  extends AIMultiBlockTile {

    private EnumFacing side = null;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return super.writeToNBT(nbt);
    }

    @Override
    public void validate(){
        this.updateGrid();
    }

    public void updateGrid() {
        if(hasMaster()){
            if(gridNode == null)
                return;
            gridNode.getGrid();
            IGrid Network = gridNode.getGrid();

            TileServerCore core = (TileServerCore)getMaster();

            if(Network.getNodes().size() > 1 && !core.portNetworks.containsValue(Network)) {
                if(Network != core.mainNetwork) {
                    core.portNetworks.put(side, Network);
                    core.networkIDMap.put(Network, core.getNextNetID());
                }
            }else{
                core.portNetworks.remove(side);
                if(core.networkIDMap.get(Network) != null)
                    core.networkIDMap.remove(core.networkIDMap.get(Network));
            }
        }
    }


    public void setDir(EnumFacing side) {
        this.side = side;
    }

    // Returns outer grid. Grid is taken from side vector from core to this port, like this:
    // Core ----Side_Vec.----> Port ----> Grid
    public IGrid getOuterGrid() {
        // Check not null
        if(getGridNode() == null)
            // Avoid null pointer exception
            return null;
        return this.getNetwork();
    }

    public AEPartLocation getSideVector() {
        return AEPartLocation.fromFacing(side);
    }
}
