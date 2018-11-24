package AppliedIntegrations.AEFeatures;

public enum IntegrationType
{
    IC2( IntegrationSide.BOTH, "Industrial Craft 2", "IC2" ),

    RotaryCraft( IntegrationSide.BOTH, "Rotary Craft", "RotaryCraft" ),

    RC( IntegrationSide.BOTH, "Railcraft", "Railcraft" ),

    BuildCraftCore( IntegrationSide.BOTH, "BuildCraft Core", "BuildCraft|Core" ),

    BuildCraftTransport( IntegrationSide.BOTH, "BuildCraft Transport", "BuildCraft|Transport" ),

    BuildCraftBuilder( IntegrationSide.BOTH, "BuildCraft Builders", "BuildCraft|Builders" ),

    RF( IntegrationSide.BOTH, "RedstoneFlux Power - Tiles", "CoFHAPI" ),

    RFItem( IntegrationSide.BOTH, "RedstoneFlux Power - Items", "CoFHAPI" ),

    MFR( IntegrationSide.BOTH, "Mine Factory Reloaded", "MineFactoryReloaded" ),

    DSU( IntegrationSide.BOTH, "Deep Storage Unit", null ),

    FZ( IntegrationSide.BOTH, "Factorization", "factorization" ),

    FMP( IntegrationSide.BOTH, "Forge MultiPart", "McMultipart" ),

    RB( IntegrationSide.BOTH, "Rotatable Blocks", "RotatableBlocks" ),

    CLApi( IntegrationSide.BOTH, "Colored Lights Core", "coloredlightscore" ),

    Waila( IntegrationSide.BOTH, "Waila", "Waila" ),

    InvTweaks( IntegrationSide.CLIENT, "Inventory Tweaks", "inventorytweaks" ),

    NEI( IntegrationSide.CLIENT, "Not Enough Items", "NotEnoughItems" ),

    CraftGuide( IntegrationSide.CLIENT, "Craft Guide", "craftguide" ),

    Mekanism( IntegrationSide.BOTH, "Mekanism", "Mekanism" ),

    ImmibisMicroblocks( IntegrationSide.BOTH, "ImmibisMicroblocks", "ImmibisMicroblocks" ),

    BetterStorage( IntegrationSide.BOTH, "BetterStorage", "betterstorage" ),

    OpenComputers( IntegrationSide.BOTH, "OpenComputers", "OpenComputers" ),

    PneumaticCraft( IntegrationSide.BOTH, "PneumaticCraft", "PneumaticCraft" );

    public final IntegrationSide side;
    public final String dspName;
    public final String modID;

    IntegrationType( final IntegrationSide side, final String name, final String modid )
    {
        this.side = side;
        this.dspName = name;
        this.modID = modid;
    }

}
