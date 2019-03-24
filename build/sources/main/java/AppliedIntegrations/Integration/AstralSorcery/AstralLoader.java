package AppliedIntegrations.Integration.AstralSorcery;

import AppliedIntegrations.Items.ItemEnum;

public class AstralLoader {
    public static void preInit() {
        ItemEnum.registerAstralItems();
    }

    public static void init() {
        ItemEnum.registerAstralItemModels();
    }
}
