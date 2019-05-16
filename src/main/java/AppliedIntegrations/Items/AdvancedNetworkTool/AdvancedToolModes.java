package AppliedIntegrations.Items.AdvancedNetworkTool;

/**
 * @Author Azazell
 */
public enum AdvancedToolModes {
	WRENCH,
	ENTROPY_MANIPULATOR;

	public AdvancedToolModes cycleMode(boolean up) {
		try {
			// Check for up scroll
			if (up) {
				// Check if it is last mode
				if (this == ENTROPY_MANIPULATOR)
				// Switch to 1st
				{
					return AdvancedToolModes.values()[0];
				} else
				// Switch mode to next
				{
					return AdvancedToolModes.values()[this.ordinal() + 1];
				}
			} else {
				// Check if it is first mode
				if (this == AdvancedToolModes.values()[0])
				// Switch to last
				{
					return ENTROPY_MANIPULATOR;
				} else
				// Switch mode to previous
				{
					return AdvancedToolModes.values()[this.ordinal() - 1];
				}
			}
		} catch (IndexOutOfBoundsException indexOutOfBound) {
			// Ignored
		}

		return WRENCH;
	}
}
