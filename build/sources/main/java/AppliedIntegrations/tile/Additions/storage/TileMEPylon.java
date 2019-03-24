package AppliedIntegrations.tile.Additions.storage;

import AppliedIntegrations.API.AIApi;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.grid.EnergyList;
import AppliedIntegrations.tile.AITile;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEPartLocation;
import appeng.fluids.util.FluidList;
import appeng.me.cache.GridStorageCache;
import appeng.me.helpers.MachineSource;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static appeng.api.config.Actionable.MODULATE;
import static net.minecraft.util.EnumFacing.*;

public class TileMEPylon extends AITile implements ICellContainer{

    public TileSingularity operatedTile;
    private boolean wasActive = false;

    // List of all ae items stored in this singularity
    private ItemList storedItems = new ItemList();

    // List of all ae fluids stored in this singularity
    private FluidList storedFluids = new FluidList();

    // List of all ae energies stored in this singularity
    private EnergyList storedEnergies = new EnergyList();

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.of(DOWN);
    }

    public boolean hasSingularity() {
        return operatedTile != null;
    }

    public TileSingularity getSingularity() {
        return operatedTile;
    }


    public void updateNodeData(){
        // Get node
        IGridNode node = getGridNode(AEPartLocation.INTERNAL);
        // Check notNull
        if (node != null) {
            // Get grid
            IGrid grid = node.getGrid();
            // Post update
            grid.postEvent(new MENetworkCellArrayUpdate());
            // Get storage grid
            IStorageGrid cache = grid.getCache(IStorageGrid.class);
            // Add node
            cache.addNode(node, this);
        }
    }

    @Override
    public void blinkCell(int i) {}


    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {
        AILog.info("getCellArray called");
        // return empty list
        if(!node.isActive() || !hasSingularity())
            return new ArrayList<>();

        // List of handlers
        List<IMEInventoryHandler> handlers = new ArrayList<>();

        // Check if storage channel present item storage channel
        if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
            handlers.add(new SingularityInventoryHandler<IAEItemStack>() {
                // Operated tile
                private TileSingularity singularityOperated = getSingularity();

                @Override
                public IAEItemStack injectItems(IAEItemStack stack, Actionable actionable, IActionSource iActionSource) {
                    AILog.info("Injecting items");
                    // Check if it's modulation
                    if(actionable == MODULATE) {
                        // Increment mass for each item in stack
                        singularityOperated.mass = stack.getStackSize() * 10;

                        // Add stack to stored list
                        storedItems.add(stack);
                    }
                    // Storage is endless, so return stack
                    return stack;
                }

                @Override
                public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
                    return null;
                }

                @Override
                public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
                    AILog.info("Getting item list");
                    return storedItems;
                }

                @Override
                public IStorageChannel<IAEItemStack> getChannel() {
                    return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
                }

                // Only write access
                @Override
                public AccessRestriction getAccess() {
                    return AccessRestriction.WRITE;
                }

            });
        }

        // return empty list
        return handlers;
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

    public boolean activate(EntityPlayer p, EnumHand hand) {
        if(!world.isRemote){
            if(hand == EnumHand.MAIN_HAND){
                for(IMEInventoryHandler handler : getCellArray(AEApi.instance().storage().
                        getStorageChannel(IItemStorageChannel.class))){
                    AILog.chatLog("Injected: " + handler.injectItems(AEItemStack.
                                    fromItemStack(new ItemStack(Items.SNOWBALL, 64)),
                                            MODULATE, new MachineSource(this)).getStackSize());
                }
            }
        }
        return true;
    }
}
