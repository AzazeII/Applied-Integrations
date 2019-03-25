package AppliedIntegrations.tile;

import AppliedIntegrations.tile.Additions.*;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import AppliedIntegrations.tile.Additions.singularities.TileWhiteHole;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.tile.LogicBus.TileLogicBusPort;
import AppliedIntegrations.tile.LogicBus.TileLogicBusRib;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerHousing;
import AppliedIntegrations.tile.Server.TileServerPort;
import AppliedIntegrations.tile.Server.TileServerRib;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;


public enum TileEnum
{
    TLBPort( TileLogicBusPort.class),
    TLBRib( TileLogicBusRib.class),
    TLBCore( TileLogicBusCore.class),

    TSCore( TileServerCore.class),
    TSPort( TileServerPort.class),
    TSRib(TileServerRib.class),
    TSHousing(TileServerHousing.class),
    TSSecurity(TileServerSecurity.class),

    EnergyInterface ( TileEnergyInterface.class),

    METurret( TileMETurretFoundation.class ),
    METurretTower(TileMETurretTower.class),

    BlackHole( TileBlackHole.class ),
    MEPylon( TileMEPylon.class ),
    WhiteHole( TileWhiteHole.class );
    // Tile entities's class
    public Class clazz;

    TileEnum(final Class clazz )
    {
        this.clazz = clazz;
    }

    public void register(ResourceLocation reg) {
        GameRegistry.registerTileEntity(clazz, reg);
    }
}
