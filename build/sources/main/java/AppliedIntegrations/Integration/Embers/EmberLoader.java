package AppliedIntegrations.Integration.Embers;

import AppliedIntegrations.Items.ItemEnum;

public class EmberLoader {
    public static void preInit() {
        ItemEnum.registerEmbersItems();
    }

    public static void init() {
        ItemEnum.registerEmbersItemModels();
    }
}