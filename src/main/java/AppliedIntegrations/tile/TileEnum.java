package AppliedIntegrations.tile;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.tile.LogicBus.TileLogicBusPort;
import AppliedIntegrations.tile.LogicBus.TileLogicBusRib;
import AppliedIntegrations.tile.Server.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @Author Azazell
 */
public enum TileEnum {
	TLBPort(TileLogicBusPort.class, AIConfig.enableLogicBus),
	TLBRib(TileLogicBusRib.class, AIConfig.enableLogicBus),
	TLBCore(TileLogicBusCore.class, AIConfig.enableLogicBus),

	TSCore(TileServerCore.class, AIConfig.enableMEServer),
	TSPort(TileServerPort.class, AIConfig.enableMEServer),
	TSRib(TileServerRib.class, AIConfig.enableMEServer),
	TSHousing(TileServerHousing.class, AIConfig.enableMEServer),
	TSSecurity(TileServerSecurity.class, AIConfig.enableMEServer),

	EnergyInterface(TileEnergyInterface.class, AIConfig.enableEnergyFeatures),

	METurret(TileMETurretFoundation.class, AIConfig.enableBlackHoleStorage),

	BlackHole(TileBlackHole.class, AIConfig.enableBlackHoleStorage),
	MEPylon(TileMEPylon.class, AIConfig.enableBlackHoleStorage),
	WhiteHole(TileWhiteHole.class, AIConfig.enableBlackHoleStorage);
	public final boolean enabled;
	// tile entities's class
	public Class clazz;

	TileEnum(final Class clazz, boolean enabled) {
		this.clazz = clazz;
		this.enabled = enabled;
	}

	public void register(ResourceLocation reg) {
		if (enabled) {
			GameRegistry.registerTileEntity(clazz, reg);
		}
	}
}
