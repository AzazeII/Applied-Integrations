package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Integration.AstralSorcery.IAstralIntegrated;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.Items.Botania.*;
import AppliedIntegrations.Items.Part.Energy.*;
import AppliedIntegrations.Items.Part.Mana.ItemPartManaInterface;
import AppliedIntegrations.Items.Part.Mana.ItemPartManaStorageBus;
import AppliedIntegrations.Items.Part.P2P.ItemPartP2PEmber;
import AppliedIntegrations.Items.Part.P2P.ItemPartP2PStarlight;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCasing;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCell;
import AppliedIntegrations.Items.StorageCells.EnergyStorageComponent;
import AppliedIntegrations.Items.StorageCells.ManaStorageCell;
import AppliedIntegrations.Items.multiTool.AdvancedNetworkTool;
import AppliedIntegrations.AIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;

/**
 * @Author Azazell
 */
public enum ItemEnum {
    ITEMPARTIMPORT(new ItemPartEnergyImport("energyImportPartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTEXPORT(new ItemPartEnergyExport("energyExportPartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTSTORAGE(new ItemPartEnergyStorage("energyStoragePartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTINTERFACE( new ItemPartEnergyInterface("energyInterfacePartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTMONITOR( new ItemPartEnergyStorageMonitor("energyMonitorPartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTTERMINAL( new ItemPartEnergyTerminal("energyTerminalPartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTANNIHILATION( new ItemPartEnergyAnnihilation( "energyAnnihilationPartItem"), AIConfig.enableEnergyFeatures),
    ITEMPARTFORMATION(new ItemPartEnergyFormation("energyFormationPartItem"), AIConfig.enableEnergyFeatures),

    ITEMP2PStarlight(new ItemPartP2PStarlight("starlightP2PPartItem"), AIConfig.enableStarlightFeatures),
    ITEMP2PEMBER(new ItemPartP2PEmber("emberP2PPartItem"), AIConfig.enablEmberFeatures),

    ITEMMANAPARTINTERFACE(new ItemPartManaInterface("manaInterfacePartItem"), AIConfig.enableManaFeatures),
    ITEMMANAPARTSTORAGEBUS(new ItemPartManaStorageBus("manaStoragePartItem"), AIConfig.enableManaFeatures),

    ITEMMANAWIRELESSMIRROR(new MEManaMirror("me_mana_mirror"), AIConfig.enableManaFeatures),
    ITEMMANAWIRELESSRING(new MEManaRing("me_mana_ring"), AIConfig.enableManaFeatures),
    ITEMMANAWIRELESSGREATRING(new MEGreaterManaRing("me_greater_mana_ring"), AIConfig.enableManaFeatures),

    ITEMENERGYWIRELESSTERMINAL(new ItemWirelessTerminal("wireless_energy_terminal"), AIConfig.enableEnergyFeatures),

    CHAOSMANIPULATOR( new AdvancedNetworkTool("advancedWrench"), true),
    MEGRAPHTOOL(new GraphTool("graphTool"), AIConfig.enableWebServer),

    ENERGYSTORAGE_1k( new EnergyStorageCell("EnergyStorageCell_1k", 1024), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_4k( new EnergyStorageCell("EnergyStorageCell_4k", 4096), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_16k( new EnergyStorageCell("EnergyStorageCell_16k", 16384), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_64k( new EnergyStorageCell("EnergyStorageCell_64k", 65536), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_256k( new EnergyStorageCell("EnergyStorageCell_256k", 262144), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_1024k(new EnergyStorageCell("EnergyStorageCell_1024k", 1048576), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_4096k( new EnergyStorageCell("EnergyStorageCell_4096k", 4194304), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGE_16384k( new EnergyStorageCell("EnergyStorageCell_16384k", 16777216), AIConfig.enableEnergyFeatures),

    MANASTORAGE_1k( new ManaStorageCell("ManaStorageCell_1k", 1024), AIConfig.enableManaFeatures),
    MANASTORAGE_4k( new ManaStorageCell("ManaStorageCell_4k", 4096), AIConfig.enableManaFeatures),
    MANASTORAGE_16k( new ManaStorageCell("ManaStorageCell_16k", 16384), AIConfig.enableManaFeatures),
    MANASTORAGE_64k( new ManaStorageCell("ManaStorageCell_64k", 65536), AIConfig.enableManaFeatures),
    MANASTORAGE_256k( new ManaStorageCell("ManaStorageCell_256k", 262144), AIConfig.enableManaFeatures),
    MANASTORAGE_1024k(new ManaStorageCell("ManaStorageCell_1024k", 1048576), AIConfig.enableManaFeatures),
    MANASTORAGE_4096k( new ManaStorageCell("ManaStorageCell_4096k", 4194304), AIConfig.enableManaFeatures),
    MANASTORAGE_16384k( new ManaStorageCell("ManaStorageCell_16384k", 16777216), AIConfig.enableManaFeatures),

    MANAANNIHILATIONCORE(new ManaAnnihilationCore("mana_annihilation_core"), AIConfig.enableManaFeatures),
    MANAFORMATIONCORE(new ManaFormationCore("mana_formation_core"), AIConfig.enableManaFeatures),

    ENERGYSTORAGECASING(new EnergyStorageCasing(), AIConfig.enableEnergyFeatures),

    ENERGYSTORAGECOMPONENT_1k(new EnergyStorageComponent("EnergyStorageComponent_1k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_4k(new EnergyStorageComponent("EnergyStorageComponent_4k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_16k(new EnergyStorageComponent("EnergyStorageComponent_16k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_64k(new EnergyStorageComponent("EnergyStorageComponent_64k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_256k(new EnergyStorageComponent("EnergyStorageComponent_256k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_1024k(new EnergyStorageComponent("EnergyStorageComponent_1024k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_4096k(new EnergyStorageComponent("EnergyStorageComponent_4096k"), AIConfig.enableEnergyFeatures),
    ENERGYSTORAGECOMPONENT_16384k(new EnergyStorageComponent("EnergyStorageComponent_16384k"), AIConfig.enableEnergyFeatures);
    public static LinkedList<MaterialEncorium> encoriumVariants = new LinkedList<>();

    private boolean enabled;
    private Item item;

    ItemEnum(AIItemRegistrable _item, boolean enabled) {
        this(_item, AppliedIntegrations.AI, enabled);
    }

    ItemEnum(Item _item, boolean enabled){
        this.item = _item;
        this.enabled = enabled;
    }

    ItemEnum(AIItemRegistrable _item, CreativeTabs creativeTab, boolean enabled) {
        this.item = _item;
        this.item.setCreativeTab(creativeTab);
        this.enabled = enabled;
    }

    public ItemStack getDamagedStack(int damage) {
        return this.getDMGStack( damage, 1 );
    }
    public ItemStack getDMGStack( final int damageValue, final int size )
    {
        return new ItemStack( this.item, size, damageValue );
    }

    public Item getItem() {
        return this.item;
    }

    public static void register() {
        for(ItemEnum itemEnum : values()){
            // Register only that items, which not **require botania or ember or AS as dependency**
            if(!IntegrationsHelper.instance.isObjectIntegrated(itemEnum.item) && itemEnum.enabled) {
                ForgeRegistries.ITEMS.register(itemEnum.item);
            }
        }
    }

    @Optional.Method(modid = "astralsorcery")
    public static void registerAstralItems() {
        for(ItemEnum itemEnum : values()){
            // Register only that items, which **require AS as dependency**
            if(itemEnum.item instanceof IAstralIntegrated && itemEnum.enabled) {
                ForgeRegistries.ITEMS.register(itemEnum.item);
            }
        }
    }

    @Optional.Method(modid = "botania")
    public static void registerBotaniaItems(){
        for(ItemEnum itemEnum : values()){
            if(itemEnum.item instanceof MaterialEncorium)
                return;
            // Register only that items, which **require botania as dependency**
            if(itemEnum.item instanceof IBotaniaIntegrated && itemEnum.enabled) {
                ForgeRegistries.ITEMS.register(itemEnum.item);
            }
        }

        if(AIConfig.enableManaFeatures) {
            for (int i = 0; i < 10; i++) {
                MaterialEncorium mat = new MaterialEncorium("encorium" + i, (i + 1) * 10 + "%");

                if (i != 0)
                    mat.setCreativeTab(null);

                ForgeRegistries.ITEMS.register(mat);
                encoriumVariants.add(mat);
            }
        }
    }

    @Optional.Method(modid = "embers")
    public static void registerEmbersItems() {
        for(ItemEnum itemEnum : values()){
            // Register only that items, which **require embers as dependency**
            if(itemEnum.item instanceof IEmberIntegrated && itemEnum.enabled) {
                ForgeRegistries.ITEMS.register(itemEnum.item);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for(ItemEnum item : values()) {
            if(!(item.item instanceof IBotaniaIntegrated)  && item.enabled) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                }
            }
        }
     }
    @Optional.Method(modid = "botania")
    @SideOnly(Side.CLIENT)
    public static void registerManaItemsModels() {
        for(ItemEnum item : values()) {
            if(item.item instanceof IBotaniaIntegrated && item.enabled ) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                }
            }
        }

        if(AIConfig.enableManaFeatures) {
            for (MaterialEncorium mat : encoriumVariants) {
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mat, 0, new ModelResourceLocation(mat.getRegistryName(), "inventory"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerAstralItemModels() {
        for(ItemEnum item : values()) {
            if(item.item instanceof IAstralIntegrated && item.enabled) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                }
            }
        }
    }

    @Optional.Method(modid = "embers")
    @SideOnly(Side.CLIENT)
    public static void registerEmbersItemModels() {
        for(ItemEnum item : values()) {
            if(item.item instanceof IEmberIntegrated && item.enabled) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                }
            }
        }
    }
}
