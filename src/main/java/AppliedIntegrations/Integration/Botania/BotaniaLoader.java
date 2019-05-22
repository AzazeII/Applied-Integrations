package AppliedIntegrations.Integration.Botania;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.ServerGUI.FilterSlots.WidgetManaSlot;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.grid.Mana.AEManaStack;
import AppliedIntegrations.grid.Mana.ManaStorageChannel;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortManaHandler;
import appeng.api.AEApi;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.botania.api.BotaniaAPI;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * @Author Azazell
 */
public class BotaniaLoader {
	public static void preInit() {

		AEApi.instance().storage().registerStorageChannel(IManaStorageChannel.class, new ManaStorageChannel());

		BotaniaEntryHelper helper = new BotaniaEntryHelper();
		helper.createPages();
	}

	public static void init() {
		// Register new mana layer, since !@#!@!#! BOTANIA HAS NO !@#!#@! CAPABILITY FOR MANA !!!!
		// Joking, botania is great mod `)
		AEApi.instance().partHelper().registerNewLayer(ManaLayer.class.getName(), ManaLayer.class.getName());
	}

	public static void postInit() {

	}

	public static void initRecipes() {

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_1k.getItem(), 1, 0), 100000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_4k.getItem(), 1, 0), 100000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_16k.getItem(), 1, 0), 200000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_64k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_64k.getItem(), 1, 0), 200000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_256k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_256k.getItem(), 1, 0), 200000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1024k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_1024k.getItem(), 1, 0), 200000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4096k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_4096k.getItem(), 1, 0), 300000);

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16384k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_16384k.getItem(), 1, 0), 300000);

		/*------------------------------------------Elven Trade------------------------------------------*/

		// Optionals
		Optional<ItemStack> fluixOptional = AEApi.instance().definitions().materials().fluixCrystal().maybeStack(1);
		Optional<ItemStack> pureFluixOptional = AEApi.instance().definitions().materials().purifiedFluixCrystal().maybeStack(1);

		if (fluixOptional.isPresent()) {
			ItemStack fluixStack = fluixOptional.get();
			BotaniaAPI.registerElvenTradeRecipe(new ItemStack(ItemEnum.encoriumVariants.get(0), 1, 0), fluixStack);
		}

		if (pureFluixOptional.isPresent()) {
			ItemStack fluixPureStack = pureFluixOptional.get();
			BotaniaAPI.registerElvenTradeRecipe(new ItemStack(ItemEnum.encoriumVariants.get(0), 1, 0), fluixPureStack);
		}
		/*------------------------------------------Elven Trade------------------------------------------*/

		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(1), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(0), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(2), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(1), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(3), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(2), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(4), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(3), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(5), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(4), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(6), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(5), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(7), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(6), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(8), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(7), 1, 0), 1000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(9), 1, 0), new ItemStack(ItemEnum.encoriumVariants.get(8), 1, 0), 1000);
	}

	public static void initChannelHandlers(AIApi instance) throws NoSuchMethodException {
		// Register mana channel handler and sprites
		instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class),
				// Sprite
				new ResourceLocation(AppliedIntegrations.modid, "textures/gui/channel_states.png"),

				// Constructor
				WidgetManaSlot.class.getConstructor(),

				// Handler
				FilteredMultiControllerPortManaHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, TileMultiControllerCore.class),

				// Converter and UV
				Utils::getManaFromItemStack, Pair.of(32, 0),

				// Encoder and decoder
				Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEManaStack::fromNBT));
	}

	public static boolean enableBotania() {
		return Loader.isModLoaded("botania") && AIConfig.enableManaFeatures;
	}
}
