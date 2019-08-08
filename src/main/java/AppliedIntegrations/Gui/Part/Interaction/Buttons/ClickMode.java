package AppliedIntegrations.Gui.Part.Interaction.Buttons;

/**
 * @Author Azazell
 */
public enum ClickMode {
	SHIFT_CLICK("Shift click"),
	CLICK("Normal click");

	public final String tip;

	ClickMode(String tip) {
		this.tip = tip;
	}
}
