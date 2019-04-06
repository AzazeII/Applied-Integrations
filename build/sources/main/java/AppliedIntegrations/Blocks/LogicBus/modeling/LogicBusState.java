package AppliedIntegrations.Blocks.LogicBus.modeling;

import net.minecraft.util.EnumFacing;

import java.util.EnumSet;
import java.util.Set;

/**
 * @Author Azazell
 */
public class LogicBusState {

    // Is isCorner of multiblock?
    private final boolean isCorner;
    private final boolean hasMaster;
    private final EnumSet<EnumFacing> sidesWithSlaves;

    public LogicBusState(EnumSet<EnumFacing> facings, boolean corner, boolean hasMaster)
    {
        this.sidesWithSlaves = facings;
        this.isCorner = corner;
        this.hasMaster = hasMaster;
    }

    public boolean isCorner()
    {
        return this.isCorner;
    }
    public boolean hasMaster() { return this.hasMaster; }

    public Set<EnumFacing> getSidesWithSlave() {
        return sidesWithSlaves;
    }
}
