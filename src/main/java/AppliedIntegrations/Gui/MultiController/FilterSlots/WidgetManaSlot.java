package AppliedIntegrations.Gui.MultiController.FilterSlots;


import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Storage.IChannelWidget;
import appeng.api.storage.data.IAEStack;

public class WidgetManaSlot implements IChannelWidget<IAEManaStack> {
	private IAEManaStack stack;

	@Override
	public IAEStack<IAEManaStack> getAEStack() {

		return this.stack;
	}

	@Override
	public void setAEStack(IAEStack<?> iaeManaStack) {

		this.stack = (IAEManaStack) iaeManaStack;
	}

	@Override
	public String getStackTip() {

		return null;
	}

	@Override
	public void drawWidget() {

	}

	@Override
	public boolean isMouseOverWidget(int x, int y) {

		return false;
	}
}
