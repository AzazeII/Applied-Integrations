package AppliedIntegrations.Entities;

import AppliedIntegrations.Entities.Server.TileServerPort;
import AppliedIntegrations.Entities.Server.TileServerRib;
import appeng.api.AEApi;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPartHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

@Optional.InterfaceList(value = { // ()____()
        @Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IKineticSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IHeatSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.EnergyStorage",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyHandler",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.API.Interfaces.Transducerable",modid = "RotaryCraft",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver",modid = "RotaryCraft",striprefs = true)})
public abstract class AITile extends TileEntity implements IActionHost,IGridHost,IGridBlock {

    public IGridNode theGridNode = null;
    public IGridConnection theConnection;
    private IGridBlock gridBlock;
    private IGridNode node = null;

    protected boolean loaded = false;
    private boolean cached = false;

    public Object getServerGuiElement( final EntityPlayer player )
    {
        return null;
    }
    public Object getClientGuiElement( final EntityPlayer player )
    {
        return null;
    }

    /**
     * Prepare a Packet to send the state of the {@link TileEntity} to the Client
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    /**
     * Process a Packet to update the state of the {@link TileEntity} on the Client
     */
    @Override
    public void onDataPacket(NetworkManager pNet, S35PacketUpdateTileEntity pPacket) {
        super.onDataPacket(pNet, pPacket);
        NBTTagCompound nbt = pPacket.func_148857_g();
        readFromNBT(nbt);
    }
    @Override
    public double getIdlePowerUsage() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        // TODO Auto-generated method stub
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean isWorldAccessible() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public DimensionalCoord getLocation() {
        // TODO Auto-generated method stub
        return new DimensionalCoord(this);
    }

    @Override
    public AEColor getGridColor() {
        // TODO Auto-generated method stub
        return AEColor.Transparent;
    }

    @Override
    public void onGridNotification(GridNotification notification) {
        // TODO Auto-generated method stub

    }
    @Override
    public void setNetworkStatus(IGrid grid, int channelsInUse) {
        // TODO Auto-generated method stub

    }
    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        // TODO Auto-generated method stub
        return EnumSet.of(ForgeDirection.SOUTH,ForgeDirection.DOWN,ForgeDirection.EAST,ForgeDirection.UP,ForgeDirection.NORTH,ForgeDirection.WEST);
    }
    public void createAELink() {
        if (!worldObj.isRemote) {
            if (theGridNode == null) theGridNode = AEApi.instance().createGridNode(this);
            theGridNode.updateState();
        }
    }
    public void destroyAELink() {
        if (theGridNode != null) theGridNode.destroy();
    }
    @Override
    public IGridHost getMachine() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public void gridChanged() {
        // TODO Auto-generated method stub

    }
    public boolean isServer() {
        return !isClient();
    }

    public boolean isClient() {
        return getWorldObj().isRemote;
    }
    @Override
    public ItemStack getMachineRepresentation() {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        // TODO Auto-generated method stub
        if(theGridNode==null) createAELink();
        return theGridNode;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        // TODO Auto-generated method stub
        return AECableType.DENSE;
    }

    @Override
    public void securityBreak() {
        // TODO Auto-generated method stub

    }
    @Override
    public IGridNode getActionableNode() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return null;
        if (this.node == null) {
            this.node = AEApi.instance().createGridNode(this.gridBlock);
        }
        return this.node;

    }
    @Override
    public void updateEntity() {
        //create grid node on add to world
        if (!loaded && hasWorldObj() && !worldObj.isRemote) {
            loaded = true;
            createAELink();
        }
    }
    public void notifyBlock(){
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
}
