package AppliedIntegrations.tile.LogicBus;
import AppliedIntegrations.Utils.MultiBlockUtils;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.Patterns.AIPatterns;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.EnumSet.noneOf;

/**
 * @Author Azazell
 */
public class TileLogicBusCore extends AITile implements IMaster, IAIMultiBlock {
	private boolean isFormed = false;
	private TileLogicBusPort mainNetworkPort;
	private Vector<TileLogicBusPort> subNetworkPorts = new Vector<>();
	private Vector<TileLogicBusSlave> slaves = new Vector<>();

	public TileLogicBusCore(){
		super();
		this.getProxy().setValidSides(noneOf(EnumFacing.class));
	}

	@Override
	public void invalidate() {

		super.invalidate();
		if (isFormed && Platform.isServer()) {
			destroyMultiBlock();
		}
	}

	// Called on invalidate()
	public void destroyMultiBlock() {
		for (TileLogicBusSlave slave : slaves) {
			slave.setMaster(null);
			slave.notifyBlock();
			slave.destroyProxyNode();
			slave.getGridNode().updateState();
			slaves.remove(slave);
		}

		for (TileLogicBusPort port : subNetworkPorts) {
			port.isSubPort = false;
		}

		subNetworkPorts = new Vector<>();
		mainNetworkPort = null;
	}

	@Nonnull
	@Override
	public IGridNode getActionableNode() {
		return getProxy().getNode();
	}

	@Override
	public void update() {
		super.update();

		if (slaves.size() > 0) {
			IMEInventory<IAEItemStack> inventory = getOuterGridInventory();

			if (inventory != null) {
				inventory.injectItems(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(new ItemStack(Items.REDSTONE)), Actionable.MODULATE, new MachineSource(this));
			}
		}
	}

	private IMEInventory<IAEItemStack> getOuterGridInventory() {
		for (TileLogicBusSlave slave : slaves) {
			if (slave instanceof TileLogicBusRib) {
				return ((TileLogicBusRib) slave).getOuterGridInventory();
			}
		}
		return null;
	}

	@Override
	public void notifyBlock() {

	}

	@Override
	public void tryConstruct(EntityPlayer p) {
		if (!isFormed() && Platform.isServer()) {
			Vector<TileLogicBusSlave> slaveCandidates = new Vector<>();
			Vector<TileLogicBusRib> corners = new Vector<>();
			AtomicInteger count = new AtomicInteger();

			MultiBlockUtils.fillListWithPattern(AIPatterns.ME_LOGIC_BUS.getPatternData(), this, (data) -> {
				count.getAndIncrement();
				slaveCandidates.add((TileLogicBusSlave) world.getTileEntity(pos.add(data.getPos())));
				if (data.type == BlockType.Corner) {
					corners.add((TileLogicBusRib) world.getTileEntity(pos.add(data.getPos())));
				}
			});


			if (count.get() == AIPatterns.ME_LOGIC_BUS.getPatternData().size()) {

				// Count of ribs in layer two of structure ( should be 4)
				int ribCounter = 0;
				// Count of ports in layer two of structure ( should be 4)
				int portCounter = 0;

				// Candidates to slaves
				TileLogicBusPort mainNetworkCandidatePort = null;
				Vector<TileLogicBusPort> subNetworkPortsCandidates = new Vector<>();

				// Is line of ports formed?
				boolean lineFormed = false;
				for (EnumFacing side : EnumFacing.HORIZONTALS) {
					TileEntity tile = world.getTileEntity(pos.offset(side));
					if (tile instanceof TileLogicBusPort) {
						portCounter++;

						TileEntity edgeTileRight = world.getTileEntity(tile.getPos().offset(side.rotateY()));
						TileEntity edgeTileLeft = world.getTileEntity(tile.getPos().offset(side.rotateY().getOpposite()));
						if (!lineFormed && edgeTileRight instanceof TileLogicBusPort && edgeTileLeft instanceof TileLogicBusPort) {
							subNetworkPortsCandidates.add((TileLogicBusPort) tile);
							subNetworkPortsCandidates.add((TileLogicBusPort) edgeTileRight);
							subNetworkPortsCandidates.add((TileLogicBusPort) edgeTileLeft);

							lineFormed = true;
							slaveCandidates.addAll(subNetworkPortsCandidates);
							portCounter += 2;
							EnumFacing opposite = side.getOpposite();
							TileEntity mayBeRibA = world.getTileEntity(edgeTileRight.getPos().offset(opposite).offset(opposite));
							TileEntity mayBeRibB = world.getTileEntity(edgeTileLeft.getPos().offset(opposite).offset(opposite));

							if (mayBeRibA instanceof TileLogicBusRib && mayBeRibB instanceof TileLogicBusRib) {
								ribCounter += 2;
								slaveCandidates.add((TileLogicBusRib) mayBeRibA);
								slaveCandidates.add((TileLogicBusRib) mayBeRibB);
							}
						} else {
							mainNetworkCandidatePort = (TileLogicBusPort) tile;
							slaveCandidates.add(mainNetworkCandidatePort);
						}
					} else if (tile instanceof TileLogicBusRib) {
						ribCounter++;
						slaveCandidates.add((TileLogicBusRib) tile);
					}
				}

				if (ribCounter == 4 && ribCounter == portCounter && mainNetworkCandidatePort != null) {
					subNetworkPorts = subNetworkPortsCandidates;
					mainNetworkPort = mainNetworkCandidatePort;

					for (TileLogicBusRib corner : corners) {
						corner.isCorner = true;
					}

					for (TileLogicBusSlave slave : slaveCandidates) {
						slave.setMaster(this);
						slave.notifyBlock();
						slave.createProxyNode();

						slaves.add(slave);
						slave.markDirty();
					}

					for (TileLogicBusPort port : subNetworkPorts) {
						port.isSubPort = true;
					}

					isFormed = true;
				}
			}
		}
	}

	public boolean isFormed() {
		return isFormed;
	}

	@Override
	public boolean hasMaster() {
		return true;
	}

	@Override
	public IMaster getMaster() {
		return this;
	}

	@Override
	public void setMaster(IMaster tileServerCore) {

	}

	@Override
	public Iterator<IGridNode> getMultiblockNodes() {
		if (slaves.size() > 0) {
			List<IGridNode> list = new ArrayList<>();
			for (TileLogicBusSlave slave : slaves) {
				IGridNode node = slave.getGridNode();
				list.add(node);
			}

			list.add(this.getGridNode());
			return list.iterator();
		}

		List<IGridNode> list = new ArrayList<>();
		list.add(this.getGridNode());
		return list.iterator();
	}
}

/* line 87:
// Iterate over logic bus pattern
// Iterate for i < len
for (int i = 0; i < AIPatterns.ME_LOGIC_BUS.length; i++) {
    // Get pattern data
    BlockData data = AIPatterns.ME_LOGIC_BUS[i];

    // Get block
    Block block = world.getBlockState(new BlockPos(pos.getX() + data.x,
            pos.getY() + data.y,
            pos.getZ() + data.z)).getBlock();

    // check if block is correctly placed
    if (block == data.b) {
        // Increase slave count
        count++;
        // Add slave candidate
        slaveCandidates.add((TileLogicBusSlave) world.getTileEntity(new BlockPos(pos.getX() + data.x,
                pos.getY() + data.y,
                pos.getZ() + data.z)));

        if(data.type == BlockType.Corner){
            corners.add((TileLogicBusRib) world.getTileEntity(new BlockPos(pos.getX() + data.x,
                    pos.getY() + data.y,
                    pos.getZ() + data.z)));
        }
    }
}
*/