package AppliedIntegrations.TileEntity;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.TileEntity.LogicBus.TileLogicBusHousing;
import AppliedIntegrations.TileEntity.LogicBus.TileLogicBusCore;
import AppliedIntegrations.TileEntity.LogicBus.TileLogicBusPort;
import AppliedIntegrations.TileEntity.LogicBus.TileLogicBusRib;
import AppliedIntegrations.TileEntity.Server.TileServerCore;
import AppliedIntegrations.TileEntity.Server.TileServerHousing;
import AppliedIntegrations.TileEntity.Server.TileServerPort;
import AppliedIntegrations.TileEntity.Server.TileServerRib;
import AppliedIntegrations.TileEntity.Server.TileServerSecurity;
import net.minecraftforge.fml.common.registry.GameRegistry;


public enum TileEnum
{
    TLBHousing("TileLogicBusHousing", TileLogicBusHousing.class, BlocksEnum.BLBHousing),
    TLBPort("TileLogicBusCore", TileLogicBusPort.class, BlocksEnum.BLBPort),
    TLBRib("TileLogicBusRib", TileLogicBusRib.class ,BlocksEnum.BLBRibs),
    TLBCore("TileLogicBusCore", TileLogicBusCore.class, BlocksEnum.BLBCore),

    TSCore("TileServerCore", TileServerCore.class, BlocksEnum.BSCore),
    TSPort("TileServerPort", TileServerPort.class, BlocksEnum.BSPort),
    TSRib("TileServerRib", TileServerRib.class,BlocksEnum.BLBRibs),
    TSHousing("TileServerHousing", TileServerHousing.class,BlocksEnum.BSHousing),
    TSSecurity("TileServerSecurity", TileServerSecurity.class,BlocksEnum.BSSecurity),

    EnergyInterface ("TileEnergyInterface", TileEnergyInterface.class, BlocksEnum.BEI);

    /**
     * Unique ID of the tile entity
     */
    private String ID;

    /**
     * Tile entity class.
     */
    private Class clazz;
    public BlocksEnum blocksEnum;

    TileEnum(final String ID, final Class clazz, BlocksEnum AssociatedBlock )
    {
        this.ID = ID;
        this.clazz = clazz;
        this.blocksEnum = AssociatedBlock;
    }

    public static void register() {
        for(TileEnum tileEnum : values()){
            GameRegistry.registerTileEntity(tileEnum.getTileClass(), tileEnum.getTileID());
        }
    }

    /**
     * Gets the tile entity's class.
     */
    public Class getTileClass()
    {
        return this.clazz;
    }

    /**
     * Gets the tile entity's ID.
     *
     * @return
     */
    public String getTileID()
    {
        return AppliedIntegrations.modid + "." + this.ID;
    }
}
