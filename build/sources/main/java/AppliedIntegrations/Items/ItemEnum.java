package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.Items.Botania.*;
import AppliedIntegrations.Items.Part.Energy.*;
import AppliedIntegrations.Items.Part.Mana.ItemPartManaInterface;
import AppliedIntegrations.Items.Part.Mana.ItemPartManaStorageBus;
import AppliedIntegrations.Items.Part.P2P.ItemPartP2PEmber;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCasing;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCell;
import AppliedIntegrations.Items.StorageCells.EnergyStorageComponent;
import AppliedIntegrations.Items.StorageCells.ManaStorageCell;
import AppliedIntegrations.Items.multiTool.toolChaosManipulator;
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
import java.util.Vector;

/**
 * @Author Azazell
 */
public enum ItemEnum {
    ENERGYANNIHILATIONCORE(new AIItemRegistrable("annihilation_core")),
    ENERGYFORMATIONCORE(new AIItemRegistrable("formation_core")),

    ITEMPARTIMPORT(new ItemPartEnergyImport("energyImportPartItem")),
    ITEMPARTEXPORT(new ItemPartEnergyExport("energyExportPartItem")),
    ITEMPARTSTORAGE(new ItemPartEnergyStorage("energyStoragePartItem")),
    ITEMPARTINTERFACE( new ItemPartEnergyInterface("energyInterfacePartItem")),
    ITEMPARTMONITOR( new ItemPartEnergyStorageMonitor("energyMonitorPartItem")),
    ITEMPARTTERMINAL( new ItemPartEnergyTerminal("energyTerminalPartItem")),
    ITEMPARTANNIHILATION( new ItemPartEnergyAnnihilation( "energyAnnihilationPartItem")),
    ITEMPARTFORMATION(new ItemPartEnergyFormation("energyFormationPartItem")),

    ITEMP2PEMBER(new ItemPartP2PEmber("emberP2PPartItem")),

    ITEMMANAPARTINTERFACE(new ItemPartManaInterface("manaInterfacePartItem")),
    ITEMMANAPARTSTORAGEBUS(new ItemPartManaStorageBus("manaStoragePartItem")),

    ITEMMANAWIRELESSMIRROR(new MEManaMirror("me_mana_mirror")),
    ITEMMANAWIRELESSRING(new MEManaRing("me_mana_ring")),
    ITEMMANAWIRELESSGREATRING(new MEGreaterManaRing("me_greater_mana_ring")),

    ITEMENERGYWIRELESSTERMINAL(new itemWirelessTerminal("wireless_energy_terminal")),
    CHAOSMANIPULATOR( new toolChaosManipulator()),
    ENERGYSTORAGE_1k( new EnergyStorageCell("EnergyStorageCell_1k", 1024)),
    ENERGYSTORAGE_4k( new EnergyStorageCell("EnergyStorageCell_4k", 4096)),
    ENERGYSTORAGE_16k( new EnergyStorageCell("EnergyStorageCell_16k", 16384)),
    ENERGYSTORAGE_64k( new EnergyStorageCell("EnergyStorageCell_64k", 65536)),
    ENERGYSTORAGE_256k( new EnergyStorageCell("EnergyStorageCell_256k", 262144)),
    ENERGYSTORAGE_1024k(new EnergyStorageCell("EnergyStorageCell_1024k", 1048576)),
    ENERGYSTORAGE_4096k( new EnergyStorageCell("EnergyStorageCell_4096k", 4194304)),
    ENERGYSTORAGE_16384k( new EnergyStorageCell("EnergyStorageCell_16384k", 16777216)),

    MANASTORAGE_1k( new ManaStorageCell("ManaStorageCell_1k", 1024)),
    MANASTORAGE_4k( new ManaStorageCell("ManaStorageCell_4k", 4096)),
    MANASTORAGE_16k( new ManaStorageCell("ManaStorageCell_16k", 16384)),
    MANASTORAGE_64k( new ManaStorageCell("ManaStorageCell_64k", 65536)),
    MANASTORAGE_256k( new ManaStorageCell("ManaStorageCell_256k", 262144)),
    MANASTORAGE_1024k(new ManaStorageCell("ManaStorageCell_1024k", 1048576)),
    MANASTORAGE_4096k( new ManaStorageCell("ManaStorageCell_4096k", 4194304)),
    MANASTORAGE_16384k( new ManaStorageCell("ManaStorageCell_16384k", 16777216)),

