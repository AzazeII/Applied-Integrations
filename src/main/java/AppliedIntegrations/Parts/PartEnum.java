package AppliedIntegrations.Parts;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Parts.P2P.PartEmberP2PTunnel;
import AppliedIntegrations.Parts.P2P.PartManaP2PTunnel;
import AppliedIntegrations.Parts.P2P.PartStarlightP2PTunnel;
import appeng.api.config.Upgrades;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = {@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage", modid = "Mekanism", striprefs = true), @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "Mekanism", striprefs = true), @Optional.Interface(iface = "com.cout970.magneticraft.api.heat.*", modid = "Magneticraft", striprefs = true), @Optional.Interface(iface = "com.cout970.magneticraft.api.heat.IHeatTile", modid = "Magneticraft", striprefs = true), @Optional.Interface(iface = "com.cout970.magneticraft.api.heat.prefab.*", modid = "Magneticraft", striprefs = true)})
public enum PartEnum {
	EnergyImportBus(ItemEnum.ITEMPARTIMPORT, AppliedIntegrations.modid + ".group.energy.transport"),
	EnergyStorageBus(ItemEnum.ITEMPARTSTORAGE),
	EnergyExportBus(ItemEnum.ITEMPARTEXPORT, AppliedIntegrations.modid + ".group.energy.transport"),
	EnergyTerminal(ItemEnum.ITEMPARTTERMINAL),

	EnergyInterface(ItemEnum.ITEMPARTINTERFACE),
	EnergyStorageMonitor(ItemEnum.ITEMPARTMONITOR),
	EnergyFormation(ItemEnum.ITEMPARTFORMATION),
	EnergyAnnihilation(ItemEnum.ITEMPARTANNIHILATION),

	ManaStorage(ItemEnum.ITEMMANAPARTSTORAGEBUS),
	ManaInterface(ItemEnum.ITEMMANAPARTINTERFACE),

	P2PEmber(ItemEnum.ITEMP2PEMBER),
	P2PStarlight(ItemEnum.ITEMP2PSTARLIGHT),
	P2PMana(ItemEnum.ITEMP2PMANA);

	/**
	 * Cached enum values
	 */
	public static final PartEnum[] VALUES = PartEnum.values();

	private ItemEnum parentItem;

	private String groupName;

	PartEnum(ItemEnum parent) {
		this(parent, null);
	}

	PartEnum(ItemEnum parent, final String groupName) {
		// Set the localization string

		// Set the group name
		this.groupName = groupName;

		// Set item form of host
		this.parentItem = parent;
	}

	public String getGroupName() {

		return this.groupName;
	}

	public ItemStack getStack() {

		return parentItem.getDamagedStack(ordinal());
	}
}
