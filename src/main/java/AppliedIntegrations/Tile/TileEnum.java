package AppliedIntegrations.Tile;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Tile.HoleStorageSystem.*;
import AppliedIntegrations.Tile.HoleStorageSystem.storage.TileMEPylon;
import AppliedIntegrations.Tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.Tile.HoleStorageSystem.singularities.TileWhiteHole;
import AppliedIntegrations.Tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.Tile.LogicBus.TileLogicBusPort;
import AppliedIntegrations.Tile.LogicBus.TileLogicBusRib;
import AppliedIntegrations.Tile.Server.TileServerCore;
import AppliedIntegrations.Tile.Server.TileServerHousing;
import AppliedIntegrations.Tile.Server.TileServerPort;
import AppliedIntegrations.Tile.Server.TileServerRib;
import AppliedIntegrations.Tile.Server.TileServerSecurity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @Author Azazell
 */
public enum TileEnum
{
    TLBPort( TileLogicBusPort.class, AIConfig.enableLogicBus),
    TLBRib( TileLogicBusRib.class, AIConfig.enableLogicBus),
    TLBCore( TileLogicBusCore.class, AIConfig.enableLogicBus),

    TSCore( TileServerCore.class, AIConfig.enableMEServer),
    TSPort( TileServerPort.class, AIConfig.enableMEServer),
    TSRib(TileServerRib.class, AIConfig.enableMEServer),
    TSHousing(TileServerHousing.class, AIConfig.enableMEServer),
    TSSecurity(TileServerSecurity.class, AIConfig.enableMEServer),

    EnergyInterface ( TileEnergyInterface.class, AIConfig.enableEnergyFeatures),

    METurret( TileMETurretFoundation.class, AIConfig.enableBlackHoleStorage ),

    BlackHole( TileBlackHole.class, AIConfig.enableBlackHoleStorage ),
    MEPylon( TileMEPylon.class, AIConfig.enableBlackHoleStorage ),
    WhiteHole( TileWhiteHole.class, AIConfig.enableBlackHoleStorage );
    public final boolean enabled;
    // Tile entities's class
    public Class clazz;

    TileEnum(final Class clazz, boolean enabled)
    {
        this.clazz = clazz;
        this.enabled = enabled;
    }

    public void register(ResourceLocation reg) {
        if(enabled)
            GameRegistry.registerTileEntity(clazz, reg);
    }
}
