package AppliedIntegrations;

import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.EnergyRepo;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import jdk.nashorn.internal.runtime.ScriptObject;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public class ApiInstance extends AIApi {
    private static AIApi instance;
    private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, ResourceLocation> channelSpriteMap = new LinkedHashMap<>();
    private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, Constructor<? extends IChannelWidget>> channelConstructorMap = new LinkedHashMap<>();

    @Override
    public void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
                                      IStorageChannel chan) {
        TileMEPylon.addBlackHoleHandler(handlerClassA, chan);
        TileMEPylon.addWhiteHoleHandler(handlerClassB, chan);
    }

    @Override
    public ResourceLocation getSpriteFromChannel(IStorageChannel<? extends IAEStack<?>> channel) {
        return channelSpriteMap.get(channel);
    }

    @Override
    public Constructor<? extends IChannelWidget> getWidgetFromChannel(IStorageChannel<? extends IAEStack<?>> chan) {
        return channelConstructorMap.get(chan);
    }

    @Override
    public void addChannelToServerFilterList(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite, Constructor<? extends IChannelWidget> widgetConstructor) {
        channelSpriteMap.put(channel, sprite);
        channelConstructorMap.put(channel, widgetConstructor);
    }

    public static AIApi staticInstance() {
        // Check not null
        if(instance == null)
            // Update instance
            instance = new ApiInstance();

        // Return instance
        return instance;
    }
}
