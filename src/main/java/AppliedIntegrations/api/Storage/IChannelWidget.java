package AppliedIntegrations.api.Storage;

import appeng.api.storage.data.IAEStack;
import net.minecraft.client.gui.Gui;

/**
 * @Author Azazell
 * @apiNote This interface represents filter for given AE stack
 */
public interface IChannelWidget<T extends IAEStack<T>> {
    IAEStack<T> getAEStack();

    String getStackTip();

    void setAEStack(IAEStack<?> t);

    void drawWidget();

    boolean isMouseOverWidget(int x, int y);
}
