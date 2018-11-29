package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.Blocks.MEServer.BlockServerRib;
import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import net.minecraft.block.Block;

import java.util.EnumSet;
import java.util.List;

public class TileServerRib extends AIMultiBlockTile implements IAIMultiBlock, ICellContainer, IInventoryHost {

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.DENSE_CAPACITY);
    }

    public void changeAlt(Boolean alt){
        Block rib = worldObj.getBlock(xCoord,yCoord,zCoord);
        if(rib != null && rib.getClass() == BlockServerRib.class) {
            ((BlockServerRib)rib).isAlt = alt;
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            worldObj.notifyBlockChange(xCoord,yCoord,zCoord,rib);
        }
    }

    @Override
    public void onInventoryChanged() {
        if(hasMaster())
            getMaster().onInventoryChanged();
    }


    @Override
    public void blinkCell(int slot) {
        if(hasMaster())
            getMaster().blinkCell(slot);
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
        if(hasMaster())
            return getMaster().getCellArray(channel);
        return null;
    }

    @Override
    public int getPriority() {
        if(hasMaster())
            return getMaster().getPriority();
        return 0;
    }

    @Override
    public void saveChanges(IMEInventory cellInventory) {
        if(hasMaster())
            getMaster().saveChanges(cellInventory);
    }


    public void notifyConflict() {
       int meta = worldObj.getBlockMetadata(xCoord,yCoord,zCoord);

       Block b = worldObj.getBlock(xCoord,yCoord,zCoord);
       if(b instanceof BlockServerRib){
           if(meta == 0)
               worldObj.setBlockMetadataWithNotify(xCoord,yCoord,zCoord, 40, 0);
           else
               worldObj.setBlockMetadataWithNotify(xCoord,yCoord,zCoord,meta*10,0);
       }
    }

    public void resetConflict() {
        int meta = worldObj.getBlockMetadata(xCoord,yCoord,zCoord);

        Block b = worldObj.getBlock(xCoord,yCoord,zCoord);
        if(b instanceof BlockServerRib){
            if(meta == 40)
                worldObj.setBlockMetadataWithNotify(xCoord,yCoord,zCoord, 0, 0);
            else
                worldObj.setBlockMetadataWithNotify(xCoord,yCoord,zCoord,meta/10,0);
        }
    }
}
