package AppliedIntegrations.Blocks;

import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusCore;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusHousing;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusPort;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusRibs;
import AppliedIntegrations.Blocks.MEServer.*;
import AppliedIntegrations.Entities.TileEnum;
import net.minecraft.block.Block;

public enum BlocksEnum {
    BEI(new BlockEnergyInterface(),"EInterface"),

    BSCore(new BlockServerCore(), "ServerCore",TileEnum.TSCore),
    BSRib(new BlockServerRib(),"ServerFrame",TileEnum.TSRib),
    BSPort(new BlockServerPort(),"ServerPort",TileEnum.TSPort),
    BSHousing(new BlockServerHousing(),"ServerHousing",TileEnum.TSHousing),
    BSSecurity(new BlockServerSecurity(), "ServerSecurity",TileEnum.TSSecurity),

    BLBRibs(new BlockLogicBusRibs(),"BlockLogicBusRibs",TileEnum.TLBRib),
    BLBCore(new BlockLogicBusCore(),"BlockLogicBusCore",TileEnum.TLBCore),
    BLBHousing(new BlockLogicBusHousing(),"BlockLogicBusHousing",TileEnum.TLBHousing),
    BLBPort(new BlockLogicBusPort(),"BlockLogicBusPort",TileEnum.TLBPort);

    public Block b;
    public String enumName;
    public TileEnum tileEnum;

    BlocksEnum(Block block, String ename){
        this.b = block;
        this.enumName = ename;
    }
    BlocksEnum(Block b,String ename, TileEnum t){
        this(b,ename);
        tileEnum = t;
    }
}
