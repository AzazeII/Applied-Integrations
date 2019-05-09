package AppliedIntegrations.api;

import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author Azazell
 */
public abstract class AIApi {
	/**
	 * Functional interface for converting normal ItemStack to IAEStack
	 */
	@FunctionalInterface
	public interface IStackConverter {
		IAEStack<?> convert(ItemStack stack);
	}

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
	 * Map new sprite to channel
	 * @param channel map key
	 * @param sprite map value #1
	 * @param widgetConstructor map value #2
	 */
	public abstract void addChannelToServerFilterList(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite,
													  Constructor<? extends IChannelWidget> widgetConstructor, IStackConverter lambda);

	/**
	 * Used by storage channel gui button
	 * @param channel Key for sprite
	 * @return Sprite of given channel
	 */
	public abstract ResourceLocation getSpriteFromChannel(IStorageChannel<? extends IAEStack<?>> channel);

	/**
	 * @param chan Key for widget
	 * @return Widget for displaying filter for material of current channel
	 */
    public abstract Constructor<? extends IChannelWidget> getWidgetFromChannel(IStorageChannel<? extends IAEStack<?>> chan);

	/**
	 * @param itemStack Itemstack to convert
	 * @param chan Key for convertor
	 * @return Converted stack
	 */
	@Nullable
	public abstract IAEStack<?> getAEStackFromItemStack(IStorageChannel<? extends IAEStack<?>> chan, ItemStack itemStack);
}
