package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.Parts.*;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCasing;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCell;
import AppliedIntegrations.Items.StorageCells.EnergyStorageComponent;
import AppliedIntegrations.Items.multiTool.toolChaosManipulator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

/**
 * @Author Azazell
 */
public enum ItemEnum {
    ENERGYANNIHILATIONCORE("energy.annihilation.core", new itemEnergyAnCore()),
    ENERGYFORMATIONCORE("energy.formation.core",new itemEnergyFmCore()),

    ITEMPARTIMPORT("energy.item.part.import", new ItemPartImport("energyImportPartItem")),
    ITEMPARTEXPORT("energy.item.part.export", new ItemPartExport("energyExportPartItem")),
    ITEMPARTSTORAGE("energy.item.part.storge", new ItemPartStorage("energyStoragePartItem")),
    ITEMPARTINTERFACE("energy.item.part.interface", new ItemPartInterface("energyInterfacePartItem")),
    ITEMPARTMONITOR("energy.item.part.monitor", new ItemPartMonitor("energyMonitorPartItem")),
    ITEMPARTTERMINAL("energy.item.part.terminal", new ItemPartInterface("energyTerminalPartItem")),

    ITEMENERGYWIRELESSTERMINAL("storage.energy.terminal",new itemWirelessTerminal()),
    CHAOSMANIPULATOR("item.chaos.manipulator", new toolChaosManipulator()),
    ENEGYSTORAGE("storage.energy", new EnergyStorageCell()),
    ENERGYSTORAGECASING("casing.energy", new EnergyStorageCasing()),
    ENERGYSTORAGECOMPONENT("component.energy", new EnergyStorageComponent());
    private final String internalName;
    private Item item;

    ItemEnum(String _internalName, Item _item) {
        this(_internalName, _item, AppliedIntegrations.AI);
    }

    ItemEnum(String _internalName, Item _item, CreativeTabs creativeTab) {
        this.internalName = _internalName;
        this.item = _item;
        this.item.setUnlocalizedName(AppliedIntegrations.modid + this.internalName);
        this.item.setCreativeTab(AppliedIntegrations.AI);
    }

    public ItemStack getDamagedStack(int damage) {
        return this.getDMGStack( damage, 1 );
    }
    public ItemStack getDMGStack( final int damageValue, final int size )
    {
        return new ItemStack( this.item, size, damageValue );
    }

    public String getInternalName() {
        return this.internalName;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getSizedStack(int size) {
        return new ItemStack(this.item, size);
    }

    public String getStatName() {
        return I18n.translateToLocal(this.item.getUnlocalizedName());
    }
}
