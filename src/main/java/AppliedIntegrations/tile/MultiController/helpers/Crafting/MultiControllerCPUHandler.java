package AppliedIntegrations.tile.MultiController.helpers.Crafting;


import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.me.cluster.IAECluster;
import appeng.tile.crafting.TileCraftingStorageTile;
import com.google.common.collect.ImmutableList;

/**
 * @Author Azazell
 */
public class MultiControllerCPUHandler extends TileCraftingStorageTile {
	private final TileMultiControllerCore host;

	public MultiControllerCPUHandler(TileMultiControllerCore core) {
		this.host = core;
	}

	@Override
	public IAECluster getCluster() {
		// Get crafting grid from host
		ICraftingGrid craftingGrid = host.getMainNetworkCraftingGrid();

		// Check not null
		if (craftingGrid == null) {
			return null;
		}

		// Get list of all CPU in grid
		ImmutableList<ICraftingCPU> cpuList = craftingGrid.getCpus().asList();

		// Iterate for each CPU in list
		for (ICraftingCPU cpu : cpuList) {
			// Check if CPU is cluster
			if (cpu instanceof IAECluster) {
				// Use this CPU
				return (IAECluster) cpu;
			}
		}

		return null;
	}
}