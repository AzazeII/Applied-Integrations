package AppliedIntegrations.tile;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketMasterSync;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @Author Azazell
 */
public class AIServerMultiBlockTile extends AITile implements IAIMultiBlock {

    private static final String KEY_FORMED = "#HasMaster";
    private static final String KEY_MASTER = "#Master";

    protected TileServerCore master;

    @Override
    public void tryConstruct(EntityPlayer p) {
        for (EnumFacing side : EnumFacing.values()) {
            if (world.getTileEntity(new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2,
                    getPos().getZ() + side.getFrontOffsetZ() * 2)) instanceof TileServerCore) {
                TileServerCore tile = (TileServerCore) world.getTileEntity(
                        new BlockPos(getPos().getX() + side.getFrontOffsetX() * 2, getPos().getY() + side.getFrontOffsetY() * 2, getPos().getZ() + side.getFrontOffsetZ() * 2));
                tile.tryConstruct(p);
                break;
            }
        }
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        if(hasMaster())
            return EnumSet.allOf(EnumFacing.class);
        return EnumSet.noneOf(EnumFacing.class);
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

        // Notify server
        NetworkHandler.sendToDimension(new PacketMasterSync(this, master), world.provider.getDimension());
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
