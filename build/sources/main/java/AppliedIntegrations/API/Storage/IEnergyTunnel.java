package AppliedIntegrations.API.Storage;

import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public interface IEnergyTunnel extends IStorageChannel<IAEEnergyStack> {
}
