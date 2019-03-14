package AppliedIntegrations.Blocks.LogicBus;

import net.minecraft.util.EnumFacing;

import java.util.Set;

public class LogicBusState {

    // Is rib of multiblock?
    private final boolean rib;

    // Is powered?
    private final boolean powered;

    public LogicBusState( boolean rib, boolean powered )
    {
        this.rib = rib;
        this.powered = powered;
    }

    public boolean isRib()
    {
        return this.rib;
    }

    public boolean isPowered()
    {
        return this.powered;
    }
}
