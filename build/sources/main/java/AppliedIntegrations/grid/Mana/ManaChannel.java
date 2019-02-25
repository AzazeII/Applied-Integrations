package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaChannel;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
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
        if (o instanceof Integer) {
            return this.createStack(o);
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
