package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.Blocks.LogicBus.modeling.ModeledLogicBus;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import appeng.api.AEApi;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class TileLogicBusSlave extends AITile implements IAIMultiBlock, IGridMultiblock {
    public boolean isCorner = false;

    private TileLogicBusCore master;

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
        if(!world.isRemote) {
            // Check if it's wrench
            if (Platform.isWrench(p, p.getHeldItem(hand), pos)) {
                // Destroy tile
                if (p.isSneaking()) {
                    final List<ItemStack> itemsToDrop = Lists.newArrayList(new ItemStack(world.getBlockState(pos).getBlock()));
                    Platform.spawnDrops(world, pos, itemsToDrop);
                    world.setBlockToAir(pos);
                    return true;
                    // Try to form logic bus
                } else {
                    return tryToFindCore(p);
                }
            }else{
                // Check if player not sneaking, and tile has core
                if(!p.isSneaking() && hasMaster()) {
                    // Open gui of logic bus
                    AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiLogicBus, p, AEPartLocation.INTERNAL, getLogicMaster().getPos());
                    return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public Iterator<IGridNode> getMultiblockNodes() {
        if(hasMaster())
            return getLogicMaster().getMultiblockNodes();
        return new ArrayList<IGridNode>().listIterator();
    }

    public boolean tryToFindCore(EntityPlayer p) {
        // Iterate over all sides
        for(EnumFacing facing : EnumFacing.HORIZONTALS){
            // Find candidate
            TileEntity candidate = world.getTileEntity(pos.offset(facing));
            // Check if candidate is LogicBusCore
            if(candidate instanceof TileLogicBusCore){
                // Capture core in variable
                TileLogicBusCore core = (TileLogicBusCore)candidate;
                // Try to construct multi-block
                core.tryConstruct(p);
                return true;
            }
        }
        return false;
    }

    @Override
    public void createAENode() {
        if (!world.isRemote && hasMaster()) {
            if (gridNode == null)
                gridNode = AEApi.instance().grid().createGridNode(this);
            gridNode.updateState();
        }
    }

    @Override
    public boolean hasMaster() {
        return master != null;
    }

    // "Logic master" you get ;)
    protected TileLogicBusCore getLogicMaster(){
        return (TileLogicBusCore)getMaster();
    }

    @Override
    public IMaster getMaster() {
        return master;
    }

    @Override
    public void setMaster(IMaster master) {
        this.master = (TileLogicBusCore)master;
    }

    @Override
    public void update() {
        //create grid node on add to world
        if (!loaded && hasWorld() && !world.isRemote && hasMaster()) {
            loaded = true;
            createAENode();
        }
    }

    @Override
    public void notifyBlock(){
        world.setBlockState(pos, world.getBlockState(pos).withProperty(ModeledLogicBus.valid, hasMaster() && !isCorner()));
    }

    public boolean isCorner() {
        return isCorner;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
       super.readFromNBT(compound);
       //setMaster(getMaster().readMaster(compound));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        //getMaster().writeMaster(compound);

        return compound;
    }

    public EnumSet<EnumFacing> getSidesWithSlaves() {
        // List of sides
        List<EnumFacing> sides = new ArrayList<>();
        // Iterate over all sides
        for(EnumFacing side : EnumFacing.values()){
            // Check if tile in this side is instance of logic bus port or core
            if(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusSlave){
                sides.add(side);
            }
        }

        // Temp set
        EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
        // Add sides
        temp.addAll(sides);
        return temp;
    }
}
