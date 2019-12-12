package AppliedIntegrations.tile.HoleStorageSystem;
import AppliedIntegrations.tile.AITile;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.util.Platform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @Author Azazell
 * Class of tiles that stand for ME cell inventory.
 * @see AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation
 * @see AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon
 */
public abstract class AITileStorageCell extends AITile implements IGridTickable, ICellContainer {
	public boolean syncActive = false;

	@Override
	public void createProxyNode() {
		super.createProxyNode();

		// Post cell event when node is created
		if (world != null && Platform.isServer()) {
			postCellInventoryEvent();
		}
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(1, 1, false, false);
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		// Call only on server
		if (Platform.isServer()) {
			// Check if node was active
			if (!syncActive && node.isActive()) {
				// Node wasn't active, but now it is active
				// Fire new cell array update event!
				postCellInventoryEvent();
				// Update sync
				syncActive = true;
			} else if (syncActive && !node.isActive()) {
				// Node was active, but now it's not
				// Fire new cell array update event!
				postCellInventoryEvent();
				// Update sync
				syncActive = false;
			}
		}
		return TickRateModulation.SAME;
	}

	@Override
	public void blinkCell(int slot) {
		// Typically ignored on handler from AI
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
		// Check if inventory not null
		if (iCellInventory != null) {
			// Persist inventory
			iCellInventory.persist();
		}

		// Mark dirty
		world.markChunkDirty(pos, this);
	}
}
