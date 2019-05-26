package AppliedIntegrations.Gui.MultiController.SubGui.Buttons;


import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.MultiController.GuiMultiControllerTerminal;

public abstract class GuiServerButton extends AIGuiButton {
	protected final GuiMultiControllerTerminal host;

	public GuiServerButton(GuiMultiControllerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {

		super(ID, xPosition, yPosition, width, height, text);
		this.host = terminal;
	}
}