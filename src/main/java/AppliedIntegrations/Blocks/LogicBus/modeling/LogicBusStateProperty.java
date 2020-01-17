package AppliedIntegrations.Blocks.LogicBus.modeling;


import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * @Author Azazell
 */
public class LogicBusStateProperty implements IUnlistedProperty<LogicBusState> {
	@Override
	public String getName() {
		return "Logic_bus_formed_state";
	}

	@Override
	public boolean isValid(LogicBusState value) {
		return value != null;
	}

	@Override
	public Class<LogicBusState> getType() {
		return LogicBusState.class;
	}

	@Override
	public String valueToString(LogicBusState value) {
		return value.toString();
	}
}
