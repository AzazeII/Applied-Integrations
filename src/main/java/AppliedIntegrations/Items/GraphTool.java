package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Topology.GraphToolMode;
import AppliedIntegrations.Topology.TopologyUtils;
import AppliedIntegrations.Utils.AILog;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.helpers.IMouseWheelItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static AppliedIntegrations.Topology.GraphToolMode.P2P_LINKS;
import static AppliedIntegrations.Topology.TopologyUtils.createLink;
import static appeng.api.util.AEPartLocation.INTERNAL;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 */
public class GraphTool extends AIItemRegistrable implements IMouseWheelItem {

    private GraphToolMode mode = GraphToolMode.ALL;

    public GraphTool(String registry) {
        super(registry);

        // Change stack size
        this.setMaxStackSize(1);

        // Add property for mode animation
        this.addPropertyOverride(new ResourceLocation(AppliedIntegrations.modid,"mode"), (stack, worldIn, entityIn) ->
                ((float) mode.ordinal()+1) / (float) 4);
    }

    private void cycleMode(boolean up) {
        try {
            // Check for up scroll
            if (up) {
                // Check if it is last mode
                if(mode == P2P_LINKS)
                    // Switch to 1st
                    mode = GraphToolMode.values()[0];
                else
                    // Switch mode to next
                    mode = GraphToolMode.values()[mode.ordinal() + 1];
            }else {
                // Check if it is first mode
                if(mode == GraphToolMode.values()[0])
                    // Switch to last
                    mode = P2P_LINKS;
                else
                    // Switch mode to previous
                    mode = GraphToolMode.values()[mode.ordinal() - 1];
            }
        }catch (IndexOutOfBoundsException indexOutOfBound){
            // Ignored
        }
    }

    @Override
    public EnumActionResult onItemUseFirst( final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side,
                                            final float hitX, final float hitY, final float hitZ, final EnumHand hand ) {
        // Trace ray on hitX,Y,Z
        final RayTraceResult mop = new RayTraceResult( new Vec3d( hitX, hitY, hitZ ), side, pos );

        // Get tile entity
        final TileEntity te = world.getTileEntity( pos );

        // Create grid node
        IGridNode node = null;

        // Check if tile is part host
        if( te instanceof IPartHost) {
            // Get part from host
            final SelectedPart part = ( (IPartHost) te ).selectPart( mop.hitVec );

            // Check not null (part)
            if(part != null && part.part != null)
                // Check not null (node)
                if(part.part.getGridNode() != null)
                    // Update node
                    node = part.part.getGridNode();

        // Check if tile is grid node
        }else if(te instanceof IGridHost){
            // Get host
            IGridHost host = (IGridHost)te;

            // Check not null
            if((host.getGridNode(INTERNAL) != null))
                // Update node
                node = host.getGridNode(INTERNAL);
        }

        // Check not null
        if(node != null){
            // Get grid
            IGrid grid = node.getGrid();

            // Pass to utils
            TopologyUtils.createWebUI(grid, player, mode, node.getMachine());

            // Log to player
            player.sendMessage(new TextComponentString("Created grid network graph at: ").appendSibling(createLink())); // (1)

            // Success
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    @Override
    public void onWheel(ItemStack is, boolean up) {
        // Pass cycle
        cycleMode(up);

        // Notify player
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Switching mode to: " + mode.name()));
    }
}
