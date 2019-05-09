package AppliedIntegrations;

import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.EnergyRepo;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public class ApiInstance extends AIApi {
    private static AIApi instance;
    private static final LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, ResourceLocation> channelSpriteMap = new LinkedHashMap<>();

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
    public void addChannelSprite(IStorageChannel<? extends IAEStack<?>> channel, ResourceLocation sprite) {
        channelSpriteMap.put(channel, sprite);
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
