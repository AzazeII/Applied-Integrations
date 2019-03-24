package AppliedIntegrations.Proxy;

import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Integration.AstralSorcery.AstralLoader;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Integration.Embers.EmberLoader;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import AppliedIntegrations.tile.Additions.TileMETurretTower;
import AppliedIntegrations.tile.Additions.storage.TileSingularity;
import AppliedIntegrations.tile.Additions.storage.TileWhiteHole;
import AppliedIntegrations.tile.Additions.render.TileMEPylonRenderer;
import AppliedIntegrations.tile.Additions.render.TileMETurretRenderer;
import AppliedIntegrations.tile.Additions.render.TileSingularityRenderer;
import AppliedIntegrations.tile.Additions.render.TileWhiteHoleRenderer;
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

        // Register custom renderers
        ClientRegistry.bindTileEntitySpecialRenderer(TileSingularity.class, new TileSingularityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWhiteHole.class, new TileWhiteHoleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMEPylon.class, new TileMEPylonRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMETurretTower.class, new TileMETurretRenderer());
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

        if(Loader.isModLoaded("botania"))
            BotaniaLoader.init();
        if(Loader.isModLoaded("embers"))
            EmberLoader.init();
        if(Loader.isModLoaded("astralsorcery"))
            AstralLoader.init();

    }

    @Override
    public void SidedPostInit(){

    }
}

