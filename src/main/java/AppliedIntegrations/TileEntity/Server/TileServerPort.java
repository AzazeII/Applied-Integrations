package AppliedIntegrations.TileEntity.Server;

import AppliedIntegrations.TileEntity.AIMultiBlockTile;
import appeng.api.networking.IGrid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;


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
            if(gridNode.getGrid() == null)
                return;
            IGrid Network = gridNode.getGrid();

            TileServerCore core = getMaster();

            if(Network.getNodes().size() > 1 && !core.portNetworks.containsValue(Network)) {
                if(Network != getMaster().MainNetwork) {
                    core.portNetworks.put(side, Network);
                    core.ServerNetworkMap.put(Network, getMaster().getNextNetID());
                }
            }else{
                core.portNetworks.remove(side);
                if(core.ServerNetworkMap.get(Network) != null)
                    getMaster().ServerNetworkMap.remove(getMaster().ServerNetworkMap.get(Network));
            }
        }
    }


    public void setDir(EnumFacing side) {
        this.side = side;
    }
}
