package AppliedIntegrations.tile.LogicBus;


import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * @Author Azazell
 */
public class TileLogicBusRib extends TileLogicBusSlave implements IAIMultiBlock {
	public TileLogicBusRib(){
		super();
		this.getProxy().setValidSides(getValidSides());
	}

	@Override
	public void tryConstruct(EntityPlayer p) {

	}

	public EnumSet<EnumFacing> getValidSides() {
		List<EnumFacing> sides = new ArrayList<>();
		for (EnumFacing side : EnumFacing.values()) {
			// Check if tile in this side is not instance of logic bus port or core, so rib will connect only to
			// other ribs in multiblock, or outer cable
			if (!(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusPort) && !(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusCore)) {
				sides.add(side);
			}
		}

		EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
		temp.addAll(sides);
		return temp;
	}

	@Override
	public boolean tryToFindCore(EntityPlayer p) {
		for (EnumFacing vertical : Arrays.asList(UP, DOWN)) {
			for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {
				// OVERRIDEN: Move pos upward to height of port
				TileEntity candidate = world.getTileEntity(pos.offset(vertical).offset(horizontal));
				if (candidate instanceof TileLogicBusCore) {
					TileLogicBusCore core = (TileLogicBusCore) candidate;
					core.tryConstruct(p);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return Interface (not block) for interacting with ME Network's inventory, used by logic bus core to
	 * inject autocrafting items to outer grid
	 */
	public IMEInventory<IAEItemStack> getOuterGridInventory() {
		if (getGridNode() == null) {
			return null;
		}

		IGrid grid = getGridNode().getGrid();
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		return storage.getInventory(getItemChannel());
	}

	private IItemStorageChannel getItemChannel() {

		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}
}
