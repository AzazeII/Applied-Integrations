package AppliedIntegrations.Proxy;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Integration.AstralSorcery.AstralLoader;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Integration.Embers.EmberLoader;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileMEPylonRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileMETurretRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileSingularityRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileWhiteHoleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
public class ClientProxy
        extends CommonProxy
{
    public ClientProxy()
    {
        MinecraftForge.EVENT_BUS.register( this );
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void SidedPreInit(){
        super.SidedPreInit();
        NetworkHandler.registerClientPackets();

        if(AIConfig.enableBlackHoleStorage) {
            // Register custom renderers
            ClientRegistry.bindTileEntitySpecialRenderer(TileBlackHole.class, new TileSingularityRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileWhiteHole.class, new TileWhiteHoleRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileMEPylon.class, new TileMEPylonRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileMETurretFoundation.class, new TileMETurretRenderer());
        }

        // State mapper for tile port
        /*StateMapperBase stateMapperPort = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return LogicBusBakedModel.variantTagPort;
            }
        };

        // State mapper for tile rib
        StateMapperBase stateMapperRib = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return LogicBusBakedModel.variantTagRib;
            }
        };

        ModelLoader.setCustomStateMapper(BlocksEnum.BLBRibs.b, stateMapperPort);
        ModelLoader.setCustomStateMapper(BlocksEnum.BLBPort.b, stateMapperRib);


        MinecraftForge.EVENT_BUS.register(LogicBusModelBakeEventHandler.instance);


        ModelResourceLocation itemRibLocation
                = new ModelResourceLocation(AppliedIntegrations.modid+":logic_bus/logic_ribs", "inventory");
        ModelLoader.setCustomModelResourceLocation(BlocksEnum.BLBRibs.itemBlock, 0, itemRibLocation);

        ModelResourceLocation itemPortLocation =
                new ModelResourceLocation(AppliedIntegrations.modid+":logic_bus/logic_port", "inventory");

        ModelLoader.setCustomModelResourceLocation(BlocksEnum.BLBPort.itemBlock, 0, itemPortLocation);*/
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void SidedInit(FMLInitializationEvent init) {
        ItemEnum.registerModels();

        BlocksEnum.registerModels();
        BlocksEnum.registerItemModels();
        //EntityEnum.registerRenderer();

        if(Loader.isModLoaded("botania") && AIConfig.enableManaFeatures)
            BotaniaLoader.init();
        if(Loader.isModLoaded("embers") && AIConfig.enablEmberFeatures)
            EmberLoader.init();
        if(Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures)
            AstralLoader.init();

    }

    @Override
    public void SidedPostInit(){

    }
}

