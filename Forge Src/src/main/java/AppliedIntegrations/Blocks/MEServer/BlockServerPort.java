package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerPort;
import appeng.util.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockServerPort extends AIMultiBlock {
    public BlockServerPort() {
        this.setBlockName("ME Server Port");

    }
    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block b) {
        TileServerPort port = (TileServerPort)w.getTileEntity(x,y,z);
        port.updateGrid();
    }

}
