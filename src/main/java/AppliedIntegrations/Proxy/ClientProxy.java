package AppliedIntegrations.Proxy;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Client.TextureEventManager;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.ServerGUI.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.ServerGUI.FilterSlots.WidgetFluidSlot;
import AppliedIntegrations.Gui.ServerGUI.FilterSlots.WidgetItemSlot;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Integration.AstralSorcery.AstralLoader;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Integration.Embers.EmberLoader;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Topology.WebServer.WebManager;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileMEPylonRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileMETurretRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileSingularityRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileWhiteHoleRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import AppliedIntegrations.tile.Server.Render.ServerRibRenderer;
import AppliedIntegrations.tile.Server.Render.ServerSecurityRenderer;
import AppliedIntegrations.tile.Server.TileServerRib;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import AppliedIntegrations.tile.Server.helpers.FilteredServerPortEnergyHandler;
import AppliedIntegrations.tile.Server.helpers.FilteredServerPortFluidHandler;
import AppliedIntegrations.tile.Server.helpers.FilteredServerPortItemHandler;
import appeng.api.AEApi;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.IAEFluidTank;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @Author Azazell
 */
public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register( this );
    }

    private void registerChannelSprites() throws NoSuchMethodException {
        // Get applied integrations api
        AIApi instance = Objects.requireNonNull(AIApi.instance());

        // Register channel'sprite pair
        instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class),
                // Sprite
                new ResourceLocation(AppliedIntegrations.modid, "textures/gui/channel_states.png"),

                // Constructor
                WidgetItemSlot.class.getConstructor(int.class, int.class),

                // Handler
                FilteredServerPortItemHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, IMEInventory.class),

                // Converter and UV
                (AEItemStack::fromItemStack), Pair.of(0, 0),

                // Encoder and decoder
                Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEItemStack::fromNBT)); // (1) Item channel

        instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class),
                // Sprite
                new ResourceLocation(AppliedIntegrations.modid, "textures/gui/channel_states.png"),

                // Constructor
                WidgetFluidSlot.class.getConstructor(IAEFluidTank.class, int.class, int.class, int.class, int.class, IWidgetHost.class),

                // Handler
                FilteredServerPortFluidHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, IMEInventory.class),

                // Converter and UV
                (stack) -> {
                    // Get stack
                    FluidStack fluidStack = FluidUtil.getFluidContained(stack);

                    // Check not null and meaningful
                    if (fluidStack == null || fluidStack.amount == 0 || fluidStack.getFluid() == null)
                        return null;

                    return AEFluidStack.fromFluidStack(fluidStack);
                }, Pair.of(16, 0),

                // Encoder and decoder
                Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEFluidStack::fromNBT)); // (2) Fluid channel

        instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class),
                // Sprite
                new ResourceLocation(AppliedIntegrations.modid, "textures/gui/channel_states.png"),

                // Constructor
                WidgetEnergySlot.class.getConstructor(IWidgetHost.class, int.class, int.class, int.class, boolean.class),

                // Handler
                FilteredServerPortEnergyHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, IMEInventory.class),

                // Converter and UV
                Utils::getEnergyStackFromItemStack, Pair.of(0, 16),

                // Encoder and decoder
                Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEEnergyStack::fromNBT)); // (3) Energy channel
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

        // Register texture manager to event bus
        FMLCommonHandler.instance().bus().register(new TextureEventManager());

        if (AIConfig.enableMEServer){
            // Register custom renderers
            ClientRegistry.bindTileEntitySpecialRenderer(TileServerRib.class, new ServerRibRenderer()); // (1)
            ClientRegistry.bindTileEntitySpecialRenderer(TileServerSecurity.class, new ServerSecurityRenderer()); // (2)
        }

        if (AIConfig.enableBlackHoleStorage) {
            // Register custom renderers
            ClientRegistry.bindTileEntitySpecialRenderer(TileBlackHole.class, new TileSingularityRenderer()); // (1)
            ClientRegistry.bindTileEntitySpecialRenderer(TileWhiteHole.class, new TileWhiteHoleRenderer()); // (2)
            ClientRegistry.bindTileEntitySpecialRenderer(TileMEPylon.class, new TileMEPylonRenderer()); // (3)
            ClientRegistry.bindTileEntitySpecialRenderer(TileMETurretFoundation.class, new TileMETurretRenderer()); // (4)
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void SidedInit(FMLInitializationEvent init) {
        ItemEnum.registerModels();

        BlocksEnum.registerModels();
        BlocksEnum.registerItemModels();

        try {
            registerChannelSprites();
        } catch (NoSuchMethodException ignored) {}

        // Check if web server enabled
        if(AIConfig.enableWebServer)
            // Init web server
            WebManager.init();

        if(Loader.isModLoaded("botania") && AIConfig.enableManaFeatures)
            BotaniaLoader.init();
        if(Loader.isModLoaded("embers") && AIConfig.enableEmberFeatures)
            EmberLoader.init();
        if(Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures)
            AstralLoader.init();
    }

    @Override
    public void SidedPostInit(){

    }
}

