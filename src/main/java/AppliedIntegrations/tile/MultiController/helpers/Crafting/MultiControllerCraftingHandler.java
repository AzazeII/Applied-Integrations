package AppliedIntegrations.tile.MultiController.helpers.Crafting;


import AppliedIntegrations.tile.MultiController.MultiControllerPortHandler;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.me.cache.CraftingGridCache;
import com.google.common.collect.ImmutableCollection;
import net.minecraft.inventory.InventoryCrafting;

import java.util.LinkedHashMap;
import java.util.List;

import static appeng.api.config.SecurityPermissions.CRAFT;

/**
 * @Author Azazell
 */
public class MultiControllerCraftingHandler extends MultiControllerPortHandler<IAEItemStack> implements ICraftingProvider {
	public MultiControllerCraftingHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> left, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> right, TileMultiControllerCore tileMultiControllerCore) {
		super(left, right, tileMultiControllerCore);
	}

	/*
		1. Get pattern collection for current stack in iteration for list of stacks in filter of CRAFT map in filteredMatterMap
		2. If pattern isn't null, then push it into crafting tracker
	 */
	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {
		CraftingGridCache craftingGrid = getOuterCraftingGrid();

		if (craftingGrid == null)
			return;

		if (filteredMatter.get(CRAFT) == null || filteredMatter.get(CRAFT).get(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) == null) {
			return;
		}

		// 1.
		// Get craft filter map. Get item stack list. Iterate for each stack in list
		filteredMatter.get(CRAFT).get(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)).forEach((stack) -> {
			// 2.
			if (stack != null) {
				IAEItemStack copy = (IAEItemStack) stack.copy();

				copy.setStackSize(0);

				// Try to explicitly get crafting for this stack. Explicitly, means that we'll just try to get non-null element from
				// CraftingGridCache map: craftable items
				ImmutableCollection<ICraftingPatternDetails> patterns = craftingGrid.getCraftingFor(copy, null, 0, null);
				for (ICraftingPatternDetails pattern : patterns) {
					// Provide crafting pattern into crafting tracker
					craftingTracker.addCraftingOption(this, pattern);
				}
			}
		});
	}

	private CraftingGridCache getOuterCraftingGrid() {

		try {
			return (CraftingGridCache) host.getMainNetworkCraftingGrid();
		} catch (ClassCastException classCast) {
			throw new IllegalStateException("Applied integrations tried to" +
					" cast ICraftingGrid into CraftingGridCache," +
					" this message indicates that AI"
					+ "didn't successfully casted it." +
					" It means some mod has overrode" +
					" crafting grid cache. If you'll " +
					"get this message, you can submit " +
					"issue on github:" + " https://github.com/AzazeII/Applied-Integrations/issues");
		}
	}

	/*
		1. Check if network has permissions to craft given outputs
		2. Try to pass call to each non-busy crafting medium of main network grid
	 */
	@Override
	public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
		if (filteredMatter.get(CRAFT) == null || filteredMatter.get(CRAFT).get(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) == null) {
			return false;
		}

		// 1.
		for (IAEItemStack output : patternDetails.getCondensedOutputs()) {
			if (!canInteract(output, CRAFT)) {
				return false;
			}
		}

		// 2.
		CraftingGridCache craftingGrid = getOuterCraftingGrid();

		for (ICraftingMedium medium : craftingGrid.getMediums(patternDetails)) {
			if (medium.isBusy()) {
				continue;
			}

			// Try to push pattern via medium
			if (medium.pushPattern(patternDetails, table)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isBusy() {
		// Ignored
		return false;
	}
}
