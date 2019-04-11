package AppliedIntegrations.Integration.Botania;

import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.grid.Mana.ManaStorageChannel;
import appeng.api.AEApi;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;

import java.util.Optional;

/**
 * @Author Azazell
 */
public class BotaniaLoader {
    public static void preInit(){
        AEApi.instance().storage().registerStorageChannel(IManaStorageChannel.class, new ManaStorageChannel());
        ItemEnum.registerBotaniaItems();

        BotaniaEntryHelper helper = new BotaniaEntryHelper();
        helper.createPages();


    }

    public static void init(){
        // Register new mana layer, as !@#!@!#! BOTANIA HAS NO !@#!#@! CAPABILITY FOR MANA !!!!
        // Joking, botania is great mod `)
        AEApi.instance().partHelper().registerNewLayer(ManaLayer.class.getName(), ManaLayer.class.getName());

        ItemEnum.registerManaItemsModels();
    }

    public static void postInit() {

    }

    public static void initRecipes() {
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_1k.getItem(), 1, 0), 100000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_4k.getItem(), 1, 0), 100000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_16k.getItem(), 1, 0), 200000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_64k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_64k.getItem(), 1, 0), 200000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_256k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_256k.getItem(), 1, 0), 200000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1024k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_1024k.getItem(), 1, 0), 200000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4096k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_4096k.getItem(), 1, 0), 300000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16384k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_16384k.getItem(), 1, 0), 300000);

        /*------------------------------------------Elven Trade------------------------------------------*/

        // Optionals
        Optional<ItemStack> fluixOptional = AEApi.instance().definitions().materials().fluixCrystal().maybeStack(1);
        Optional<ItemStack> pureFluixOptional = AEApi.instance().definitions().materials().purifiedFluixCrystal().maybeStack(1);

        if(fluixOptional.isPresent()){
            ItemStack fluixStack = fluixOptional.get();
            BotaniaAPI.registerElvenTradeRecipe(new ItemStack(ItemEnum.encoriumVariants.get(0), 1, 0), fluixStack);
        }

        if(pureFluixOptional.isPresent()) {
            ItemStack fluixPureStack = pureFluixOptional.get();
            BotaniaAPI.registerElvenTradeRecipe(new ItemStack(ItemEnum.encoriumVariants.get(0), 1, 0), fluixPureStack);
        }
        /*------------------------------------------Elven Trade------------------------------------------*/

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(1), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(0), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(2), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(1), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(3), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(2), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(4), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(3), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(5), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(4), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(6), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(5), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(7), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(6), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(8), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(7), 1, 0), 1000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.encoriumVariants.get(9), 1,0),
                new ItemStack(ItemEnum.encoriumVariants.get(8), 1, 0), 1000);

    }
}
