package AppliedIntegrations.tile;

import AppliedIntegrations.API.EnergyStack;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.MachineSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;

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
public abstract class AITile extends TileEntity implements IActionHost,IGridHost,IGridBlock, ITickable {

    public IGridNode gridNode = null;
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
        return AEColor.TRANSPARENT;
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
    public EnumSet<EnumFacing> getConnectableSides() {
        // TODO Auto-generated method stub
        return EnumSet.of(EnumFacing.SOUTH,EnumFacing.DOWN,EnumFacing.EAST,EnumFacing.UP,EnumFacing.NORTH,EnumFacing.WEST);
    }
    public void createAELink() {
        if(world != null) {
            if (!world.isRemote) {
                if (gridNode == null) gridNode = AEApi.instance().grid().createGridNode(this);
                gridNode.updateState();
            }
        }
    }
    public void destroyAELink() {
        if (gridNode != null) gridNode.destroy();
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
        return world.isRemote;
    }
    @Override
    public ItemStack getMachineRepresentation() {
        DimensionalCoord location = this.getLocation();
        if (location == null)
            return null;
        return new ItemStack(location.getWorld().getBlockState(new BlockPos(location.x, location.y, location.z)).getBlock(), 1,
                location.getWorld().getBlockState(new BlockPos(location.x, location.y, location.z)).getBlock().getMetaFromState((location.getWorld().getBlockState(new BlockPos(location.x, location.y, location.z)))));

    }
    public IGridNode getGridNode() {
        return getGridNode(AEPartLocation.INTERNAL);
    }
    @Override
    public IGridNode getGridNode(AEPartLocation dir) {
        // TODO Auto-generated method stub
        if(gridNode ==null) createAELink();
        return gridNode;
    }

    @Override
    public AECableType getCableConnectionType(AEPartLocation dir) {
        // TODO Auto-generated method stub
        return AECableType.DENSE_SMART;
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
            this.node = AEApi.instance().grid().createGridNode(this.gridBlock);
        }
        return this.node;

    }
    @Override
    public void update() {
        //create grid node on add to world
        if (!loaded && hasWorld() && !world.isRemote) {
            loaded = true;
            createAELink();
        }
    }
    public void notifyBlock(){
    }

    protected IGrid getNetwork(){
        if(getGridNode() != null)
            return getGridNode().getGrid();
        return null;
    }

    /**
     * @param resource
     * 	Resource to be extracted
     * @param actionable
     * 	Simulate of Modulate?
     * @return
     * 	amount extracted
     */
    public int ExtractEnergy(EnergyStack resource, Actionable actionable) {
        if(node == null)
            return 0;
        IGrid grid = node.getGrid();
        if (grid == null) {
            AILog.info("Grid cannot be initialized, WTF?");
            return 0;
        }

        IStorageGrid storage = (IStorageGrid)grid.getCache(IStorageGrid.class);
        if (storage == null) {
            AILog.info("StorageGrid cannot be initialized, WTF?");
            return 0;
        }

        IAEEnergyStack notRemoved = (IAEEnergyStack)storage.getInventory(getChannel()).extractItems(
                getChannel().createStack(resource), actionable, new MachineSource(this));

        if (notRemoved == null)
            return (int)resource.amount;
        return (int)(resource.amount - notRemoved.getStackSize());
    }

    public IEnergyTunnel getChannel(){
        return AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class);
    }

    /**
     * @param resource
     * 	Resource to be injected
     * @param actionable
     * 	Simulate or modulate?
     * @return
     *  amount injected
     */
    public int InjectEnergy(EnergyStack resource, Actionable actionable) {
        if(node == null)
            return 0;
        IGrid grid = node.getGrid(); // check grid node
        if (grid == null) {
            AILog.info("Grid cannot be initialized, WTF?");
            return 0;
        }

        IStorageGrid storage = grid.getCache(IStorageGrid.class); // check storage gridnode
        if (storage == null && this.node.getGrid().getCache(IStorageGrid.class) == null) {
            AILog.info("StorageGrid cannot be initialized, WTF?");
            return 0;
        }

        IAEEnergyStack returnAmount = storage.getInventory(this.getChannel()).injectItems(
                getChannel().createStack(resource), actionable, new MachineSource(this));

        if (returnAmount == null)
            return (int)resource.amount;
        return (int) (resource.amount - returnAmount.getStackSize());
    }
}
