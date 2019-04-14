package AppliedIntegrations.tile.Server;

import AppliedIntegrations.tile.AIMultiBlockTile;
import appeng.api.networking.IGrid;
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
                if(Network != core.MainNetwork) {
                    core.portNetworks.put(side, Network);
                    core.ServerNetworkMap.put(Network, core.getNextNetID());
                }
            }else{
                core.portNetworks.remove(side);
                if(core.ServerNetworkMap.get(Network) != null)
                    core.ServerNetworkMap.remove(core.ServerNetworkMap.get(Network));
            }
        }
    }


    public void setDir(EnumFacing side) {
        this.side = side;
    }
}