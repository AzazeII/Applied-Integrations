package AppliedIntegrations.tile.MultiController;


import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.AEPartLocation;
import appeng.me.GridAccessException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.EnumSet.noneOf;
import static java.util.EnumSet.of;


/**
 * @Author Azazell
 */
public class TileMultiControllerPort extends AIMultiControllerTile implements ICellContainer, ICraftingProvider {

	// Side of port in multi-block
	private AEPartLocation side = AEPartLocation.INTERNAL;

	// ID of port on edge
	private int portID = -1;

	public void setSideVector(AEPartLocation side) {
		// Update side
		this.side = side;

		// Update valid sides of proxy
		getProxy().setValidSides(getValidSides());

		// Notify node
		getProxy().getNode().updateState();
	}

	public AEPartLocation getSideVector() {
		return side;
	}

	public int getPortID() {
		return portID;
	}

	public void setPortID(int portID) {
		this.portID = portID;
	}

	public IGrid requestNetwork() throws GridAccessException {
		// Get grid and return it
		return getProxy().getGrid();
	}

	@Override
	protected EnumSet<EnumFacing> getValidSides() {
		// Check if tile has master
		if (hasMaster() && getSideVector() != AEPartLocation.INTERNAL)
			// Return vector of side
			return of(getSideVector().getFacing());
		// Empty
		return noneOf(EnumFacing.class);
	}

	public void onNeighborChange() throws GridAccessException {
		// Check if port has master
		if (hasMaster()) {
			// Get core
			TileMultiControllerCore core = (TileMultiControllerCore) getMaster();

			// Notify all networks
			core.postNetworkCellEvents();
		}
	}

	/* -----------------------------Drive Methods----------------------------- */
	@Override
	public void blinkCell(int slot) {
		// Ignored
	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		// Check not null
		if (getMaster() == null) {
			// Empty list
			return new ArrayList<>();
		}

		// Pass call to master
		return ((TileMultiControllerCore) getMaster()).getPortCellArray(side, portID, channel);
	}

	@Override
	public int getPriority() {
		// Ignored
		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> cellInventory) {
		// Check not null
		if (getMaster() == null) {
			// Skip
			return;
		}

		// Pass call to master
		((TileMultiControllerCore) getMaster()).savePortChanges(cellInventory, side, portID);
	}
	/* -----------------------------Drive Methods----------------------------- */

	/* -----------------------------Crafting Methods----------------------------- */
	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {
		// Check not null
		if (getMaster() == null) {
			// Skip
			return;
		}

		// Pass call to master
		((TileMultiControllerCore) getMaster()).providePortCrafting(craftingTracker, side, portID);
	}

	@Override
	public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
		// Check not null
		if (getMaster() == null) {
			// Skip
			return false;
		}

		// Pass call to master
		return ((TileMultiControllerCore) getMaster()).pushPortPattern(patternDetails, table, side, portID);
	}

	@Override
	public boolean isBusy() {
		// Check not null
		if (getMaster() == null) {
			// Skip
			return false;
		}

		// Pass call to master
		return ((TileMultiControllerCore) getMaster()).isPortBusy(side, portID);
	}
	/* -----------------------------Crafting Methods----------------------------- */
}
