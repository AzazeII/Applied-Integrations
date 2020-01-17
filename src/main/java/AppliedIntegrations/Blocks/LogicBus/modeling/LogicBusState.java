package AppliedIntegrations.Blocks.LogicBus.modeling;
/**
 * @Author Azazell
 */
public class LogicBusState {
	private final boolean isCorner;
	private final boolean hasMaster;

	public LogicBusState(boolean corner, boolean hasMaster) {
		this.isCorner = corner;
		this.hasMaster = hasMaster;
	}

	public boolean isCorner() {
		return this.isCorner;
	}

	public boolean hasMaster() {
		return this.hasMaster;
	}
}
