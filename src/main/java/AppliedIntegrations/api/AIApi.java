package AppliedIntegrations.api;


import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
		IAEStack<?> convert(ItemStack stack, World world) throws IOException;
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
			// Access API implementation via reflection.
			Class apiReflection = Class.forName("AppliedIntegrations.ApiInstance");
			Method instanceReflection = apiReflection.getMethod("staticInstance");
			return (AIApi) instanceReflection.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			System.out.println("Error when accessing implementation of AIApi: " + e.getMessage());
		}

		return null;
	}

	public abstract void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB, IStorageChannel chan);

	/**
	 * Map new sprite to channel
	 *
	 * @param channel           map key
	 * @param sprite            map value #1
	 * @param widgetConstructor map value #2
	 * @param UV                map value #3 U, V for sprite
	 * @param coderPair         map value #4
	 */
	public abstract void addChannelToServerFilterList(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite, Constructor<? extends IChannelWidget> widgetConstructor, Constructor<? extends FilteredMultiControllerPortHandler> handler, IStackConverter lambda, Pair<Integer, Integer> UV, Pair<IStackEncoder, IStackDecoder> coderPair);

	/**
	 * Used by storage channel gui button
	 *
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
	 * @param chan      Key for convertor
	 * @param itemStack Itemstack to convert
	 * @param world     World where this operation is performed. Needed for case when stack stores block with tile
	 * @return Converted stack
	 */
	@Nullable
	public abstract IAEStack<?> getAEStackFromItemStack(IStorageChannel<? extends IAEStack<?>> chan, ItemStack itemStack, World world);

	/**
	 * @return ME inventory handler from given channel
	 */
	public abstract Constructor<? extends FilteredMultiControllerPortHandler> getHandlerFromChannel(IStorageChannel<? extends IAEStack<?>> channel);

	/**
	 * @param is Key stack
	 * @return ItemPart stack representing p2p tunnel
	 */
	public abstract ItemStack getTunnelFromStack(Item is);

	/**
	 * @param is Key stack
	 * @param tunnel Any AI p2p tunnel
	 */
	public abstract void addTunnelAsStack(Item is, ItemStack tunnel);
}
