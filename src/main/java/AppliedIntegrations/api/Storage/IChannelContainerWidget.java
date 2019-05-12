package AppliedIntegrations.api.Storage;

import appeng.api.storage.data.IAEStack;
import net.minecraft.inventory.Slot;

public interface IChannelContainerWidget<T extends IAEStack<T>> extends IChannelWidget<T> {
    Slot getSlotWrapper();

    void visible(boolean b);
}
