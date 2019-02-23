package AppliedIntegrations.Integration.Botania;

import AppliedIntegrations.API.Botania.IManaChannel;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.grid.Mana.ManaChannel;
import appeng.api.AEApi;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;

public class BotaniaLoader {
    public static void preInit(){
        AEApi.instance().storage().registerStorageChannel(IManaChannel.class, new ManaChannel());
        ItemEnum.registerBotaniaItems();
    }

    public static void init(){
        ItemEnum.registerManaItemsModels();
    }

    public static void initRecipes() {
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_1k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_4k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_16k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_64k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_64k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_256k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_256k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_1024k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_1024k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_4096k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_4096k.getItem(), 1, 0), 10000);

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ItemEnum.MANASTORAGE_16384k.getItem(), 1, 0),
                new ItemStack(ItemEnum.ENERGYSTORAGE_16384k.getItem(), 1, 0), 10000);
    }
}
