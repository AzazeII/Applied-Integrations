package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @Author Azazell
 */
public class TileLogicBusPort extends TileLogicBusSlave implements IAIMultiBlock {
	public boolean isSubPort = false;
	private boolean isCorner;

	@Override
	public EnumSet<GridFlags> getFlags() {
		return EnumSet.of(GridFlags.REQUIRE_CHANNEL, GridFlags.MULTIBLOCK);
	}

	@Override
	public EnumSet<EnumFacing> getConnectableSides() {
		// list of sides
		List<EnumFacing> sides = new ArrayList<>();
		// Iterate only over horizontal sides, as only these sides can be connected to cable
		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			// Check if tile in this side is not instance of logic bus port
			if (!(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusPort)) {
				sides.add(side);
			}
		}

		// Temp set
		EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
		// Add sides
		temp.addAll(sides);
		return temp;
	}

	@Override
	public void tryConstruct(EntityPlayer p) {

	}
}
