package AppliedIntegrations.Items;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Topology.GraphToolMode;
import AppliedIntegrations.Topology.TopologyUtils;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.helpers.IMouseWheelItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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

import static AppliedIntegrations.Topology.GraphToolMode.P2P_LINKS;
import static AppliedIntegrations.Topology.TopologyUtils.createLink;
import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * @Author Azazell
 */
public class GraphTool extends AIItemRegistrable implements IMouseWheelItem {
	private GraphToolMode mode = GraphToolMode.ALL;

	public GraphTool(String registry) {
		super(registry);
		this.setMaxStackSize(1);
		this.addPropertyOverride(new ResourceLocation(AppliedIntegrations.modid,
				"mode"), (stack, worldIn, entityIn) -> ((float) mode.ordinal() + 1) / (float) 4);
	}

	@Override
	public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
		boolean success = false;

		if (player.isServerWorld()) {
			// Trace ray on hitX,Y,Z
			final RayTraceResult mop = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
			final TileEntity te = world.getTileEntity(pos);
			IGridNode node = null;

			if (te instanceof IPartHost) {
				final SelectedPart part = ((IPartHost) te).selectPart(mop.hitVec);
				if (part != null && part.part != null) {
					if (part.part.getGridNode() != null) {
						node = part.part.getGridNode();
					}
				}

			} else if (te instanceof IGridHost) {
				IGridHost host = (IGridHost) te;

				if ((host.getGridNode(INTERNAL) != null)) {
					node = host.getGridNode(INTERNAL);
				}
			}

			// Check not null
			if (node != null) {
				IGrid grid = node.getGrid();
				TopologyUtils.createWebUI(grid, player, mode, node.getMachine());
				player.sendMessage(new TextComponentString("Created grid network graph at: ").appendSibling(createLink())); // (1)

				success = true;
			}
		}

		if (!success) {
			return EnumActionResult.FAIL;
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public void onWheel(ItemStack is, boolean up) {
		cycleMode(up);
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Switching mode to: " + mode.name()));
	}

	private void cycleMode(boolean up) {
		try {
			if (up) {
				if (mode == P2P_LINKS) {
					mode = GraphToolMode.values()[0];
				} else {
					mode = GraphToolMode.values()[mode.ordinal() + 1];
				}
			} else {
				if (mode == GraphToolMode.values()[0]) {
					mode = P2P_LINKS;
				} else {
					mode = GraphToolMode.values()[mode.ordinal() - 1];
				}
			}
		} catch (IndexOutOfBoundsException indexOutOfBound) {}
	}
}
