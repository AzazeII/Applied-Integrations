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
		ICraftingGrid craftingGrid = host.getMainNetworkCraftingGrid();
		if (craftingGrid == null) {
			return null;
		}

		ImmutableList<ICraftingCPU> cpuList = craftingGrid.getCpus().asList();
		for (ICraftingCPU cpu : cpuList) {
			if (cpu instanceof IAECluster) {
				return (IAECluster) cpu;
			}
		}

		return null;
	}
}