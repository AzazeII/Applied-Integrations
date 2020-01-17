package AppliedIntegrations.tile.LogicBus;


import AppliedIntegrations.Blocks.LogicBus.modeling.ModeledLogicBus;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class TileLogicBusSlave extends AITile implements IAIMultiBlock {
	public boolean isCorner = false;

	private TileLogicBusCore master;

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
		if (Platform.isServer()) {
			if (Platform.isWrench(p, p.getHeldItem(hand), pos)) {
				if (p.isSneaking()) {
					final List<ItemStack> itemsToDrop = Lists.newArrayList(new ItemStack(world.getBlockState(pos).getBlock()));
					Platform.spawnDrops(world, pos, itemsToDrop);
					world.setBlockToAir(pos);
					return true;
				} else {
					// Try to form logic bus
					return tryToFindCore(p);
				}
			} else {
				if (!p.isSneaking() && hasMaster()) {
					AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiLogicBus, p, AEPartLocation.INTERNAL, getLogicMaster().getPos());
					return true;
				}
			}
		}
		return false;
	}

	public boolean tryToFindCore(EntityPlayer p) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			TileEntity candidate = world.getTileEntity(pos.offset(facing));
			if (candidate instanceof TileLogicBusCore) {
				TileLogicBusCore core = (TileLogicBusCore) candidate;
				core.tryConstruct(p);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasMaster() {

		return master != null;
	}

	// "Logic master" you got it ;)
	protected TileLogicBusCore getLogicMaster() {
		return (TileLogicBusCore) getMaster();
	}

	@Override
	public IMaster getMaster() {
		return master;
	}

	@Override
	public void setMaster(IMaster master) {
		this.master = (TileLogicBusCore) master;
	}

	@Override
	public void createProxyNode() {
		if (hasMaster()) {
			super.createProxyNode();
		}
	}

	public EnumSet<EnumFacing> getSidesWithSlaves() {
		List<EnumFacing> sides = new ArrayList<>();
		for (EnumFacing side : EnumFacing.values()) {
			if (world.getTileEntity(pos.offset(side)) instanceof TileLogicBusSlave) {
				sides.add(side);
			}
		}

		EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
		temp.addAll(sides);
		return temp;
	}

	@Override
	public void update() {
		if (!loaded && hasWorld() && Platform.isServer() && hasMaster()) {
			loaded = true;
			createProxyNode();
		}
	}


	@Override
	public void notifyBlock() {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(ModeledLogicBus.valid, hasMaster() && !isCorner()));
	}

	public boolean isCorner() {
		return isCorner;
	}
}
