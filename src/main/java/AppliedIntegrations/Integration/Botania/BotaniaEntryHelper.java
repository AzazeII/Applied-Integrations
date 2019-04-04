package AppliedIntegrations.Integration.Botania;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.lexicon.BLexiconCategory;
import vazkii.botania.common.lexicon.BasicLexiconEntry;

/**
 * @Author Azazell
 */
public class BotaniaEntryHelper {
    private LexiconCategory appliedIntegrations;

    // Botania and ae2 cross
    private LexiconEntry crossover;
    // Alfheim recipes
    private LexiconEntry crystals;
    // Machines
    private LexiconEntry manaManipulation;

    public void createPages() {
        BotaniaAPI.addCategory(appliedIntegrations = new BLexiconCategory("Applied Integrations", 4));
        appliedIntegrations.setIcon(new ResourceLocation(AppliedIntegrations.modid, "textures/items/manastoragecell_1k.png"));

        crystals = new BasicLexiconEntry("MaterialEncorium", appliedIntegrations);
        crossover = new BasicLexiconEntry("Crossover", appliedIntegrations);
        manaManipulation = new BasicLexiconEntry("ManaDevices", appliedIntegrations);
    }
}
