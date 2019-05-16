package AppliedIntegrations.Container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class AIContainer extends Container {
	/**
	 * The player interacting with this container.
	 */
	public final EntityPlayer player;

	private final List<Slot> slotMap = new ArrayList<>();

	public AIContainer(final EntityPlayer player) {
		// Set the player
		this.player = player;
	}

	@Override
	protected Slot addSlotToContainer(@Nonnull final Slot slot) {
		// Call super
		super.addSlotToContainer(slot);

		// Map the slot
		this.slotMap.add(slot.slotNumber, slot);

		return slot;
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		// Call super
		super.onContainerClosed(player);

		// Clear the map
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
