package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.*;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * @Author Azazell
 */
public class TileLogicBusRib extends TileLogicBusSlave implements IAIMultiBlock, IGridMultiblock {

    @Override
    public void tryConstruct(EntityPlayer p) {

    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.MULTIBLOCK);
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        // list of sides
        List<EnumFacing> sides = new ArrayList<>();
        // Iterate over all sides
        for(EnumFacing side : EnumFacing.values()){
            // Check if tile in this side is not instance of logic bus port or core, so rib will connect only to
            // other ribs in multiblock, or outer cable
            if(!(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusPort)
                    && !(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusCore)){
                sides.add(side);
            }
        }

        // Temp set
        EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
        // Add sides
        temp.addAll(sides);
        return temp;
    }

    @Override
    public boolean tryToFindCore(EntityPlayer p) {
        // Iterate over up and down
        for(EnumFacing vertical : Arrays.asList(UP, DOWN)) {
            // Iterate over horizontal sides
            for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {
                // Find candidate
                // OVERRIDEN: Move pos upward to height of port
                TileEntity candidate = world.getTileEntity(pos.offset(vertical).offset(horizontal));
                // Check if candidate is LogicBusCore
                if (candidate instanceof TileLogicBusCore) {
                    // Capture core in variable
                    TileLogicBusCore core = (TileLogicBusCore) candidate;
                    // Try to construct multi-block
                    core.tryConstruct(p);
                    return true;
                }
            }
        }
        return false;
    }

    private IItemStorageChannel getItemChannel(){
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    /**
     * @return Interface (not block) for interacting with ME Network's inventory, used by logic bus core to
     * inject autocrafting items to outer grid
     */
    public IMEInventory<IAEItemStack> getOuterGridInventory() {
        if(getGridNode() == null)
            return null;
        IGrid grid = getGridNode().getGrid();

        IStorageGrid storage = grid.getCache(IStorageGrid.class); // check storage gridnode

        return storage.getInventory(getItemChannel());
    }
}
