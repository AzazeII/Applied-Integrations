package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerHousing;
import AppliedIntegrations.Entities.Server.TileServerRib;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockServerHousing extends AIMultiBlock {
    public BlockServerHousing(){
        this.setUnlocalizedName("ME Server Housing");
        this.setRegistryName("ServerHousing");
    }

}
