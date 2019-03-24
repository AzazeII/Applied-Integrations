package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Blocks.MEServer.BlockServerSecurity;
import AppliedIntegrations.Container.Server.ContainerServerPacketTracer;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;
import java.util.Vector;

public class TileServerSecurity extends AIMultiBlockTile {

    public EnumFacing fw;
    public Vector<ContainerServerPacketTracer> Listeners = new Vector<>();



    @Override
    public void update() {
        super.update();

        BlockServerSecurity block = null;
        if(world.getBlockState(pos).getBlock() instanceof BlockServerSecurity)
            block = (BlockServerSecurity)world.getBlockState(getPos()).getBlock();
        if(gridNode != null && block != null)
            block.isActive = gridNode.isActive();
        if(!hasMaster()){
            if(gridNode == null)
                return;
            if(gridNode.getGrid() == null)
                return;
            IGrid grid = gridNode.getGrid();
            for(IGridNode node : grid.getNodes()){
                if(node.getMachine() instanceof TileServerCore ) {
                    TileServerCore master = ((TileServerCore)node.getMachine());
                    if(master.isFormed) {
                        master.addSlave(this);
                        setMaster(master);
                    }
                    return;
                }
            }
        }

    }
    @Override
    public Object getServerGuiElement( final EntityPlayer player ) {
        return new ContainerServerPacketTracer((TileServerCore)getMaster(),player);
    }
    @Override
    public Object getClientGuiElement( final EntityPlayer player )
    {
        return new ServerPacketTracer((ContainerServerPacketTracer)this.getServerGuiElement(player),(TileServerCore)getMaster(),player);
    }
    @Override
    public void createAELink() {
        if (!world.isRemote) {
            if (gridNode == null)
                gridNode = AEApi.instance().grid().createGridNode(this);
                gridNode.updateState();
        }
    }
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }
    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        // TODO Auto-generated method stub
        EnumSet<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
        for(EnumFacing side : EnumFacing.values()){
            if(side != fw){
                set.add(side);
            }
        }
        return set;
    }

    @Override
    public void invalidate() {
        if (world != null && !world.isRemote) {
            destroyAELink();
        }
        if(hasMaster()){
            ((TileServerCore)getMaster()).Slaves.remove(this);
            master.MainNetwork = null;
        }
    }

    public void notifyBlock(){

    }


}
