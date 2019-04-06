package AppliedIntegrations.tile.HoleStorageSystem.Anomalies;

import net.minecraft.block.state.IBlockState;

import java.util.LinkedHashMap;

import static net.minecraft.init.Blocks.*;

/**
 * @Author Azazell
 */
public class EntropyTransformations {
    // Map of all entropy entries from entropy tool class
    public final static LinkedHashMap<IBlockState, IBlockState> entropyMap = new LinkedHashMap<>();
    static {
        // Chiseled Stone Brick -> Stone brick -> corrupted stone break -> Stone -> Cobble -> Gravel
        entropyMap.put(STONEBRICK.getStateFromMeta(3),
                STONEBRICK.getDefaultState());
        entropyMap.put(STONEBRICK.getDefaultState(),
                STONEBRICK.getStateFromMeta(2));
        entropyMap.put(STONE.getDefaultState(),
                COBBLESTONE.getDefaultState());
        entropyMap.put(COBBLESTONE.getDefaultState(),
                GRAVEL.getDefaultState());

        // Dark prismarine brick -> Prismarine brick -> Prismarine
        entropyMap.put(PRISMARINE.getStateFromMeta(2),
                PRISMARINE.getStateFromMeta(1));
        entropyMap.put(PRISMARINE.getStateFromMeta(1),
                PRISMARINE.getStateFromMeta(0));

        // Lava -> Obsidian
        entropyMap.put(LAVA.getDefaultState(),
                OBSIDIAN.getDefaultState());

        // Flowing lava -> Obsidian
        entropyMap.put(FLOWING_LAVA.getDefaultState(),
                OBSIDIAN.getDefaultState());

        // Snow -> Water -> Ice
        entropyMap.put(SNOW.getDefaultState(),
                FLOWING_WATER.getDefaultState());
        entropyMap.put(FLOWING_WATER.getDefaultState(),
                ICE.getDefaultState());

        // Glass -> Sand
        entropyMap.put(GLASS.getDefaultState(),
                SAND.getDefaultState());

        // Anvil -> Damaged anvil -> Very damaged anvil
        entropyMap.put(ANVIL.getDefaultState(),
                ANVIL.getStateFromMeta(1));
        entropyMap.put(ANVIL.getStateFromMeta(1),
                ANVIL.getStateFromMeta(2));

        // Grass_path -> Grass -> Dirt -> Coarse dirt
        entropyMap.put(GRASS_PATH.getDefaultState(),
                GRASS.getDefaultState());
        entropyMap.put(GRASS.getDefaultState(),
                DIRT.getDefaultState());
        entropyMap.put(DIRT.getDefaultState(),
                DIRT.getStateFromMeta(1));

        // Wheat stage max -> ... -> Wheat stage min
        entropyMap.put(WHEAT.getStateFromMeta(6),
                WHEAT.getStateFromMeta(5));
        entropyMap.put(WHEAT.getStateFromMeta(5),
                WHEAT.getStateFromMeta(4));
        entropyMap.put(WHEAT.getStateFromMeta(4),
                WHEAT.getStateFromMeta(3));
        entropyMap.put(WHEAT.getStateFromMeta(3),
                WHEAT.getStateFromMeta(2));
        entropyMap.put(WHEAT.getStateFromMeta(2),
                WHEAT.getStateFromMeta(1));
        entropyMap.put(WHEAT.getStateFromMeta(1),
                WHEAT.getStateFromMeta(0));

        // Potato stage max -> ... -> Potato stage min
        entropyMap.put(POTATOES.getStateFromMeta(6),
                WHEAT.getStateFromMeta(5));
        entropyMap.put(POTATOES.getStateFromMeta(5),
                WHEAT.getStateFromMeta(4));
        entropyMap.put(POTATOES.getStateFromMeta(4),
                WHEAT.getStateFromMeta(3));
        entropyMap.put(POTATOES.getStateFromMeta(3),
                WHEAT.getStateFromMeta(2));
        entropyMap.put(POTATOES.getStateFromMeta(2),
                WHEAT.getStateFromMeta(1));
        entropyMap.put(POTATOES.getStateFromMeta(1),
                WHEAT.getStateFromMeta(0));

        // Wheat stage max -> ... -> Wheat stage min
        entropyMap.put(CARROTS.getStateFromMeta(6),
                WHEAT.getStateFromMeta(5));
        entropyMap.put(CARROTS.getStateFromMeta(5),
                WHEAT.getStateFromMeta(4));
        entropyMap.put(CARROTS.getStateFromMeta(4),
                WHEAT.getStateFromMeta(3));
        entropyMap.put(CARROTS.getStateFromMeta(3),
                WHEAT.getStateFromMeta(2));
        entropyMap.put(CARROTS.getStateFromMeta(2),
                WHEAT.getStateFromMeta(1));
        entropyMap.put(CARROTS.getStateFromMeta(1),
                WHEAT.getStateFromMeta(0));

        // humans' head -> skeleton head
        entropyMap.put(SKULL.getStateFromMeta(3),
                SKULL.getDefaultState());

        // All concrete -> all concrete powder
        for(int i = 0; i < 16; i++){
            entropyMap.put(CONCRETE.getStateFromMeta(i),
                    CONCRETE_POWDER.getStateFromMeta(i));
        }

        // (Red) Chiseled sandstone -> (Red) sandstone
        entropyMap.put(SANDSTONE.getStateFromMeta(1),
                SANDSTONE.getDefaultState());
        entropyMap.put(RED_SANDSTONE.getStateFromMeta(1),
                RED_SANDSTONE.getDefaultState());

    }
}
