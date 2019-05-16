package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.Utils.MultiBlockUtils;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.tile.AIPatterns;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Azazell
 */
public class TileLogicBusCore extends AITile implements IMaster, IAIMultiBlock {
	private boolean isFormed = false;

	private TileLogicBusPort mainNetworkPort;
	private Vector<TileLogicBusPort> subNetworkPorts = new Vector<>();
	private Vector<TileLogicBusSlave> slaves = new Vector<>();

	@Override
	public EnumSet<GridFlags> getFlags() {
		return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
	}

	@Override
	public EnumSet<EnumFacing> getConnectableSides() {
		return EnumSet.noneOf(EnumFacing.class);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (isFormed && !world.isRemote) {
			destroyMultiBlock();
		}
	}

	// Called on invalidate()
	public void destroyMultiBlock() {
		// Release slaves
		for (TileLogicBusSlave slave : slaves) {
			// Set master to null
			slave.setMaster(null);
			// Notify block
			slave.notifyBlock();
			// destroy node ( very cruel moment in code )
			slave.destroyAENode();
			// Notify grid node
			slave.getGridNode().updateState();
			// Remove from slave list
			slaves.remove(slave);
		}

		// Iterate over all sub - network ports
		for (TileLogicBusPort port : subNetworkPorts) {
			// Set state to main port state
			port.isSubPort = false;
		}

		// Nullify ports
		subNetworkPorts = new Vector<>();

		// Nullify main network port
		mainNetworkPort = null;
	}

	@Nonnull
	@Override
	public IGridNode getActionableNode() {
		return this.gridNode;
	}

	@Override
	public void update() {
		super.update();

		if (slaves.size() > 0) {
			IMEInventory<IAEItemStack> inventory = getOuterGridInventory();

			if (inventory != null) {
				inventory.injectItems(AEItemStack.fromItemStack(new ItemStack(Items.REDSTONE)), Actionable.MODULATE, new MachineSource(this));
			}
		}
	}

	// Return IMEInventory of network which belongs to ribs of multiblock
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
		if (!isFormed() && !world.isRemote) {
			// Create list of tiles which may be slaves
			Vector<TileLogicBusSlave> slaveCandidates = new Vector<>();

			// list of tiles that should become corners
			Vector<TileLogicBusRib> corners = new Vector<>();

			// Count all slaves
			AtomicInteger count = new AtomicInteger();

			MultiBlockUtils.fillListWithPattern(AIPatterns.ME_LOGIC, this, (data) -> {
				// Increment count
				count.getAndIncrement();

				// Add tile to candidate list
				slaveCandidates.add((TileLogicBusSlave) world.getTileEntity(pos.add(data.getPos())));

				// Check if block is corner
				if (data.type == BlockType.Corner)
				// Add to corner list
				{
					corners.add((TileLogicBusRib) world.getTileEntity(pos.add(data.getPos())));
				}
			});


			// Check if count is equal to pattern's block count
			if (count.get() == AIPatterns.ME_LOGIC.length) {

				// Count of ribs in layer two of structure ( should be 4)
				int ribCounter = 0;
				// Count of ports in layer two of structure ( should be 4)
				int portCounter = 0;

				// Candidates to slaves
				TileLogicBusPort mainNetworkCandidatePort = null;
				Vector<TileLogicBusPort> subNetworkPortsCandidates = new Vector<>();

				// Is line of ports formed?
				boolean lineFormed = false;

				// Iterate over horizontal sides
				for (EnumFacing side : EnumFacing.HORIZONTALS) {
					// Get tile entity with offset
					TileEntity tile = world.getTileEntity(pos.offset(side));
					// Found port
					if (tile instanceof TileLogicBusPort) {
						// Add to tile counter
						portCounter++;

						// Right tile from first tile
						TileEntity edgeTileA = world.getTileEntity(tile.getPos().offset(side.rotateY()));
						// Left tile from first tile
						TileEntity edgeTileB = world.getTileEntity(tile.getPos().offset(side.rotateY().getOpposite()));

						// Check if this line of tiles is ports
						if (!lineFormed && edgeTileA instanceof TileLogicBusPort && edgeTileB instanceof TileLogicBusPort) {
							// Add port-slave candidates
							subNetworkPortsCandidates.add((TileLogicBusPort) tile);
							subNetworkPortsCandidates.add((TileLogicBusPort) edgeTileA);
							subNetworkPortsCandidates.add((TileLogicBusPort) edgeTileB);

							// Change lineFormed to true
							lineFormed = true;

							// Add port-slave candidates to slave candidates
							slaveCandidates.addAll(subNetworkPortsCandidates);

							// Add to counter
							portCounter += 2;

							// get opposite side
							EnumFacing opposite = side.getOpposite();

							// Check for ribs in corners with double offset at opposite side
							TileEntity mayBeRibA = world.getTileEntity(edgeTileA.getPos().offset(opposite).offset(opposite));
							TileEntity mayBeRibB = world.getTileEntity(edgeTileB.getPos().offset(opposite).offset(opposite));

							if (mayBeRibA instanceof TileLogicBusRib && mayBeRibB instanceof TileLogicBusRib) {
								// Add to counter
								ribCounter += 2;

								// Add ribs to candidates
								slaveCandidates.add((TileLogicBusRib) mayBeRibA);
								slaveCandidates.add((TileLogicBusRib) mayBeRibB);
							}
						} else {
							// Record main network candidate
							mainNetworkCandidatePort = (TileLogicBusPort) tile;
							// Add main network candidate to slave candidates
							slaveCandidates.add(mainNetworkCandidatePort);
						}
						// Check if tile is rib
					} else if (tile instanceof TileLogicBusRib) {
						// Add to counter
						ribCounter++;
						// Add candidate
						slaveCandidates.add((TileLogicBusRib) tile);
					}
				}

				// Check if there is 4 ribs and 4 ports
				if (ribCounter == 4 && ribCounter == portCounter && mainNetworkCandidatePort != null) {
					// Record ports
					subNetworkPorts = subNetworkPortsCandidates;
					mainNetworkPort = mainNetworkCandidatePort;

					// Make corners know they are corners
					for (TileLogicBusRib corner : corners) {
						corner.isCorner = true;
					}

					// Set master for each slave
					for (TileLogicBusSlave slave : slaveCandidates) {
						// Update master
						slave.setMaster(this);
						// Notify block state
						slave.notifyBlock();
						// Update node
						slave.createAENode();
						// Add to slave list
						slaves.add(slave);
						// Mark to update
						slave.markDirty();
					}

					// Iterate over all sub - network ports
					for (TileLogicBusPort port : subNetworkPorts) {
						// Set state to sub port state
						port.isSubPort = true;
					}

					// Now formed
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
		// Check for size of slave list
		if (slaves.size() > 0) {
			// Create list
			List<IGridNode> list = new ArrayList<>();
			// Iterate over slaves
			for (TileLogicBusSlave slave : slaves) {
				// Get node
				IGridNode node = slave.getGridNode();
				// Add node
				list.add(node);
			}
			// Add this
			list.add(this.getGridNode());
			// Return list
			return list.iterator();
		}

		// Create list, which only contains this tile
		List<IGridNode> list = new ArrayList<>();
		list.add(this.getGridNode());
		return list.iterator();
	}
}

/* line 87:
// Iterate over logic bus pattern
// Iterate for i < len
for (int i = 0; i < AIPatterns.ME_LOGIC.length; i++) {
    // Get pattern data
    BlockData data = AIPatterns.ME_LOGIC[i];

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