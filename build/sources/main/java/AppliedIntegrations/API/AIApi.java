package AppliedIntegrations.API;

import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import AppliedIntegrations.API.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.API.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.storage.IStorageChannel;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public abstract class AIApi {

	private static AIApi api = new AIApi() {
		@Override
		public void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
										  IStorageChannel chan) {
			TileMEPylon.addBlackHoleHandler(handlerClassA, chan);
			TileMEPylon.addWhiteHoleHandler(handlerClassB, chan);
		}
	};

	@Nonnull
	public static AIApi instance()
	{
		return api;
	}

    public abstract void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
											   IStorageChannel chan);
}
