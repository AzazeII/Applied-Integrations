package AppliedIntegrations.API;

import AppliedIntegrations.Parts.AIPart;
import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public abstract class AIApi {
	protected static AIApi api = new AIApi() {
		// Map of all storage channels translated from ae stacks
		private LinkedHashMap<Class<? extends IAEStack>, IStorageChannel> StackTranslationMap =
				new LinkedHashMap<>();

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
	};

	@Nullable
	public static AIApi instance()
	{
		return api;
	}

	@Nullable
	public abstract IStorageChannel getChannelFromType(Class stack);

	public abstract void addStorageChannelToPylon(Class<? extends IAEStack> stack, IStorageChannel chan);

	public abstract boolean channelHandled(IStorageChannel<?> channel);
}
