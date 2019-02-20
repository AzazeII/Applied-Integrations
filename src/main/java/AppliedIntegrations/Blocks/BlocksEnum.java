package AppliedIntegrations.Blocks;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusCore;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusHousing;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusPort;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusRibs;
import AppliedIntegrations.Blocks.MEServer.*;
import AppliedIntegrations.tile.TileEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Vector;

public enum BlocksEnum {
    BEI(new BlockEnergyInterface()),

    BSCore(new BlockServerCore("ServerCore", "ME Server Core"),TileEnum.TSCore),
    BSRib(new BlockServerRib("ServerFrame", "ME Server Rib"),TileEnum.TSRib),
    BSPort(new BlockServerPort("ServerPort", "ME Server Port"),TileEnum.TSPort),
    BSHousing(new BlockServerHousing("ServerHousing", "ME Server Housing"),TileEnum.TSHousing),
    BSSecurity(new BlockServerSecurity("ServerSecurity", "ME Server Security Terminal"),TileEnum.TSSecurity),

    BLBRibs(new BlockLogicBusRibs("BlockLogicBusRibs", "ME Logic Bus Rib"),TileEnum.TLBRib),
    BLBCore(new BlockLogicBusCore("BlockLogicBusCore", "ME Logic Bus Core"),TileEnum.TLBCore),
    BLBHousing(new BlockLogicBusHousing("BlockLogicBusHousing", "ME Logic Bus Housing"),TileEnum.TLBHousing),
    BLBPort(new BlockLogicBusPort("BlockLogicBusPort", "ME Logic Bus Port"),TileEnum.TLBPort);

    private static Vector<ItemBlock> itemBlocks = new Vector<>();
    public BlockAIRegistrable b;
    public TileEnum tileEnum;

    BlocksEnum(BlockAIRegistrable block){
        this.b = block;
        this.b.setCreativeTab(AppliedIntegrations.AI);
    }
    BlocksEnum(BlockAIRegistrable b, TileEnum t){
        this(b);
        tileEnum = t;
    }

    public static void register() {
        for(BlocksEnum blocksEnum : values()){
            ForgeRegistries.BLOCKS.register(blocksEnum.b);

            ItemBlock block = new ItemBlock(blocksEnum.b);
            block.setRegistryName(blocksEnum.b.getRegistryName());

            ForgeRegistries.ITEMS.register(block);
            itemBlocks.add(block);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {


    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        for(ItemBlock itemBlk : itemBlocks){
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlk, 0, new ModelResourceLocation(itemBlk.getRegistryName(), "inventory"));
        }
    }
}
