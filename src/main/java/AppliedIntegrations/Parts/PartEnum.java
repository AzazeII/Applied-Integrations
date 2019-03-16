package AppliedIntegrations.Parts;


import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Parts.Energy.*;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.AIStrings;

import AppliedIntegrations.Parts.P2PTunnels.PartP2PJoules;
import AppliedIntegrations.Parts.P2PTunnels.PartP2PRotaryWatts;
import appeng.api.config.Upgrades;
import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
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
    EnergyImportBus (AIStrings.Part_EnergyImportBus, PartEnergyImport.class, AppliedIntegrations.modid + ".group.energy.transport",
            generatePair( Upgrades.CAPACITY, 2 ), generatePair( Upgrades.REDSTONE, 1 ), generatePair( Upgrades.SPEED, 2 )),

    EnergyStorageBus (AIStrings.Part_EnergyStorageBus, PartEnergyStorage.class, null, generatePair( Upgrades.INVERTER, 1 )),

    EnergyExportBus (AIStrings.Part_EnergyExportBus, PartEnergyExport.class, AppliedIntegrations.modid + ".group.energy.transport",
            generatePair( Upgrades.CAPACITY, 2 ), generatePair( Upgrades.REDSTONE, 1 ), generatePair( Upgrades.SPEED, 2 )),

    EnergyTerminal (AIStrings.Part_EnergyTerminal, PartEnergyTerminal.class),


    EnergyInterface (AIStrings.Part_EnergyInterface, PartEnergyInterface.class),

    EnergyStorageMonitor (AIStrings.Part_EnergyStorageMonitor, PartEnergyStorageMonitor.class),

    P2PTunnelWatts (AIStrings.Part_P2PRotary, PartP2PRotaryWatts.class),

    P2PTunnelJoules (AIStrings.Part_P2PJoules, PartP2PJoules.class);

    /**
     * Cached enum values
     */
    public static final PartEnum[] VALUES = PartEnum.values();

    private AIStrings unlocalizedName;

    private Class<? extends AIPart> partClass;

    private String groupName;

    private Map<Upgrades, Integer> upgrades = new HashMap<Upgrades, Integer>();
    PartEnum(final AIStrings unlocalizedName, final Class<? extends AIPart> partClass )
    {
        this( unlocalizedName, partClass, null );
    }

    PartEnum(final AIStrings unlocalizedName, final Class<? extends AIPart> partClass, final String groupName )
    {
        // Set the localization string
        this.unlocalizedName = unlocalizedName;

        // Set the class
        this.partClass = partClass;

        // Set the group name
        this.groupName = groupName;
    }

    PartEnum(final AIStrings unlocalizedName, final Class<? extends AIPart> partClass, final String groupName,
                     final Pair<Upgrades, Integer> ... upgrades )
    {
        this( unlocalizedName, partClass, groupName );

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
        int clamped = MathHelper.clamp_int( itemStack.getItemDamage(), 0, PartEnum.VALUES.length - 1 );

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
        return ItemEnum.PARTITEM.getDamagedStack( this.ordinal() );
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
