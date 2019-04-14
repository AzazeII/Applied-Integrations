package AppliedIntegrations.Grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @Author Azazell
 */
public class ManaStorageChannel implements IManaStorageChannel {

    @Nonnull
    @Override
    public IItemList<IAEManaStack> createList() {
        return new ManaList();
    }

    @Nullable
    @Override
    public IAEManaStack createStack(@Nonnull Object o) {
        if (o instanceof Integer) {
            return new AEManaStack(((Integer) o).intValue());
        } else if (o instanceof AEManaStack) {
            return ((AEManaStack) o).copy();
        }
        return null;
    }

    @Nullable
    @Override
    public IAEManaStack readFromPacket(@Nonnull ByteBuf byteBuf) throws IOException {
        return AEManaStack.fromPacket(byteBuf);
    }

    @Nullable
    @Override
    public IAEManaStack createFromNBT(@Nonnull NBTTagCompound nbtTagCompound) {
        return AEManaStack.fromNBT(nbtTagCompound);
    }
}
