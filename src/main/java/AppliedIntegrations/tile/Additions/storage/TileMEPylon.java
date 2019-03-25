package AppliedIntegrations.tile.Additions.storage;

import AppliedIntegrations.API.AIApi;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import AppliedIntegrations.tile.Additions.storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.Additions.storage.helpers.impl.BlackHoleEnergyHandler;
import AppliedIntegrations.tile.Additions.storage.helpers.impl.BlackHoleFluidHandler;
import AppliedIntegrations.tile.Additions.storage.helpers.impl.BlackHoleItemHandler;
import AppliedIntegrations.tile.Additions.storage.helpers.impl.BlackHoleManaHandler;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.util.AEPartLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.*;

import static java.util.Collections.singletonList;

public class TileMEPylon extends AITile implements ICellContainer {

    public TileBlackHole operatedTile;

    public boolean syncActive = false;

    // List of all handlers, operated by this tile
    public final LinkedHashMap<IStorageChannel, IMEInventoryHandler> handlers = AIApi.instance().getNewBlackHoleHandlerList();

    public boolean hasSingularity() {
        return operatedTile != null;
    }

    public TileBlackHole getSingularity() {
        return operatedTile;
    }


    public void postCellEvent(){
        // Get node
        IGridNode node = getGridNode(AEPartLocation.INTERNAL);
        // Check notNull
        if (node != null) {
            // Get grid
            IGrid grid = node.getGrid();
            // Check not null
            if(grid != null) {
                // Post update
                grid.postEvent(new MENetworkCellArrayUpdate());
            }
        }
    }

    @Override
    public void update() {
        super.update();

        if(getGridNode() == null)
            return;
        
        // Check if node was active
        if(!syncActive && getGridNode().isActive()){
            // Node wasn't active, but now it is active
            // Fire new cell array update event!
            postCellEvent();
            // Update sync
            syncActive = true;
        }else if(syncActive && !getGridNode().isActive()){
            // Node was active, but now it not
            // Fire new cell array update event!
            postCellEvent();
            // Update sync
            syncActive = false;
        }
    }

    @Override
    public void createAENode() {
        if(world != null) {
            if (!world.isRemote) {
                gridNode = AEApi.instance().grid().createGridNode(this);
                gridNode.updateState();

                // Fire new cell array update event!
                postCellEvent();
            }
        }
    }

    @Override
    public void blinkCell(int i) {}

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {
        // return empty list
        if(!hasSingularity() || !getGridNode().isActive())
            return new ArrayList<>();

        // Check for each channel, if not found correct -> pass to AIApi
        if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
            return singletonList(new BlackHoleItemHandler(getSingularity()));
        }else if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class)) {
            return singletonList(new BlackHoleFluidHandler(getSingularity()));
        }else if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class)) {
            return singletonList(new BlackHoleEnergyHandler(getSingularity()));
        }else if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class)) {
            return singletonList(new BlackHoleManaHandler(getSingularity()));
        }else {
            // Check not null
            if (handlers.get(iStorageChannel) != null)
                // return handler from map
                return singletonList(handlers.get(iStorageChannel));
        }
        return new ArrayList<>();
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.of(EnumFacing.DOWN);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
        // Check if inventory not null
        if (iCellInventory != null)
            // Persist inventory
            iCellInventory.persist();
        // Mark dirty
        world.markChunkDirty(pos, this);
    }

    public boolean activate(EnumHand hand) {
        if(hand == EnumHand.MAIN_HAND) {
            if (!world.isRemote) {
                //AILog.chatLog("Has grid: " + (getGridNode().getGrid() != null) + " Active: " + getGridNode().isActive() + " Has singularity: " + hasSingularity());
                for(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClass : AIApi.instance().getBlackHoleHandlerClasses()){
                    AILog.chatLog(handlerClass.toString());
                }
            }
        }
        return true;
    }
}
