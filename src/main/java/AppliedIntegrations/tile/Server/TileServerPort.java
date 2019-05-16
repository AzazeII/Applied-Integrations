package AppliedIntegrations.tile.Server;


import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.AEPartLocation;
import appeng.me.helpers.AENetworkProxy;
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
public class TileServerPort extends AIServerMultiBlockTile implements ICellContainer, ICraftingProvider {

	private AEPartLocation side = AEPartLocation.INTERNAL;
	private AENetworkProxy outerProxy = new AENetworkProxy(this, "AI Tile Outer Proxy", getProxy().getMachineRepresentation(), true);

	public TileServerPort() {
		super();

		// Set inner proxy constants
		this.getProxy().setFlags(GridFlags.CANNOT_CARRY);
		this.getProxy().setIdlePowerUsage(0);

		// Set outer proxy constants
		this.outerProxy.setFlags(GridFlags.CANNOT_CARRY);
		this.outerProxy.setIdlePowerUsage(0);
	}

	public void setDir(EnumFacing side) {
		// Update side
		this.side = AEPartLocation.fromFacing(side);

		// Make outer proxy connectible
		this.outerProxy.setValidSides(of(side));
	}

	public AEPartLocation getSideVector() {
		return side;
	}

	public IGrid requestNetwork() {
		// Check not null
		if (gridNode == null) {
			return null;
		}

		// Get grid and return it
		return gridNode.getGrid();
	}

	@Override
	public void validate() {
		super.validate();
		this.onNeighborChange();
		this.outerProxy.onReady();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.outerProxy.invalidate();

	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		this.outerProxy.onChunkUnload();
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

	public void onNeighborChange() {
		// Check if port has master
		if (hasMaster()) {
			// Get core
			TileServerCore core = (TileServerCore) getMaster();

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
		return ((TileServerCore) getMaster()).getPortCellArray(side, channel);
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
		((TileServerCore) getMaster()).savePortChanges(cellInventory, side);
	}
	/* -----------------------------Drive Methods----------------------------- */

	/* -----------------------------Crafting Methods----------------------------- */
	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {
		// Check not null
		if (getMaster() == null)
		// Skip
		{
			return;
		}

		// Pass call to master
		((TileServerCore) getMaster()).providePortCrafting(craftingTracker, side);
	}

	@Override
	public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
		// Check not null
		if (getMaster() == null)
		// Skip
		{
			return false;
		}

		// Pass call to master
		return ((TileServerCore) getMaster()).pushPortPattern(patternDetails, table, side);
	}

	@Override
	public boolean isBusy() {
		// Check not null
		if (getMaster() == null)
		// Skip
		{
			return false;
		}

		// Pass call to master
		return ((TileServerCore) getMaster()).isPortBusy(side);
	}
	/* -----------------------------Crafting Methods----------------------------- */
}
