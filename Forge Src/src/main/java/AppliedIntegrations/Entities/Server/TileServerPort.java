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


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

    }

    public void updateGrid() {
        if(hasMaster()){
            if(theGridNode == null)
                return;
            if(theGridNode.getGrid() == null)
                return;
            IGrid Network = theGridNode.getGrid();
            if(Network.getNodes().size() > 1 && !getMaster().portNetworks.contains(Network)) {
                getMaster().portNetworks.add(Network);
            }else{
                getMaster().portNetworks.remove(Network);
            }
        }
    }
}
