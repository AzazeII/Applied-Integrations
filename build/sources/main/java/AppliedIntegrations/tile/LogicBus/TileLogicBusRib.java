package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.IGridNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.*;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

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
        // List of sides
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
}
