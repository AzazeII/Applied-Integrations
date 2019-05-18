package AppliedIntegrations;


import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import AppliedIntegrations.tile.Server.helpers.Matter.FilteredServerPortHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public class ApiInstance extends AIApi {
	// ----# Channel Maps #---- //
	private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, ResourceLocation> channelSpriteMap = new LinkedHashMap<>();

	private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, Constructor<? extends IChannelWidget>> channelConstructorMap = new LinkedHashMap<>();

	private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, Constructor<? extends FilteredServerPortHandler>> channelHandlerMap = new LinkedHashMap<>();

	private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IStackConverter> channelStackConverterMap = new LinkedHashMap<>();

	private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, Pair<Integer, Integer>> channelUVMap = new LinkedHashMap<>();

	private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, Pair<IStackEncoder, IStackDecoder>> channelCoderMap = new LinkedHashMap<>();

	private static AIApi instance;
	// ----# Channel Maps #---- //

	private static final LinkedHashMap<Item, ItemStack> tunnelMap = new LinkedHashMap<>();

	public static AIApi staticInstance() {
		// Check not null
		if (instance == null)
		// Update instance
		{
			instance = new ApiInstance();
		}

		// Return instance
		return instance;
	}

	@Override
	public void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB, IStorageChannel chan) {

		TileMEPylon.addBlackHoleHandler(handlerClassA, chan);
		TileMEPylon.addWhiteHoleHandler(handlerClassB, chan);
	}

	@Override
	public void addChannelToServerFilterList(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite, Constructor<? extends IChannelWidget> widgetConstructor, Constructor<? extends FilteredServerPortHandler> handler, IStackConverter lambda, Pair<Integer, Integer> pair, Pair<IStackEncoder, IStackDecoder> coderPair) {

		channelSpriteMap.put(channel, sprite);
		channelConstructorMap.put(channel, widgetConstructor);
		channelStackConverterMap.put(channel, lambda);
		channelUVMap.put(channel, pair);
		channelCoderMap.put(channel, coderPair);
		channelHandlerMap.put(channel, handler);
	}

	@Override
	public ResourceLocation getSpriteFromChannel(IStorageChannel<? extends IAEStack<?>> channel) {

		return channelSpriteMap.get(channel);
	}

	@Override
	public int getSpriteU(IStorageChannel<? extends IAEStack<?>> channel) {

		return channelUVMap.get(channel).getLeft();
	}

	@Override
	public int getSpriteV(IStorageChannel<? extends IAEStack<?>> channel) {

		return channelUVMap.get(channel).getRight();
	}

	@Override
	public Constructor<? extends IChannelWidget> getWidgetFromChannel(IStorageChannel<? extends IAEStack<?>> chan) {

		return channelConstructorMap.get(chan);
	}

	@Override
	public IStackEncoder getStackEncoder(IStorageChannel<? extends IAEStack<?>> chan) {

		return channelCoderMap.get(chan).getLeft();
	}

	@Override
	public IStackDecoder getStackDecoder(IStorageChannel<? extends IAEStack<?>> chan) {
		return channelCoderMap.get(chan).getRight();
	}

	@Nullable
	@Override
	public IAEStack<?> getAEStackFromItemStack(IStorageChannel<? extends IAEStack<?>> chan, ItemStack itemStack) {

		try {
			return channelStackConverterMap.get(chan).convert(itemStack);
		} catch (IOException ignored) {
			throw new IllegalStateException("Unexpected error");
		}
	}

	@Override
	public Constructor<? extends FilteredServerPortHandler> getHandlerFromChannel(IStorageChannel<? extends IAEStack<?>> channel) {

		return channelHandlerMap.get(channel);
	}

	@Override
	public ItemStack getTunnelFromStack(ItemStack is) {
		return tunnelMap.get(is);
	}

	@Override
	public void addTunnelAsStack(Item item, ItemStack tunnel) {
		// Put map in stack
		tunnelMap.put(item, tunnel);
	}
}
