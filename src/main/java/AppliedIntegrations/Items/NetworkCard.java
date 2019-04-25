package AppliedIntegrations.Items;

import AppliedIntegrations.tile.Server.TileServerPort;
import appeng.api.networking.IGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkCard extends AIItemRegistrable {
    private IGrid encodedGrid;

    public NetworkCard(String registry) {
        super(registry);
    }

    @Override
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side,
                                           final float hitX, final float hitY, final float hitZ, final EnumHand hand ) {
        // Get tile entity clicked
        TileEntity tile = world.getTileEntity(pos);

        // Check if tile instance of server port
        if (tile instanceof TileServerPort){
            // Get port
            TileServerPort port = (TileServerPort) tile;

            // Encode grid from port
            encodeGrid(port.getOuterGrid());

            // Success!
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    private void encodeGrid(IGrid outerGrid) {
        this.encodedGrid = outerGrid;
    }

    public IGrid getEncodedGrid() {
        return encodedGrid;
    }
}
