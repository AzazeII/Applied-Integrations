package AppliedIntegrations.Gui;

import net.minecraft.util.text.translation.I18n;

/**
 * @Author AlgorithmX2
 */
public enum ButtonToolTips
{
    PowerUnits, IOMode, CondenserOutput, RedstoneMode, MatchingFuzzy,

    MatchingMode, TransferDirection, SortOrder, SortBy, View,

    PartitionStorage, Clear, FuzzyMode, OperationMode, TrashController,

    InterfaceBlockingMode, InterfaceCraftingMode, Trash, MatterBalls,

    Singularity, Read, Write, ReadWrite, AlwaysActive,

    ActiveWithoutSignal, ActiveWithSignal, ActiveOnPulse,

    EmitLevelsBelow, EmitLevelAbove, MatchingExact, TransferToNetwork,

    TransferToStorageCell, ToggleSortDirection, SearchMode_Auto,

    SearchMode_Standard, SearchMode_NEIAuto, SearchMode_NEIStandard,

    SearchMode, ItemName, NumberOfItems, PartitionStorageHint,

    ClearSettings, StoredItems, StoredCraftable, Craftable,

    FZPercent_25, FZPercent_50, FZPercent_75, FZPercent_99, FZIgnoreAll,

    MoveWhenEmpty, MoveWhenWorkIsDone, MoveWhenFull, Disabled, Enable,

    Blocking, NonBlocking,

    LevelType, LevelType_Energy, LevelType_Item, InventoryTweaks, TerminalStyle, TerminalStyle_Full, TerminalStyle_Tall, TerminalStyle_Small,

    Stash, StashDesc, Encode, EncodeDescription, Substitutions, SubstitutionsOn, SubstitutionsOff, SubstitutionsDescEnabled, SubstitutionsDescDisabled, CraftOnly, CraftEither,

    Craft, Mod, DoesntDespawn, EmitterMode, CraftViaRedstone, EmitWhenCrafting, ReportInaccessibleItems, ReportInaccessibleItemsYes, ReportInaccessibleItemsNo,

    BlockPlacement, BlockPlacementYes, BlockPlacementNo,

    // Used in the tooltips of the items in the terminal, when moused over
    ItemsStored, ItemsRequestable,

    SchedulingMode, SchedulingModeDefault, SchedulingModeRoundRobin, SchedulingModeRandom;

    private final String root;

    ButtonToolTips()
    {
        this.root = "gui.tooltips.appliedenergistics2";
    }

    ButtonToolTips( final String r )
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