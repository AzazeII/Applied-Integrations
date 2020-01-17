package AppliedIntegrations.tile.LogicBus;


import AppliedIntegrations.tile.IAIMultiBlock;
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
	public TileLogicBusPort(){
		super();

		this.getProxy().setValidSides(getValidSides());
	}

	private EnumSet<EnumFacing> getValidSides() {
		List<EnumFacing> sides = new ArrayList<>();
		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			if (!(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusPort)) {
				sides.add(side);
			}
		}

		EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
		temp.addAll(sides);

		return temp;
	}

	@Override
	public void tryConstruct(EntityPlayer p) {

	}
}
