package AppliedIntegrations.Container;
import AppliedIntegrations.Container.Sync.ISyncHostHolder;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class AIContainer extends Container implements ISyncHostHolder {
	public final EntityPlayer player;

	private final List<Slot> slotMap = new ArrayList<>();

	public AIContainer(final EntityPlayer player) {
		this.player = player;
	}

	protected void syncHostWithGUI() {
		NetworkHandler.sendTo(new PacketCoordinateInit(getSyncHost()), (EntityPlayerMP) this.player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (Platform.isClient())
			return;

		this.syncHostWithGUI();
	}

	@Override
	protected Slot addSlotToContainer(@Nonnull final Slot slot) {
		super.addSlotToContainer(slot);
		this.slotMap.add(slot.slotNumber, slot);

		return slot;
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		super.onContainerClosed(player);
		this.slotMap.clear();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Nullable
	public Slot getSlotOrNull(final int slotNumber) {
		return this.slotMap.get(slotNumber);
	}
}
