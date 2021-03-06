package AppliedIntegrations.Integration.Botania;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.MultiController.FilterSlots.WidgetManaSlot;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.grid.Mana.AEManaStack;
import AppliedIntegrations.grid.Mana.ManaStorageChannel;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortManaHandler;
import appeng.api.AEApi;
import appeng.api.features.IGrinderRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.botania.api.BotaniaAPI;

import javax.annotation.Nonnull;
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

		/*BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_256k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_256k.getItem(), 1, 0), 200000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1024k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_1024k.getItem(), 1, 0), 200000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4096k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_4096k.getItem(), 1, 0), 300000);
		BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16384k.getItem(), 1, 0), new ItemStack(ItemEnum.ENERGYSTORAGE_16384k.getItem(), 1, 0), 300000);*/

		/*------------------------------------------Elven Trade------------------------------------------*/

		// Optionals
		Optional<ItemStack> fluixOptional = AEApi.instance().definitions().materials().fluixCrystal().maybeStack(1);
		Optional<ItemStack> pureFluixOptional = AEApi.instance().definitions().materials().purifiedFluixCrystal().maybeStack(1);

		if (fluixOptional.isPresent()) {
			ItemStack fluixStack = fluixOptional.get();
			BotaniaAPI.registerElvenTradeRecipe(new ItemStack(ItemEnum.ITEMENCORIUMSEED.getItem(), 1, 0), fluixStack);
		}

		if (pureFluixOptional.isPresent()) {
			ItemStack fluixPureStack = pureFluixOptional.get();
			BotaniaAPI.registerElvenTradeRecipe(new ItemStack(ItemEnum.ITEMENCORIUMSEED.getItem(), 1, 0), fluixPureStack);
		}
		/*------------------------------------------Elven Trade------------------------------------------*/

		// Linkorium dust
		AEApi.instance().registries().grinder().addRecipe(new IGrinderRecipe() {
			@Nonnull
			@Override
			public ItemStack getInput() {
				return ItemEnum.ITEMENCORIUM.getStack();
			}

			@Nonnull
			@Override
			public ItemStack getOutput() {
				return ItemEnum.ITEMENCORIUMDUST.getStack();
			}

			@Nonnull
			@Override
			public Optional<ItemStack> getOptionalOutput() {
				return Optional.empty();
			}

			@Override
			public Optional<ItemStack> getSecondOptionalOutput() {
				return Optional.empty();
			}

			@Nonnull
			@Override
			public float getOptionalChance() {
				return 0;
			}

			@Override
			public float getSecondOptionalChance() {
				return 0;
			}

			@Override
			public int getRequiredTurns() {
				return 8;
			}
		});
	}

	public static void initChannelHandlers(AIApi instance) throws NoSuchMethodException {
		// Register mana channel handler and sprites
		instance.addChannelToServerFilterList(AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class),
				// Sprite
				new ResourceLocation(AppliedIntegrations.modid, "textures/gui/states.png"),

				// Constructor
				WidgetManaSlot.class.getConstructor(),

				// Handler
				FilteredMultiControllerPortManaHandler.class.getConstructor(LinkedHashMap.class, LinkedHashMap.class, TileMultiControllerCore.class),

				// Converter and UV
				(itemStack, world) -> Utils.getManaFromItemStack(itemStack), Pair.of(32, 0),

				// Encoder and decoder
				Pair.of((nbt, stack) -> stack.writeToNBT(nbt), AEManaStack::fromNBT));
	}

	public static boolean enableBotania() {
		return Loader.isModLoaded("botania") && AIConfig.enableManaFeatures;
	}
}
