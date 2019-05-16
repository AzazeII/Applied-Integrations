package AppliedIntegrations;


import net.minecraft.util.text.translation.I18n;

/**
 * @Author Azazell
 */
public enum AIStrings {
	//Parts
	Part_EnergyImportBus("Energy.ImportBus", true),
	Part_EnergyExportBus("Energy.ExportBus", true),
	Part_EnergyStorageBus("Energy.StorageBus", true),
	Part_EnergyTerminal("Energy.terminal", true),
	Part_EnergyInterface("Energy.Interface", true),
	Part_EnergyStorageMonitor("Energy.StorageMonitor", true),
	Part_P2PRotary("Energy.Machine.P2PRotary", true),
	Part_P2PJoules("Energy.Machine.P2PJoules", true),
	Part_P2PHeat("Energy.Machine.P2PHeat", true),

	Part_P2PEmber("P2P.Ember", true),
	Part_P2PStarlight("P2P.Starlight", true),

	Part_EnergyAnnihilation("Energy.AnnihilationBus", true),
	Part_EnergyFormation("Energy.FormationBus", true),

	Part_ManaInterface("Mana.Interface", true),
	Part_ManaStorage("Mana.Storage", true),

	Tooltip_ItemStackDetails("tooltip.itemstack.details", false),
	Tooltip_CellBytes("tooltip.energy.cell.bytes", false),
	Tooltip_CellTypes("tooltip.energy.cell.types", false),

	Item_EnergyCell_1k("item.energy.cell.1k", true),
	Item_EnergyCell_4k("item.energy.cell.4k", true),
	Item_EnergyCell_16k("item.energy.cell.16k", true),
	Item_EnergyCell_64k("item.energy.cell.64k", true),
	Item_EnergyCell_256k("item.energy.cell.256k", true),
	Item_EnergyCell_1024k("item.energy.cell.1024k", true),
	Item_EnergyCell_4096k("item.energy.cell.4096k", true),
	Item_EnergyCell_Creative("item.energy.cell.creative", true),

	Item_EnergyCellHousing("item.storage.casing", true),
	Item_StorageComponent_1k("item.storage.component.1k", true),
	Item_StorageComponent_4k("item.storage.component.4k", true),
	Item_StorageComponent_16k("item.storage.component.16k", true),
	Item_StorageComponent_64k("item.storage.component.64k", true),
	Item_StorageComponent_256k("item.storage.component.256k", true),
	Item_StorageComponent_1024k("item.storage.component.1024k", true),
	Item_StorageComponent_4096k("item.storage.component.4096k", true),

	Gui_SelectedEnergy("gui.selected.energy", true),
	Gui_SelectedAmount("gui.selected.amount", true);

	private String unlocalized;

	private boolean isDotName;

	AIStrings(final String unloc, final boolean isDotName) {

		this.unlocalized = AppliedIntegrations.modid + "." + unloc;
		this.isDotName = isDotName;
	}

	public String getLocalized() {

		if (this.isDotName) {
			return I18n.translateToLocal(this.unlocalized + ".name");
		}

		return I18n.translateToLocal(this.unlocalized);
	}

	public String getUnlocalized() {

		return this.unlocalized;
	}
}
