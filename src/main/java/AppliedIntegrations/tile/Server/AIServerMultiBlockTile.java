package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketMasterSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

/**
 * @Author Azazell
 */
public class AIServerMultiBlockTile extends AITile implements IAIMultiBlock {
    protected TileServerCore master;

    private ChangeHandler<TileServerCore> masterChangeHandler = new ChangeHandler<>();

    @Override
    public void tryConstruct(EntityPlayer p) {
        for (EnumFacing side : EnumFacing.values()) {
            // Check if tile from two block from this block to direction of side
            if (world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2,
                    getPos().getZ() + side.getFrontOffsetZ() * 2)) instanceof TileServerCore) {
                // Get tile
                TileServerCore tile = (TileServerCore) world.getTileEntity(
                        new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2, getPos().getZ() + side.getFrontOffsetZ() * 2));

                // Check not null
                if (tile != null) {
                    // Pass call to core
                    tile.tryConstruct(p);
                }
                break;
            }
        }
    }

    @Override
    public void update() {
        super.update();

        // Call master change handler
        masterChangeHandler.onChange(master, (master) -> {
            // Notify server
            NetworkHandler.sendToDimension(new PacketMasterSync(this, master), world.provider.getDimension());
        });
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        if(hasMaster())
            return EnumSet.allOf(EnumFacing.class);
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public void createAENode() {
        // Run code only on server and check if tile has master
        if (!world.isRemote && hasMaster()) {
            // Check if node is null
            if (gridNode == null)
                // Initialized node
                gridNode = AEApi.instance().grid().createGridNode(this);
            // Update node status
            gridNode.updateState();
        }
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.noneOf(GridFlags.class);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (hasMaster())
            master.destroyMultiBlock();
    }

    @Override
    public boolean hasMaster() {
        return master!=null;
    }

    @Override
    public IMaster getMaster() {
        return master;
    }

    @Override
    public void setMaster(IMaster tileServerCore) {
        // Update master
        master = (TileServerCore)tileServerCore;
    }

    @Override
    public void notifyBlock(){

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return super.writeToNBT(tag);
    }
}