    MANAANNIHILATIONCORE(new ManaAnnihilationCore("mana_annihilation_core")),
    MANAFORMATIONCORE(new ManaFormationCore("mana_formation_core")),

    ENERGYSTORAGECASING(new EnergyStorageCasing()),

    ENERGYSTORAGECOMPONENT_1k(new EnergyStorageComponent("EnergyStorageComponent_1k")),
    ENERGYSTORAGECOMPONENT_4k(new EnergyStorageComponent("EnergyStorageComponent_4k")),
    ENERGYSTORAGECOMPONENT_16k(new EnergyStorageComponent("EnergyStorageComponent_16k")),
    ENERGYSTORAGECOMPONENT_64k(new EnergyStorageComponent("EnergyStorageComponent_64k")),
    ENERGYSTORAGECOMPONENT_256k(new EnergyStorageComponent("EnergyStorageComponent_256k")),
    ENERGYSTORAGECOMPONENT_1024k(new EnergyStorageComponent("EnergyStorageComponent_1024k")),
    ENERGYSTORAGECOMPONENT_4096k(new EnergyStorageComponent("EnergyStorageComponent_4096k")),
    ENERGYSTORAGECOMPONENT_16384k(new EnergyStorageComponent("EnergyStorageComponent_16384k"));
    public static LinkedList<MaterialEncorium> encoriumVariants = new LinkedList<>();

    private Item item;

    ItemEnum( AIItemRegistrable _item) {
        this(_item, AppliedIntegrations.AI);
    }

    ItemEnum(Item _item){
        this.item = _item;
    }

    ItemEnum(AIItemRegistrable _item, CreativeTabs creativeTab) {
        this.item = _item;
        this.item.setCreativeTab(creativeTab);
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
            // Register only that items, which not **require botania as dependency**
            if(!IntegrationsHelper.instance.isObjectIntegrated(itemEnum.item)) {
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
            if(itemEnum.item instanceof IBotaniaIntegrated) {
                ForgeRegistries.ITEMS.register(itemEnum.item);
            }
        }

        for(int i = 0; i < 10; i++){
            MaterialEncorium mat = new MaterialEncorium("encorium"+i, (i+1)*10+"%");

            if(i != 0)
                mat.setCreativeTab(null);

            ForgeRegistries.ITEMS.register(mat);
            encoriumVariants.add(mat);
        }
    }

    @Optional.Method(modid = "embers")
    public static void registerEmbersItems() {
        for(ItemEnum itemEnum : values()){
            // Register only that items, which not **require botania as dependency**
            if(itemEnum.item instanceof IEmberIntegrated) {
                ForgeRegistries.ITEMS.register(itemEnum.item);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for(ItemEnum item : values()) {
            if(!(item.item instanceof IBotaniaIntegrated)) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                } else if (item.item instanceof toolChaosManipulator) {
                    toolChaosManipulator tCM = (toolChaosManipulator) item.item;
                    tCM.registerModel();
                }
            }
        }
     }
    @Optional.Method(modid = "botania")
    @SideOnly(Side.CLIENT)
    public static void registerManaItemsModels() {
        for(ItemEnum item : values()) {
            if(item.item instanceof IBotaniaIntegrated ) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                }
            }
        }

        for(MaterialEncorium mat : encoriumVariants){
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mat, 0, new ModelResourceLocation(mat.getRegistryName(), "inventory"));
        }
    }

    @Optional.Method(modid = "embers")
    @SideOnly(Side.CLIENT)
    public static void registerEmbersItemsModels() {
        for(ItemEnum item : values()) {
            if(item.item instanceof IEmberIntegrated ) {
                if (item.item instanceof AIItemRegistrable) {
                    AIItemRegistrable registrableItem = (AIItemRegistrable) item.item;
                    registrableItem.registerModel();
                }
            }
        }
    }
}
