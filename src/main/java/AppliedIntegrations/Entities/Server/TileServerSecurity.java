package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.API.AppliedCoord;
import AppliedIntegrations.Blocks.MEServer.BlockServerSecurity;
import AppliedIntegrations.Container.Server.ContainerServerPacketTracer;
import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.Vector;

public class TileServerSecurity extends AIMultiBlockTile {

    public ForgeDirection fw;
    public Vector<ContainerServerPacketTracer> Listeners = new Vector<>();



    @Override
    public void updateEntity() {
        super.updateEntity();

        BlockServerSecurity block = null;
        if(worldObj.getBlock(xCoord,yCoord,zCoord) instanceof BlockServerSecurity)
          block = (BlockServerSecurity)worldObj.getBlock(xCoord,yCoord,zCoord);
        if(theGridNode != null && block != null)
            block.isActive = theGridNode.isActive();
        if(!hasMaster()){
            if(theGridNode == null)
                return;
            if(theGridNode.getGrid() == null)
                return;
            IGrid grid = theGridNode.getGrid();
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
            return new ContainerServerPacketTracer(getMaster(),player);
    }
    @Override
    public Object getClientGuiElement( final EntityPlayer player )
    {
            return new ServerPacketTracer((ContainerServerPacketTracer)this.getServerGuiElement(player),getMaster(),player);
    }
    @Override
    public void createAELink() {
        if (!worldObj.isRemote) {
            if (theGridNode == null)
                theGridNode = AEApi.instance().createGridNode(this);
                theGridNode.updateState();
        }
    }
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }
    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        // TODO Auto-generated method stub
        EnumSet<ForgeDirection> set = EnumSet.noneOf(ForgeDirection.class);
        for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS){
            if(side != fw){
                set.add(side);
            }
        }
        return set;
    }

    @Override
    public void invalidate() {
        if (worldObj != null && !worldObj.isRemote) {
            destroyAELink();
        }
        if(hasMaster()){
            getMaster().Slaves.remove(this);
            master.MainNetwork = null;
        }
    }

    @Override
    public void onChunkUnload() {
        if (worldObj != null && !worldObj.isRemote) {
            destroyAELink();
        }

    }
    public void notifyBlock(){
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }


}
