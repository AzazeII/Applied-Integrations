package AppliedIntegrations.Parts.Energy;


import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;

/**
 * @Author Azazell
 */

// TODO Rewrite EVERYTHING here!:
/*
    1. onActivate mechanics
    2. Cell container mechanics
    3. Actually showing energy
    4. Packets
*/
public class PartEnergyStorageMonitor extends AIRotatablePart implements IStackWatcherHost, IPowerChannelState {

	private LiquidAIEnergy energy = null;

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
	public void writeToNBT(NBTTagCompound data) {

		super.writeToNBT(data);
		// TODO See line at class start
	}

	@Override
	public int getLightLevel() {
		// TODO See line at class start
		return this.isActive() ? 0 : 1;
	}

	@Override
	public double getIdlePowerUsage() {
		// TODO See line at class start
		return 0.5D;
	}

	@Override
	public void onEntityCollision(Entity entity) {
		// TODO See line at class start
	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {

		return 2;
	}

	@Override
	public void updateWatcher(IStackWatcher w) {
		// TODO See line at class start
	}

	@Override
	public void onStackChange(IItemList<?> iItemList, IAEStack<?> iaeStack, IAEStack<?> iaeStack1, IActionSource iActionSource, IStorageChannel<?> iStorageChannel) {
		// TODO See line at class start
	}

	@Override
	public void writeToStream(ByteBuf data) throws IOException {

		super.writeToStream(data);
		// TODO See line at class start
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d pos) {
		// TODO See line at class start
		return false;
	}
}