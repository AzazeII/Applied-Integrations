package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.Server.TileServerPort;
import appeng.api.networking.IGrid;
import appeng.api.util.AEPartLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkCard extends AIItemRegistrable {
    private IGrid encodedGrid;
    private AEPartLocation side = AEPartLocation.INTERNAL;

    public NetworkCard(String registry) {
        super(registry);

        // Change stack size
        this.setMaxStackSize(1);

        // Add property for mode animation
        this.addPropertyOverride(new ResourceLocation(AppliedIntegrations.modid,"bit"), (stack, worldIn, entityIn) ->
                ((float) side.ordinal()) / (float) 10);
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
            // Get side vector from port
            encodeGrid(port.getOuterGrid(), port.getSideVector());

            // Success!
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    private void encodeGrid(IGrid outerGrid, AEPartLocation sideVector) {
        this.encodedGrid = outerGrid;
        this.side = sideVector;
    }

    public IGrid getEncodedGrid() {
        return encodedGrid;
    }
}
