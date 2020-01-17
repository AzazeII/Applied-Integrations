package AppliedIntegrations.Proxy;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Client.TextureEventManager;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetEnergySlot;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetFluidSlot;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetItemSlot;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Topology.WebServer.WebManager;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileBlackHoleRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileMEPylonRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileMETurretRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.render.TileWhiteHoleRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import AppliedIntegrations.tile.MultiController.Render.MultiControllerRibRenderer;
import AppliedIntegrations.tile.MultiController.Render.MultiControllerSecurityRenderer;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import AppliedIntegrations.tile.MultiController.TileMultiControllerRib;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortEnergyHandler;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortFluidHandler;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortItemHandler;
import appeng.api.AEApi;
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

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void SidedPreInit() {
		super.SidedPreInit();

		FMLCommonHandler.instance().bus().register(new TextureEventManager());

		if (AIConfig.enableMEServer) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileMultiControllerRib.class, new MultiControllerRibRenderer()); // (1)
			ClientRegistry.bindTileEntitySpecialRenderer(TileMultiControllerTerminal.class, new MultiControllerSecurityRenderer()); // (2)
		}

		if (AIConfig.enableBlackHoleStorage) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileBlackHole.class, new TileBlackHoleRenderer()); // (1)
			ClientRegistry.bindTileEntitySpecialRenderer(TileWhiteHole.class, new TileWhiteHoleRenderer()); // (2)
			ClientRegistry.bindTileEntitySpecialRenderer(TileMEPylon.class, new TileMEPylonRenderer()); // (3)
			ClientRegistry.bindTileEntitySpecialRenderer(TileMETurretFoundation.class, new TileMETurretRenderer()); // (4)
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void SidedInit(FMLInitializationEvent init) {
		super.SidedInit(init);

		ItemEnum.registerModels();

		BlocksEnum.registerModels();
		BlocksEnum.registerItemModels();

		try {
			registerChannelSprites();
		} catch (NoSuchMethodException ignored) {
		}

		if (AIConfig.enableWebServer) {
			// Enables web serer
			WebManager.init();
		}
	}

	private void registerChannelSprites() throws NoSuchMethodException {
		// Get applied integrations api
		AIApi instance = Objects.requireNonNull(AIApi.instance());

		// Register channel'sprite pair
		instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class),
				new ResourceLocation(AppliedIntegrations.modid, "textures/gui/states.png"),
				WidgetItemSlot.class.getConstructor(int.class, int.class),
				FilteredMultiControllerPortItemHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, TileMultiControllerCore.class),
				((stack1, world) -> AEItemStack.fromItemStack(stack1)), Pair.of(0, 0),
				Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEItemStack::fromNBT)); // (1) Item channel

		instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class),
				new ResourceLocation(AppliedIntegrations.modid, "textures/gui/states.png"),
				WidgetFluidSlot.class.getConstructor(IAEFluidTank.class, int.class, int.class, int.class, int.class, IWidgetHost.class),
				FilteredMultiControllerPortFluidHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, TileMultiControllerCore.class),
				(stack, world) -> {
					FluidStack fluidStack = FluidUtil.getFluidContained(stack);
					if (fluidStack == null || fluidStack.amount == 0 || fluidStack.getFluid() == null) {
						return null;
					}

					return AEFluidStack.fromFluidStack(fluidStack);
				}, Pair.of(16, 0),

				Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEFluidStack::fromNBT)); // (2) Fluid channel

		instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class),
				new ResourceLocation(AppliedIntegrations.modid, "textures/gui/states.png"),
				WidgetEnergySlot.class.getConstructor(IWidgetHost.class, int.class, int.class, int.class, boolean.class),
				FilteredMultiControllerPortEnergyHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, TileMultiControllerCore.class),
				Utils::getEnergyStackFromItemStack, Pair.of(0, 16),
				Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEEnergyStack::fromNBT)); // (3) Energy channel

		// Check if botania is loaded and integrations is enabled
		if (Loader.isModLoaded("botania") && AIConfig.enableManaFeatures) {
			// (4) Mana channel
			BotaniaLoader.initChannelHandlers(instance);
		}
	}

	@Override
	public void SidedPostInit() {

	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
	}
}

