package AppliedIntegrations.Parts;


import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Parts.Botania.PartManaInterface;
import AppliedIntegrations.Parts.Botania.PartManaStorageBus;
import AppliedIntegrations.Parts.Energy.*;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.AIStrings;

import AppliedIntegrations.Parts.P2P.PartEmberP2PTunnel;
import AppliedIntegrations.Parts.P2P.PartStarlightP2PTunnel;
import appeng.api.config.Upgrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Optional;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "com.cout970.magneticraft.api.heat.*",modid = "Magneticraft",striprefs = true),
        @Optional.Interface(iface = "com.cout970.magneticraft.api.heat.IHeatTile", modid = "Magneticraft",striprefs = true),
        @Optional.Interface(iface = "com.cout970.magneticraft.api.heat.prefab.*",modid = "Magneticraft",striprefs = true)
})
public enum PartEnum
{
    EnergyImportBus (AIStrings.Part_EnergyImportBus, PartEnergyImport.class, ItemEnum.ITEMPARTIMPORT, AppliedIntegrations.modid + ".group.energy.transport",
            generatePair( Upgrades.CAPACITY, 2 ), generatePair( Upgrades.REDSTONE, 1 ), generatePair( Upgrades.SPEED, 2 )),

    EnergyStorageBus (AIStrings.Part_EnergyStorageBus, PartEnergyStorage.class, ItemEnum.ITEMPARTSTORAGE, null, generatePair( Upgrades.INVERTER, 1 )),

    EnergyExportBus (AIStrings.Part_EnergyExportBus, PartEnergyExport.class, ItemEnum.ITEMPARTEXPORT,AppliedIntegrations.modid + ".group.energy.transport",
            generatePair( Upgrades.CAPACITY, 2 ), generatePair( Upgrades.REDSTONE, 1 ), generatePair( Upgrades.SPEED, 2 )),

    EnergyTerminal (AIStrings.Part_EnergyTerminal, PartEnergyTerminal.class, ItemEnum.ITEMPARTTERMINAL),


    EnergyInterface (AIStrings.Part_EnergyInterface, PartEnergyInterface.class, ItemEnum.ITEMPARTINTERFACE),

    EnergyStorageMonitor (AIStrings.Part_EnergyStorageMonitor, PartEnergyStorageMonitor.class, ItemEnum.ITEMPARTMONITOR),

    EnergyFormation(AIStrings.Part_EnergyFormation, PartEnergyFormation.class, ItemEnum.ITEMPARTFORMATION),

    EnergyAnnihilation (AIStrings.Part_EnergyAnnihilation, PartEnergyAnnihilation.class, ItemEnum.ITEMPARTANNIHILATION),

    ManaStorage(AIStrings.Part_ManaStorage, PartManaStorageBus.class, ItemEnum.ITEMMANAPARTSTORAGEBUS ),

    ManaInterface(AIStrings.Part_ManaInterface, PartManaInterface.class, ItemEnum.ITEMMANAPARTINTERFACE ),

    P2PEmber(AIStrings.Part_P2PEmber, PartEmberP2PTunnel.class, ItemEnum.ITEMP2PEMBER ),
    P2PStarlight(AIStrings.Part_P2PStarlight, PartStarlightP2PTunnel.class, ItemEnum.ITEMP2PStarlight );
    /**
     * Cached enum values
     */
    public static final PartEnum[] VALUES = PartEnum.values();

    private AIStrings unlocalizedName;

    private Class<? extends AIPart> partClass;

    private ItemEnum parentItem;

    private String groupName;

    private Map<Upgrades, Integer> upgrades = new HashMap<Upgrades, Integer>();
    PartEnum(final AIStrings unlocalizedName, final Class<? extends AIPart> partClass, ItemEnum parent )
    {
        this( unlocalizedName, partClass, null, parent );
    }

    PartEnum(final AIStrings unlocalizedName, final Class<? extends AIPart> partClass, final String groupName, ItemEnum parent)
    {
        // Set the localization string
        this.unlocalizedName = unlocalizedName;

        // Set the class
        this.partClass = partClass;

        // Set the group name
        this.groupName = groupName;

        // Set item form of part
        this.parentItem = parent;
    }

    PartEnum(final AIStrings unlocalizedName, final Class<? extends AIPart> partClass, ItemEnum parent, final String groupName,
                     final Pair<Upgrades, Integer> ... upgrades)
    {
        this( unlocalizedName, partClass, groupName, parent );

        for( Pair<Upgrades, Integer> pair : upgrades )
        {
            // Add the upgrade to the map
            this.upgrades.put( pair.getKey(), pair.getValue() );
        }

    }

    private static Pair<Upgrades, Integer> generatePair( final Upgrades upgrade, final int maximum )
    {
        return new ImmutablePair<Upgrades, Integer>( upgrade, Integer.valueOf( maximum ) );
    }


    public static PartEnum getPartFromDamageValue( final ItemStack itemStack )
    {
        // Clamp the damage
        int clamped = MathHelper.clamp( itemStack.getItemDamage(), 0, PartEnum.VALUES.length - 1 );

        // Get the part
        return PartEnum.VALUES[clamped];
    }

    public AIPart createPartInstance(final ItemStack itemStack ) throws InstantiationException, IllegalAccessException
    {
        // Create a new instance of the part
        AIPart part = this.partClass.newInstance();

        // Setup based on the itemStack
        part.setupPartFromItem( itemStack );

        // Return the newly created part
        return part;

    }
    /**
     * Gets the group associated with this part.
     *
     * @return
     */
    public String getGroupName()
    {
        return this.groupName;
    }

    public String getLocalizedName()
    {
        return this.unlocalizedName.getLocalized();
    }


    public ItemStack getStack()
    {
        return parentItem.getDamagedStack(ordinal());
    }

    /**
     * Gets the unlocalized name for this part.
     *
     * @return
     */
    public String getUnlocalizedName()
    {
        return this.unlocalizedName.getUnlocalized();
    }

    public Map<Upgrades, Integer> getUpgrades()
    {
        return this.upgrades;
    }
}
