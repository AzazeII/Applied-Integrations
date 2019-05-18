package AppliedIntegrations.Integration.AstralSorcery;


import AppliedIntegrations.Items.ItemEnum;
import appeng.api.AEApi;

/**
 * @Author Azazell
 */
public class AstralLoader {
	public static void preInit() {

		ItemEnum.registerAstralItems();
	}

	public static void init() {

		ItemEnum.registerAstralItemModels();
		AEApi.instance().partHelper().registerNewLayer(StarlightLayer.class.getName(), StarlightLayer.class.getName());
	}
}
