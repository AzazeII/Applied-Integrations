package AppliedIntegrations.Blocks.LogicBus.modeling;


import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * @Author Azazell
 */
public class LogicBusStateProperty implements IUnlistedProperty<LogicBusState> /* V = LogicBusState*/ {
	// Registry name of this stateProp
	@Override
	public String getName() {

		return "Logic_bus_formed_state";
	}

	// Is stateProp valid ?
	@Override
	public boolean isValid(LogicBusState value) {

		return value != null;
	}

	// Type of stateProp ( simply V.class )
	@Override
	public Class<LogicBusState> getType() {

		return LogicBusState.class;
	}

	// String representation of V stateProp
	@Override
	public String valueToString(LogicBusState value) {

		return value.toString();
	}
}
