package AppliedIntegrations.Blocks.LogicBus.modeling;

import net.minecraft.util.EnumFacing;

import java.util.Set;

public class LogicBusState {

    // Is rib of multiblock?
    private final boolean rib;

    public LogicBusState( boolean rib)
    {
        this.rib = rib;
    }

    public boolean isRib()
    {
        return this.rib;
    }

}
