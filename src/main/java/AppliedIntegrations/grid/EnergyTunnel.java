package AppliedIntegrations.grid;

import AppliedIntegrations.API.EnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyTunnel implements IEnergyTunnel {
    @Nonnull
    @Override
    public IItemList<IAEEnergyStack> createList() {
        return new EnergyList();
    }

    @Nullable
    @Override
    public IAEEnergyStack createStack(@Nonnull Object o) {
        if (o instanceof LiquidAIEnergy) {
            return this.createStack(new EnergyStack((LiquidAIEnergy) o, Integer.MAX_VALUE));
        } else if (o instanceof EnergyStack) {
            return AEEnergyStack.fromStack((EnergyStack) o);
        } else if (o instanceof AEEnergyStack) {
            return ((AEEnergyStack) o).copy();
        }
        return null;
    }

    @Nullable
    @Override
    public IAEEnergyStack readFromPacket(@Nonnull ByteBuf buf) {
        return AEEnergyStack.fromPacket(buf);
    }

    @Nullable
    @Override
    public IAEEnergyStack createFromNBT(@Nonnull NBTTagCompound tag) {
        return AEEnergyStack.fromNBT(tag);
    }
}
