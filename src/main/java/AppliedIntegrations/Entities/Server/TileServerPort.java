package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Entities.AITile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.Iterator;


public class TileServerPort  extends AIMultiBlockTile {

    private ForgeDirection side = ForgeDirection.UNKNOWN;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

    }

    @Override
    public void validate(){
        this.updateGrid();
    }
    public void updateGrid() {
        if(hasMaster()){
            if(theGridNode == null)
                return;
            if(theGridNode.getGrid() == null)
                return;
            IGrid Network = theGridNode.getGrid();

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


    public void setDir(ForgeDirection side) {
        this.side = side;
    }
}
