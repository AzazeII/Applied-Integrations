package AppliedIntegrations.tile;

import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.Blocks.BlocksEnum;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.*;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkEvent;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
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
        @Optional.Interface(iface = "Reika.RotaryCraft.api.Interfaces.Transducerable",modid = "RotaryCraft",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.api.Power.AdvancedShaftPowerReceiver",modid = "RotaryCraft",striprefs = true)})

/**
 * @Author Azazell
 */
public abstract class AITile extends TileEntity implements IActionHost,IGridHost,IGridBlock, ITickable, IGridProxyable, ISyncHost {

    protected IGridNode gridNode = null;
    protected IGridNode node = null;

    protected boolean loaded = false;
    private AENetworkProxy proxy;

    public AITile(){
        for (BlocksEnum blocksEnum : BlocksEnum.values()) {
            if(blocksEnum.tileEnum.clazz == this.getClass()) {
                this.proxy = new AENetworkProxy(this, "AITile",
                        new ItemStack(blocksEnum.b), true);
            }
        }
    }

    public Object getServerGuiElement( final EntityPlayer player ){ return null; }
    public Object getClientGuiElement( final EntityPlayer player ){ return null; }

    public void postCellInventoryEvent(){
        // Pass call to overridden function
        postCellEvent(new MENetworkCellArrayUpdate());
    }

    public void postCellEvent(MENetworkEvent event){
        // Get node
        IGridNode node = getGridNode(AEPartLocation.INTERNAL);

        // Check not null
        if (node != null) {
            // Get grid
            IGrid grid = node.getGrid();

            // Post update
            postCellEvent(grid, event);
        }
    }

    private void postCellEvent(IGrid iGrid, MENetworkEvent event) {
        // Notify listeners of event change
        iGrid.postEvent(event);
    }

    public void postCellInventoryEvent(IGrid iGrid) {
        // Pass call to overridden function
        postCellEvent(iGrid, new MENetworkCellArrayUpdate());
    }

    @Override
    public double getIdlePowerUsage() {
        return 1;
    }

    @Nonnull
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean isWorldAccessible() {
        return true;
    }

    @Nonnull
    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Nonnull
    @Override
    public AEColor getGridColor() {
        return AEColor.TRANSPARENT;
    }

    @Override
    public void onGridNotification(GridNotification notification) {

    }
    @Override
    public void setNetworkStatus(IGrid grid, int channelsInUse) {

    }

    @Override
    public AENetworkProxy getProxy() {
        return proxy;
    }

    @Nonnull
    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.of(EnumFacing.SOUTH,EnumFacing.DOWN,EnumFacing.EAST,EnumFacing.UP,EnumFacing.NORTH,EnumFacing.WEST);
    }
    public void createAENode() {
        if(world != null) {
            if (!world.isRemote) {
                gridNode = AEApi.instance().grid().createGridNode(this);
                gridNode.updateState();
            }
        }
    }

    public void destroyAENode() {
        if (gridNode != null) gridNode.destroy();
    }

    @Nonnull
    @Override
    public IGridHost getMachine() {
        return this;
    }

    @Override
    public void gridChanged() {

    }

    @Override
    public ItemStack getMachineRepresentation() {
        DimensionalCoord location = this.getLocation();
        return new ItemStack(location.getWorld().getBlockState(new BlockPos(location.x, location.y, location.z)).getBlock(), 1,
                location.getWorld().getBlockState(new BlockPos(location.x, location.y, location.z)).getBlock().getMetaFromState((location.getWorld().getBlockState(new BlockPos(location.x, location.y, location.z)))));

    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote) {
            destroyAENode();
        }
    }

    @Override
    public void onChunkUnload() {
        if (world != null && !world.isRemote) {
            destroyAENode();
        }
    }

    public IGridNode getGridNode() {
        return getGridNode(AEPartLocation.INTERNAL);
    }

    @Override
    public IGridNode getGridNode(AEPartLocation dir) {
        if(gridNode == null)
            createAENode();
        return gridNode;
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(AEPartLocation dir) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public void securityBreak() {

    }

    @Nonnull
    @Override
    public IGridNode getActionableNode() {
        if (this.gridNode == null)
            createAENode();
        return gridNode;
    }

    @Override
    public void update() {
        //create grid node on add to world
        if (!loaded && hasWorld() && !world.isRemote) {
            loaded = true;
            createAENode();
        }
    }
    public void notifyBlock(){
    }

    protected IGrid getNetwork(){
        return getGridNode().getGrid();
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

        IStorageGrid storage = grid.getCache(IStorageGrid.class);

        IAEEnergyStack notRemoved = storage.getInventory(getEnergyChannel()).extractItems(
                getEnergyChannel().createStack(resource), actionable, new MachineSource(this));

        if (notRemoved == null)
            return (int)resource.amount;
        return (int)(resource.amount - notRemoved.getStackSize());
    }

    private IEnergyStorageChannel getEnergyChannel(){
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
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
        IGrid grid = node.getGrid();

        IStorageGrid storage = grid.getCache(IStorageGrid.class); // check storage gridnode

        IAEEnergyStack returnAmount = storage.getInventory(this.getEnergyChannel()).injectItems(
                getEnergyChannel().createStack(resource), actionable, new MachineSource(this));

        if (returnAmount == null)
            return (int)resource.amount;
        return (int) (resource.amount - returnAmount.getStackSize());
    }

    @Override
    public AEPartLocation getSide() {
        return AEPartLocation.INTERNAL;
    }
}
