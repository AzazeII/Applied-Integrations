package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaChannel;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class ManaChannel implements IManaChannel {

    @Nonnull
    @Override
    public IItemList<IAEManaStack> createList() {
        return new ManaList();
    }

    @Nullable
    @Override
    public IAEManaStack createStack(@Nonnull Object o) {
        return new AEManaStack((int)o);
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
