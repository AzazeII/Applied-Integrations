package AppliedIntegrations.Blocks;

import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusCore;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusHousing;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusPort;
import AppliedIntegrations.Blocks.LogicBus.BlockLogicBusRibs;
import AppliedIntegrations.Blocks.MEServer.*;
import AppliedIntegrations.Entities.TileEnum;
import net.minecraft.block.Block;

public enum BlocksEnum {
    BEI(new BlockEnergyInterface(),""),

    BSCore(new BlockServerCore(), "",TileEnum.TSCore),
    BSRib(new BlockServerRib(),"",TileEnum.TSRib),
    BSPort(new BlockServerPort(),"",TileEnum.TSPort),
    BSHousing(new BlockServerHousing(),"",TileEnum.TSHousing),
    BSSecurity(new BlockServerSecurity(), "",TileEnum.TSSecurity),

    BLBRibs(new BlockLogicBusRibs(),"",TileEnum.TLBRib),
    BLBCore(new BlockLogicBusCore(),"",TileEnum.TLBCore),
    BLBHousing(new BlockLogicBusHousing(),"",TileEnum.TLBHousing),
    BLBPort(new BlockLogicBusPort(),"",TileEnum.TLBPort);

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
