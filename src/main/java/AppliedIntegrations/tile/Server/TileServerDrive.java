package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.api.IInventoryHost;
import appeng.api.AEApi;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TileServerDrive extends TileServerHousing implements ICellContainer {
    private class DriveInventoryManager implements IInventoryHost {
        @Override
        public void onInventoryChanged() {
            // Refresh drive states
            nullifyMap();
        }
    }

    private LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IMEInventoryHandler>> driveHandlers = new LinkedHashMap<>();
    private DriveInventoryManager driveManager = new DriveInventoryManager();

    {
        // Refresh drive states
        nullifyMap();
    }

    public AIGridNodeInventory driveInv = new AIGridNodeInventory("ME Server",6,1, this.driveManager){
        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return AEApi.instance().registries().cell().isCellHandled(itemstack);
        }
    };

    public void nullifyMap () {
        // Iterate for each channel
        for (IStorageChannel<? extends IAEStack<?>> chan : AEApi.instance().storage().storageChannels()) {
            // Put empty list in handler map
            driveHandlers.put(chan, new ArrayList<>());
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        // Drop slots from drive inventory
        Platform.spawnDrops(world, pos, Arrays.asList(driveInv.slots)); // Drive inv
    }

    // -----------------------------Drive Methods-----------------------------//
    @Override
    public void blinkCell(int slot) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel channel) {
        // Check if node is active
        if (!gridNode.isActive())
            return new ArrayList<>();

        // Return drive list from map
        return this.driveHandlers.get(channel);
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
        // Check if inventory not null
        if (iCellInventory != null)
            // Persist inventory
            iCellInventory.persist();

        // Mark dirty
        getWorld().markChunkDirty(this.getPos(), this);
    }
    // -----------------------------Drive Methods-----------------------------//


    @Override
    public void readFromNBT(NBTTagCompound tag) {
        // Read inventory tag
        driveInv.readFromNBT(tag.getTagList("#driveInv", 10)); // Drive inventory

        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        // Write inventory tag
        tag.setTag("#driveInv", driveInv.writeToNBT()); // Drive inventory

        return super.writeToNBT(tag);
    }
}
