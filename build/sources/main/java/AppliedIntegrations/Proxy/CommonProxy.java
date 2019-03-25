package AppliedIntegrations.Proxy;

import AppliedIntegrations.API.AIApi;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Integration.AstralSorcery.AstralLoader;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Integration.Embers.EmberLoader;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.tile.Additions.storage.helpers.impl.*;
import AppliedIntegrations.tile.TileEnum;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.grid.EnergyStorageChannel;
import appeng.api.AEApi;
import appeng.api.movable.IMovableRegistry;
import appeng.api.recipes.IRecipeLoader;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.*;

/**
 * @Author Azazell
 */
public class CommonProxy
{
    private class ExternalRecipeLoader implements IRecipeLoader {

        @Override
        public BufferedReader getFile(String path) throws Exception {
            return new BufferedReader(new FileReader(new File(path)));
        }
    }

    public void SidedPreInit(){
        ItemEnum.register();
        BlocksEnum.register();
            //PartEnum.registerAEModels();

        NetworkHandler.registerServerPackets();

        // Register channel
        AEApi.instance().storage().registerStorageChannel(IEnergyStorageChannel.class, new EnergyStorageChannel());

        if(Loader.isModLoaded("botania"))
            BotaniaLoader.preInit();
        if(Loader.isModLoaded("embers"))
            EmberLoader.preInit();
        if(Loader.isModLoaded("astralsorcery"))
            AstralLoader.preInit();
    }

    public void SidedInit(FMLInitializationEvent init){
        // Add storage channels
        AIApi.instance().addStorageChannelToPylon(IAEItemStack.class,
                AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

        AIApi.instance().addStorageChannelToPylon(IAEFluidStack.class,
                AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));

        AIApi.instance().addStorageChannelToPylon(IAEEnergyStack.class,
                AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));

        // Add handler classes
        addAIHandlers();
    }

    public void SidedPostInit(){

    }

    private class InternalRecipeLoader implements IRecipeLoader {

        @Override
        public BufferedReader getFile(String path) throws Exception {
            InputStream resourceAsStream = getClass().getResourceAsStream("/assets/appliedintegrations/recipes/" + path);
            InputStreamReader reader = new InputStreamReader(resourceAsStream, "UTF-8");
            return new BufferedReader(reader);
        }
    }
    /**
     * Adds tile entities to the AE2 SpatialIO whitelist
     */
    public void registerSpatialIOMovables()
    {
        IMovableRegistry movableRegistry = AEApi.instance().registries().movable();
        for( TileEnum tile : TileEnum.values() )
        {
            movableRegistry.whiteListTileEntity( tile.clazz );
        }
    }
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    /**
     * Adds handlers, to ME pylon's handlers list
     */
    @SuppressWarnings("unchecked")
    private void addAIHandlers() {
        // Add item handler
        AIApi.instance().addHandlersForMEPylon(BlackHoleItemHandler.class, WhiteHoleItemHandler.class, AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

        // Add fluid handler
        AIApi.instance().addHandlersForMEPylon(BlackHoleFluidHandler.class, WhiteHoleFluidHandler.class, AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));

        // Add energy handler
        AIApi.instance().addHandlersForMEPylon(BlackHoleEnergyHandler.class, WhiteHoleEnergyHandler.class, AEApi.instance().storage().getStorageChannel(EnergyStorageChannel.class));

        // Add mana handler
        AIApi.instance().addHandlersForMEPylon(BlackHoleManaHandler.class, WhiteHoleManaHandler.class, AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class));
    }
}