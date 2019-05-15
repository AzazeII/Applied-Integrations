package AppliedIntegrations.tile.Server.helpers;

import AppliedIntegrations.tile.Server.ServerPortHandler;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.crafting.*;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.me.Grid;
import appeng.me.cache.CraftingGridCache;
import com.google.common.collect.ImmutableCollection;
import net.minecraft.inventory.InventoryCrafting;

import java.util.LinkedHashMap;
import java.util.List;

import static appeng.api.config.SecurityPermissions.CRAFT;

/**
 * @Author Azazell
 */
public class ServerPortCraftingHandler extends ServerPortHandler<IAEItemStack> implements ICraftingProvider {
    public ServerPortCraftingHandler(LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>> left,
                                     LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>> right,
                                     TileServerCore tileServerCore) {
        super(left, right, tileServerCore);
    }

    private CraftingGridCache getOuterCraftingGrid() {
        try {
            return (CraftingGridCache) host.getMainNetworkCraftingGrid();
        }catch (ClassCastException classCast) {
            throw new IllegalStateException("Applied integrations tried to cast ICraftingGrid into CraftingGridCache, this message indicates that AI" +
                    "didn't successfully casted it. It means some mod has overrode crafting grid cache. If you'll discover one, you can submit issue on github:" +
                    " https://github.com/AzazeII/Applied-Integrations/issues");
        }
    }

    /*
        1. Get pattern collection for current stack in iteration for list of stacks in filter of CRAFT map in filteredMatterMap
        2. If pattern isn't null, then push it into crafting tracker
     */
    @Override
    public void provideCrafting(ICraftingProviderHelper craftingTracker) {
        // Get crafting grid
        CraftingGridCache craftingGrid = getOuterCraftingGrid();

        // 1.
        // Get craft filter map. Get item stack list. Iterate for each stack in list
        filteredMatter.get(CRAFT).get(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)).forEach((stack) -> {
            // 2.
            // Check not null
            if (stack != null) {
                // Copy stack
                IAEItemStack copy = (IAEItemStack) stack.copy();

                // Remove any stack size from copy, to match CraftingGridCache map
                copy.setStackSize(0);

                // Try to explicitly get crafting for this stack. Explicitly, means that we'll just try to get non-null element from
                // CraftingGridCache map: craftableItems
                ImmutableCollection<ICraftingPatternDetails> patterns = craftingGrid.getCraftingFor(copy, null, 0, null);

                // Iterate for each pattern in collection
                for (ICraftingPatternDetails pattern : patterns) {
                    // Provide crafting pattern into crafting tracker
                    craftingTracker.addCraftingOption(this, pattern);
                }
            }
        });
    }

    /*
        1. Check if network has permissions to craft given outputs
        2. Try to pass call to each non-busy crafting medium of main network grid
     */
    @Override
    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        // 1.
        // Iterate for each outputs without nulls
        for (IAEItemStack output : patternDetails.getCondensedOutputs()) {
            // Check if can interact returned false
            if (!canInteract(output, CRAFT)) {
                // Skip iteration and break method
                return false;
            }
        }

        // 2.
        // Get main network crafting grid
        CraftingGridCache craftingGrid = getOuterCraftingGrid();

        // Iterate for each medium of main network crafting grid
        for (ICraftingMedium medium : craftingGrid.getMediums(patternDetails)) {
            // Check if medium is busy
            if (medium.isBusy())
                continue;

            // Try to push pattern via medium
            // Check for true result
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
