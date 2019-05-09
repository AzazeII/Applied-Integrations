package AppliedIntegrations.api;

import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author Azazell
 */
public abstract class AIApi {
	public static AIApi instance() {
		try {
			// Create reflection of our api
			Class apiReflection = Class.forName("AppliedIntegrations.ApiInstance");

			// Create instance reflection of static api
			Method instanceReflection = apiReflection.getMethod("staticInstance");

			// Get static api and return it
			return (AIApi) instanceReflection.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {

		}

		return null;
	}

    public abstract void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
											   IStorageChannel chan);

	/**
	 * Used by storage channel gui button
	 * @param channel Key for sprite
	 * @return Sprite of given channel
	 */
    public abstract ResourceLocation getSpriteFromChannel(IStorageChannel<? extends IAEStack<?>> channel);

	/**
	 * Map new sprite to channel
	 * @param channel map key
	 * @param sprite map value
	 */
	public abstract void addChannelSprite(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite);
}
