package AppliedIntegrations.API;

import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.Additions.storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.Additions.storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @Author Azazell
 */
public abstract class AIApi {
	private static AIApi api = new AIApi() {
		// Map of all storage channels translated from ae stacks
		private LinkedHashMap<Class<? extends IAEStack>, IStorageChannel> StackTranslationMap =
				new LinkedHashMap<>();

		public LinkedHashMap<IStorageChannel, Class<? extends BlackHoleSingularityInventoryHandler<?>>> blackHoleHandlers = new LinkedHashMap<>();
		private LinkedHashMap<IStorageChannel, Class<? extends WhiteHoleSingularityInventoryHandler<?>>> whiteHoleHandlers = new LinkedHashMap<>();


		@Nullable
		@Override
		public IStorageChannel getChannelFromType(Class stack) {
			return StackTranslationMap.get(stack);
		}

		@Override
		public void addStorageChannelToPylon(Class<? extends IAEStack> stack, IStorageChannel chan) {
			StackTranslationMap.put(stack, chan);
		}

		@Override
		public boolean channelHandled(IStorageChannel<?> channel) {
			return StackTranslationMap.containsValue(channel);
		}

		@Override
		public LinkedHashMap<IStorageChannel, IMEInventoryHandler> getNewBlackHoleHandlerList() {
			// Create list
			LinkedHashMap<IStorageChannel, IMEInventoryHandler> handlers = new LinkedHashMap<>();

			try {
				// Fill it
				for (IStorageChannel key : blackHoleHandlers.keySet()) {
					handlers.put(key, blackHoleHandlers.get(key).newInstance());
				}
			}catch (InstantiationException | IllegalAccessException e){
				AILog.info(e.getMessage());
			}

			return handlers;
		}

		@Override
		public LinkedHashMap<IStorageChannel, IMEInventoryHandler> getNewWhiteHoleHandlerList() {
			// Create list
			LinkedHashMap<IStorageChannel, IMEInventoryHandler> handlers = new LinkedHashMap<>();

			try {
				// Fill it
				for (IStorageChannel key : whiteHoleHandlers.keySet()) {
					handlers.put(key, whiteHoleHandlers.get(key).newInstance());
				}
			}catch (InstantiationException | IllegalAccessException e){
				AILog.info(e.getMessage());
			}

			return handlers;
		}

		@Override
		public void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
										  IStorageChannel chan) {
			AILog.info("Added new ME pylon chain on channel: " + chan.toString());
			AILog.info("	Black hole handler: " + handlerClassA.toString());
			AILog.info("	White hole handler: " + handlerClassB.toString());
			blackHoleHandlers.put(chan, handlerClassA);
			whiteHoleHandlers.put(chan, handlerClassB);
		}

		@Override
		public Collection<Class<? extends BlackHoleSingularityInventoryHandler<?>>> getBlackHoleHandlerClasses() {
			return blackHoleHandlers.values();
		}

		@Override
		public Collection<Class<? extends WhiteHoleSingularityInventoryHandler<?>>> getWhiteHoleHandlerClasses() {
			return whiteHoleHandlers.values();
		}
	};

	@Nonnull
	public static AIApi instance()
	{
		return api;
	}

	@Nullable
	public abstract IStorageChannel getChannelFromType(Class stack);

	public abstract void addStorageChannelToPylon(Class<? extends IAEStack> stack, IStorageChannel chan);

	public abstract boolean channelHandled(IStorageChannel<?> channel);

    public abstract LinkedHashMap<IStorageChannel, IMEInventoryHandler> getNewBlackHoleHandlerList();
	public abstract LinkedHashMap<IStorageChannel, IMEInventoryHandler> getNewWhiteHoleHandlerList();

    public abstract void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
											   IStorageChannel chan);

	public abstract Collection<Class<? extends BlackHoleSingularityInventoryHandler<?>>> getBlackHoleHandlerClasses();
	public abstract Collection<Class<? extends WhiteHoleSingularityInventoryHandler<?>>> getWhiteHoleHandlerClasses();

}
