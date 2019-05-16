package AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons;

import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;

public abstract class GuiServerButton extends AIGuiButton {
	protected final GuiServerTerminal host;

	public GuiServerButton(GuiServerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {
		super(ID, xPosition, yPosition, width, height, text);
		this.host = terminal;
	}
}
