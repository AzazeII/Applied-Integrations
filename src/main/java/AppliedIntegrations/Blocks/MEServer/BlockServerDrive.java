package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerDrive;
import AppliedIntegrations.tile.Server.TileServerRib;
import appeng.util.Platform;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockServerDrive extends BlockServerHousing {
    public BlockServerDrive(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Check if player isn't sneaking
        if(!p.isSneaking()) {
            // Get drive
            TileServerDrive drive = (TileServerDrive) world.getTileEntity(pos);

            // Check not null, has master and call only on server
            if (drive != null && drive.hasMaster() && !world.isRemote) {
                // Activate
                drive.activate(p);

                return true;
            }
        }

        return super.onBlockActivated(world, pos, state, p, hand, facing, hitX, hitY, hitZ);
    }
}
