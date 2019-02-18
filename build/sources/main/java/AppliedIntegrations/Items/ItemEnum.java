package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.Parts.*;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCasing;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCell;
import AppliedIntegrations.Items.StorageCells.EnergyStorageComponent;
import AppliedIntegrations.Items.multiTool.toolChaosManipulator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @Author Azazell
 */
public enum ItemEnum {
    ENERGYANNIHILATIONCORE(new AIItemRegistrable("annihilation_core")),
    ENERGYFORMATIONCORE(new AIItemRegistrable("formation_core")),

    ITEMPARTIMPORT(new ItemPartImport("energyImportPartItem")),
    ITEMPARTEXPORT(new ItemPartExport("energyExportPartItem")),
    ITEMPARTSTORAGE(new ItemPartStorage("energyStoragePartItem")),
    ITEMPARTINTERFACE( new ItemPartInterface("energyInterfacePartItem")),
    ITEMPARTMONITOR( new ItemPartMonitor("energyMonitorPartItem")),
    ITEMPARTTERMINAL( new ItemPartTerminal("energyTerminalPartItem")),

    ITEMENERGYWIRELESSTERMINAL(new itemWirelessTerminal()),
    CHAOSMANIPULATOR( new toolChaosManipulator()),
    ENERGYSTORAGE_1k( new EnergyStorageCell("EnergyStorageCell_1k", 1024)),
    ENERGYSTORAGE_4k( new EnergyStorageCell("EnergyStorageCell_4k", 4096)),
    ENERGYSTORAGE_16k( new EnergyStorageCell("EnergyStorageCell_16k", 16384)),
    ENERGYSTORAGE_64k( new EnergyStorageCell("EnergyStorageCell_64k", 65536)),
    ENERGYSTORAGE_256k( new EnergyStorageCell("EnergyStorageCell_256k", 262144)),
    ENERGYSTORAGE_1024k(new EnergyStorageCell("EnergyStorageCell_1024k", 1048576)),
    ENERGYSTORAGE_4096k( new EnergyStorageCell("EnergyStorageCell_4096k", 4194304)),
    ENERGYSTORAGE_16384k( new EnergyStorageCell("EnergyStorageCell_16384k", 16777216)),

    ENERGYSTORAGECASING(new EnergyStorageCasing()),

    ENERGYSTORAGECOMPONENT_1k(new EnergyStorageComponent("EnergyStorageComponent_1k")),
    ENERGYSTORAGECOMPONENT_4k(new EnergyStorageComponent("EnergyStorageComponent_4k")),
    ENERGYSTORAGECOMPONENT_16k(new EnergyStorageComponent("EnergyStorageComponent_16k")),
    ENERGYSTORAGECOMPONENT_64k(new EnergyStorageComponent("EnergyStorageComponent_64k")),
    ENERGYSTORAGECOMPONENT_256k(new EnergyStorageComponent("EnergyStorageComponent_256k")),
    ENERGYSTORAGECOMPONENT_1024k(new EnergyStorageComponent("EnergyStorageComponent_1024k")),
    ENERGYSTORAGECOMPONENT_4096k(new EnergyStorageComponent("EnergyStorageComponent_4096k")),
    ENERGYSTORAGECOMPONENT_16384k(new EnergyStorageComponent("EnergyStorageComponent_16384k"));

    private Item item;

    ItemEnum( AIItemRegistrable _item) {
        this(_item, AppliedIntegrations.AI);
    }

    ItemEnum(Item _item){
        this.item = _item;
    }

    ItemEnum(AIItemRegistrable _item, CreativeTabs creativeTab) {
        this.item = _item;
        this.item.setCreativeTab(AppliedIntegrations.AI);
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
            ForgeRegistries.ITEMS.register(itemEnum.item);

            if(itemEnum.item instanceof ItemPartAIBase){
                ItemPartAIBase itemPart = (ItemPartAIBase)itemEnum.item;

            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for(ItemEnum item : values()) {
            if(item.item instanceof AIItemRegistrable) {
                AIItemRegistrable registrableItem = (AIItemRegistrable)item.item;
                registrableItem.registerModel();
            }else if(item.item instanceof toolChaosManipulator){
                toolChaosManipulator tCM = (toolChaosManipulator)item.item;
                tCM.registerModel();
            }
        }
     }
}
