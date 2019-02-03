package AppliedIntegrations.AEFeatures;

import net.minecraft.util.text.translation.I18n;

public enum GuiText
{
    inventory( "container" ), // mc's default Inventory localization.

    Chest, StoredEnergy, Of, Condenser, Drive, GrindStone, SkyChest,

    VibrationChamber, SpatialIOPort, LevelEmitter, Terminal,

    Interface, Config, StoredItems, Patterns, ImportBus, ExportBus,

    CellWorkbench, NetworkDetails, StorageCells, IOBuses,

    IOPort, BytesUsed, Types, QuantumLinkChamber, PortableCell,

    NetworkTool, PowerUsageRate, PowerInputRate, Installed, EnergyDrain,

    StorageBus, Priority, Security, Encoded, Blank, Unlinked, Linked,

    SecurityCardEditor, NoPermissions, WirelessTerminal, Wireless,

    CraftingTerminal, FormationPlane, Inscriber, QuartzCuttingKnife,

    // tunnel names
    METunnel, ItemTunnel, RedstoneTunnel, EUTunnel, FluidTunnel, OCTunnel, LightTunnel, RFTunnel, PressureTunnel,

    StoredSize, CopyMode, CopyModeDesc, PatternTerminal,

    // Pattern tooltips
    CraftingPattern,
    ProcessingPattern,
    Crafts,
    Creates,
    And,
    With,
    Substitute,
    Yes,
    No,

    MolecularAssembler,

    StoredPower, MaxPower, RequiredPower, Efficiency, InWorldCrafting,

    inWorldFluix, inWorldPurificationCertus, inWorldPurificationNether,

    inWorldPurificationFluix, inWorldSingularity, ChargedQuartz,

    NoSecondOutput,
    OfSecondOutput,
    MultipleOutputs,

    Stores, Next, SelectAmount, Lumen, Empty,

    ConfirmCrafting, Stored, Crafting, Scheduled, CraftingStatus, Cancel, ETA, ETAFormat,

    FromStorage, ToCraft, CraftingPlan, CalculatingWait, Start, Bytes,

    CraftingCPU, Automatic, CoProcessors, Simulation, Missing,

    InterfaceTerminal, NoCraftingCPUs, Clean, InvalidPattern,

    InterfaceTerminalHint, Range, TransparentFacades, TransparentFacadesHint,

    NoCraftingJobs, CPUs, FacadeCrafting, inWorldCraftingPresses, ChargedQuartzFind,

    Included, Excluded, Partitioned, Precise, Fuzzy,

    // Used in a terminal to indicate that an item is craftable
    SmallFontCraft, LargeFontCraft,

    // Used in a ME Interface when no appropriate TileEntity was detected near it
    Nothing;

    private final String root;

    GuiText()
    {
        this.root = "gui.appliedenergistics2";
    }

    GuiText( final String r )
    {
        this.root = r;
    }

    public String getLocal()
    {
        return I18n.translateToLocal( this.getUnlocalized() );
    }

    public String getUnlocalized()
    {
        return this.root + '.' + this.toString();
    }

}
