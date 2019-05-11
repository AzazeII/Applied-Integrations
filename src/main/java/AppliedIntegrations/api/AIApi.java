package AppliedIntegrations.api;

import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.Server.helpers.FilteredServerPortHandler;
import AppliedIntegrations.tile.Server.helpers.FilteredServerPortItemHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
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

		IAEStack<?> convert(ItemStack stack) throws IOException;
	}
	/**
	 * Functional interface for encoding ae stack to buf
	 */
	@FunctionalInterface
	public interface IStackEncoder {

		void encode(NBTTagCompound tag, IAEStack<?> stack) throws IOException;
	}
	/**
	 * Functional interface for decoding ae stack from buf
	 */
	@FunctionalInterface
	public interface IStackDecoder {

		IAEStack<?> decode(NBTTagCompound tag) throws IOException;
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
	 * @param UV map value #3 U, V for sprite
	 * @param coderPair map value #4
	 * @param handler
	 */
	public abstract void addChannelToServerFilterList(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite,
													  Constructor<? extends IChannelWidget> widgetConstructor,
													  Constructor<? extends FilteredServerPortHandler> handler,
													  IStackConverter lambda,
													  Pair<Integer, Integer> UV, Pair<IStackEncoder, IStackDecoder> coderPair);

	/**
	 * Used by storage channel gui button
	 * @param channel Key for sprite
	 * @return Sprite of given channel
	 */
	public abstract ResourceLocation getSpriteFromChannel(IStorageChannel<? extends IAEStack<?>> channel);
	public abstract int getSpriteU(IStorageChannel<? extends IAEStack<?>> channel);
	public abstract int getSpriteV(IStorageChannel<? extends IAEStack<?>> channel);
	/**
	 * @param chan Key for widget
	 * @return Widget for displaying filter for material of current channel
	 */
    public abstract Constructor<? extends IChannelWidget> getWidgetFromChannel(IStorageChannel<? extends IAEStack<?>> chan);

	public abstract IStackEncoder getStackEncoder(IStorageChannel<? extends IAEStack<?>> chan);
	public abstract IStackDecoder getStackDecoder(IStorageChannel<? extends IAEStack<?>> chan);

	/**
	 * @param itemStack Itemstack to convert
	 * @param chan Key for convertor
	 * @return Converted stack
	 */
	@Nullable
	public abstract IAEStack<?> getAEStackFromItemStack(IStorageChannel<? extends IAEStack<?>> chan, ItemStack itemStack);

	/**
	 * @return  ME inventory handler from given channel
	 */
	public abstract Constructor<? extends FilteredServerPortHandler> getHandlerFromChannel(IStorageChannel<? extends IAEStack<?>> channel);
}
