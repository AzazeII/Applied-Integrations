package AppliedIntegrations.Blocks;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.Additions.BlockBlackHole;
import AppliedIntegrations.Blocks.Additions.BlockMEPylon;
import AppliedIntegrations.Blocks.Additions.BlockMETurret;
import AppliedIntegrations.Blocks.Additions.BlockWhiteHole;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusCore;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusPort;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusRibs;
import AppliedIntegrations.Blocks.MultiController.*;
import AppliedIntegrations.tile.TileEnum;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

/**
 * @Author Azazell
 */
public enum BlocksEnum {
	BEI(new BlockEnergyInterface("EInterface", "ME Energy Interface"), TileEnum.EnergyInterface, AIConfig.enableEnergyFeatures),

	BMCCore(new BlockMultiControllerCore("ServerCore", "ME Server Core"), TileEnum.TMCCore, AIConfig.enableMEServer),
	BMCRib(new BlockMultiControllerRib("ServerFrame", "ME Server Rib"), TileEnum.TMCRib, AIConfig.enableMEServer),
	BMCPort(new BlockMultiControllerPort("ServerPort", "ME Server Port"), TileEnum.TMCPort, AIConfig.enableMEServer),
	BMCHousing(new BlockMultiControllerHousing("ServerHousing", "ME Server Housing"), TileEnum.TMCHousing, AIConfig.enableMEServer),
	BMCTerminal(new BlockMultiControllerTerminal("ServerSecurity", "ME Server Security Terminal"), TileEnum.TMCTerminal, AIConfig.enableMEServer),

	BLBRibs(new BlockLogicBusRibs("BlockLogicBusRibs", "ME Logic Bus Rib"), TileEnum.TLBRib, AIConfig.enableLogicBus),
	BLBCore(new BlockLogicBusCore("BlockLogicBusCore", "ME Logic Bus Core"), TileEnum.TLBCore, AIConfig.enableLogicBus),
	BLBPort(new BlockLogicBusPort("BlockLogicBusPort", "ME Logic Bus Port"), TileEnum.TLBPort, AIConfig.enableLogicBus),

	BTurret(new BlockMETurret("BlockMETurret", "ME Turret"), TileEnum.METurret, AIConfig.enableBlackHoleStorage),
	BlackHole(new BlockBlackHole("BlockSingularity", "Black Hole"), TileEnum.BlackHole, AIConfig.enableBlackHoleStorage),
	WhiteHole(new BlockWhiteHole("BlockWhiteHole", "White Hole"), TileEnum.WhiteHole, AIConfig.enableBlackHoleStorage),
	BlockMEPylon(new BlockMEPylon("BlockMEPylon", "ME Pylon"), TileEnum.MEPylon, AIConfig.enableBlackHoleStorage);

	private static LinkedHashMap<Block, ItemBlock> itemBlocks = new LinkedHashMap<>();

	public BlockAIRegistrable b;

	public TileEnum tileEnum;

	public ItemBlock itemBlock;

	public boolean enabled;

	BlocksEnum(BlockAIRegistrable b, TileEnum t, boolean enabled) {

		this(b);
		this.tileEnum = t;
		this.enabled = enabled;
	}

	BlocksEnum(BlockAIRegistrable block) {
		this.b = block;
		this.b.setCreativeTab(AppliedIntegrations.AI);
	}

	public static void register() {

		for (BlocksEnum blocksEnum : values()) {
			if (!blocksEnum.enabled) {
				continue;
			}

			ForgeRegistries.BLOCKS.register(blocksEnum.b);
			ItemBlock block = new ItemBlock(blocksEnum.b);
			block.setRegistryName(blocksEnum.b.getRegistryName());
			ForgeRegistries.ITEMS.register(block);
			itemBlocks.put(blocksEnum.b, block);

			blocksEnum.itemBlock = block;
			blocksEnum.tileEnum.register(blocksEnum.b.getRegistryName());
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {

	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModels() {

		for (ItemBlock itemBlk : itemBlocks.values()) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlk, 0, new ModelResourceLocation(itemBlk.getRegistryName(), "inventory"));
		}
	}
}
