package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.WrenchUtil;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

/**
 * @Author Azazell
 */

public class PartEnergyStorageMonitor extends AIRotatablePart implements IStackWatcherHost,IPowerChannelState {

    private LiquidAIEnergy energy = null;
    private long amount = 0L;
    private Object dspList;

    private boolean locked = false;

    private IStackWatcher watcher = null;

    public PartEnergyStorageMonitor() {
        super(PartEnum.EnergyStorageMonitor);
    }

    @Override
    protected AIGridNodeInventory getUpgradeInventory() {
        return null;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 13, 12, 12, 14);
        bch.addBox(5, 5, 12, 11, 11, 13);
    }

    @Override
    public double getIdlePowerUsage() {
        return 0.5D;
    }

    @Override
    public int getLightLevel() {
        return this.isActive() ? 0 : 1;
    }

    @Override
    public NBTTagCompound getWailaTag(NBTTagCompound tag) {
        super.getWailaTag(tag);
        tag.setBoolean("locked", this.locked);
        tag.setLong("amount", this.amount);
        if (this.energy == null)
            tag.setString("Energy", "");
        else
            tag.setString("Energy", this.energy.getTag());
        return tag;
    }

    @Override
    public boolean onActivate(EntityPlayer player,EnumHand hand, Vec3d pos) {
        if (player == null || player.world == null)
            return false;
        if (player.world.isRemote)
            return false;
        ItemStack s = player.getHeldItem(hand);
        if (WrenchUtil.canWrench(s, player, this.hostTile.getPos().getX(), this.hostTile.getPos().getY(),
                this.hostTile.getPos().getZ())) {
            this.locked = !this.locked;
            WrenchUtil.wrenchUsed(s, player, this.hostTile.getPos().getX(),
                    this.hostTile.getPos().getY(), this.hostTile.getPos().getZ());
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
            if (this.locked)
                player.sendMessage(new TextComponentString(
                        "chat.appliedenergistics2.isNowLocked"));
            else
                player.sendMessage(new TextComponentString(
                        "chat.appliedenergistics2.isNowUnlocked"));
            return true;
        }
        if (this.locked)
            return false;
        if (Utils.getEnergyFromItemStack(s) != null) {
            if (this.energy != null && this.watcher != null)
                this.watcher.remove(getChannel().createStack(new FluidStack(this.energy, 1)));
            this.energy = Utils.getEnergyFromItemStack(s);
            if (this.watcher != null)
                this.watcher.add(getChannel().createStack(new FluidStack(this.energy, 1)));
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public float getCableConnectionLength(AECableType aeCableType) {
        return 2;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("amount"))
            this.amount = data.getLong("amount");
        if (data.hasKey("Energy")) {
            String id = data.getString("Energy");
            if (id.equals(""))
                this.energy = null;
            else
                this.energy = LiquidAIEnergy.getEnergy(data.getString("Energy"));
        }
        if (data.hasKey("locked"))
            this.locked = data.getBoolean("locked");
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        super.readFromStream(data);
        this.amount = data.readLong();
        int id = data.readInt();
        if (id == -1)
            this.energy = null;
        else
            this.energy = LiquidAIEnergy.getEnergy(id + "");
        this.locked = data.readBoolean();
        return true;
    }

    @Override
    public void onEntityCollision(Entity entity) {

    }

    @Override
    public void updateWatcher(IStackWatcher w) {
        this.watcher = w;
        if (this.energy != null)
            w.add(getChannel().createStack(new FluidStack(energy, 1)));
        onStackChange(null, null, null, null, null);
    }

    @Override
    public void onStackChange(IItemList<?> iItemList, IAEStack<?> iaeStack, IAEStack<?> iaeStack1, IActionSource iActionSource, IStorageChannel<?> iStorageChannel) {
        if (this.energy != null) {
            IMEMonitor<IAEEnergyStack> Energies = super.getEnergyInventory();
            if (Energies == null)
                return;
            for (IAEEnergyStack s : Energies.getStorageList()) {
                if (s.getEnergy() == this.energy) {
                    this.amount = s.getStackSize();
                    IPartHost host = getHost();
                    if (host != null)
                        host.markForUpdate();
                    return;
                }
            }
            this.amount = 0L;
            IPartHost host = getHost();
            if (host != null)
                host.markForUpdate();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setLong("amount", this.amount);
        if (this.energy == null)
            data.setString("Energy", "");
        else
            data.setString("Energy", this.energy.getTag());
        data.setBoolean("locked", this.locked);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeLong(this.amount);
        if (this.energy == null)
            data.writeInt(-1);
        else
            data.writeInt(this.energy.getIndex());
        data.writeBoolean(this.locked);

    }
}