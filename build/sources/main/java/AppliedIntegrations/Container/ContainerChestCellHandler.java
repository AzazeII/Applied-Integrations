package AppliedIntegrations.Container;

import AppliedIntegrations.API.Grid.ICraftingIssuerHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.EffectiveSide;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ISaveProvider;
import appeng.me.helpers.BaseActionSource;
import appeng.me.helpers.PlayerSource;
import appeng.tile.storage.TileChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


/**
 * @Author Azazell
 */
public class ContainerChestCellHandler
        extends ContainerEnergyTerminalDuality
{
    /**
     * The ME chest the cell is stored in.
     */
    private final TileChest hostChest;

    /**
     * Network source representing the player who is interacting with the
     * container.
     */
    private PlayerSource playerSource = null;

    /**
     * Compiler safe reference to the TileChest when using the
     * ISaveProvider interface.
     */
    private ISaveProvider chestSaveProvider;

    private LiquidAIEnergy tmpSelectedEnergy;

    /**
     * Import and export inventory slots.
     */
    private AIGridNodeInventory privateInventory = new AIGridNodeInventory( AppliedIntegrations.modid + ".item.energy.cell.inventory", 2, 64 )
    {
        @Override
        public boolean isItemValidForSlot( final int slotID, final ItemStack itemStack )
        {
            return Utils.getEnergyFromItemStack(itemStack) != null;
        }
    };

    /**
     * Creates the container.
     *
     * @param player
     * The player that owns this container.
     * @param world
     * The world the ME chest is in.
     * @param x
     * X position of the ME chest.
     * @param y
     * Y position of the ME chest.
     * @param z
     * Z position of the ME chest.
     */
    public ContainerChestCellHandler(final EntityPlayer player, final World world, final int x, final int y, final int z )
    {
        // Call the super-constructor
        super( player );

        // Get the tile entity for the chest
        this.hostChest = (TileChest)world.getTileEntity( new BlockPos(x, y, z));

        // Is this server side?
        if( EffectiveSide.isServerSide() )
        {
            /*
             * Note: Casting the hostChest to an object is required to prevent the compiler
             * from seeing the soft-dependencies of AE2, such a buildcraft, which it attempts
             * to resolve at compile time.
             * */
            Object hostObject = this.hostChest;
            this.chestSaveProvider = ( (ISaveProvider)hostObject );

            // Create the action source
            this.playerSource = new PlayerSource( this.player, (IActionHost)hostObject );
        }
        else
        {
            // Request a full update from the server
       //     Packet_S_EnergyCellTerminal.sendFullUpdateRequest( player );
        }

        // Bind our inventory
        this.bindToInventory( this.privateInventory );

    }

    /**
     * Gets a handler for the energy cell.
     *
     * @return
     */
    //private HandlerItemEnergyCell getCellHandler()
    //{
        // Ensure we have a host
 /*       if( this.hostChest == null )
        {
            return null;
        }

        // Get the cell
        ItemStack EnergyCell = this.hostChest.getStackInSlot( 1 );

        // Ensure we have the cell
        if( ( EnergyCell == null ) || !( EnergyCell.getItem() instanceof EnergyStorageCell) )
        {
            return null;
        }

        // Get the handler
        return new HandlerItemEnergyCell( EnergyCell, this.chestSaveProvider );return null;
    */

    @Override
    protected BaseActionSource getActionSource()
    {
        return null;
    }

    @Override
    protected IGrid getHostGrid()
    {
        try
        {
            return this.hostChest.getActionableNode().getGrid();
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    protected LiquidAIEnergy getHostSelectedEnergy()
    {
        return this.tmpSelectedEnergy;
    }

    @Override
    protected IMEMonitor<IAEEnergyStack> getNewMonitor()
    {
        try
        {
            IMEInventoryHandler<IAEEnergyStack> handler = null;

            // Get the chest handler
            List<IMEInventoryHandler> hostCellArray = this.hostChest.getCellArray(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class));
            if( hostCellArray.size() > 0 )
            {
                handler = hostCellArray.get( 0 );
            }

            // Get the monitor
            if( handler != null )
            {
                // Create the energy monitor
                return (IMEMonitor<IAEEnergyStack>)handler;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void setHostSelectedEnergy( final LiquidAIEnergy energy )
    {
        this.tmpSelectedEnergy = energy;
    }

    @Override
    public boolean canInteractWith( final EntityPlayer player )
    {
        if( this.hostChest != null )
        {
          return true;//return this.hostChest.isUseableByPlayer( player );
        }
        return false;
    }

    /**
     * Transfers energy.
     */
    @Override
    public void doWork( final int elapsedTicks )
    {
        this.transferEnergyFromWorkSlots();
    }

    @Override
    public ICraftingIssuerHost getCraftingHost()
    {
        // Can't craft.
        return null;
    }

    /**
     * Gets the current list from the AE monitor and sends
     * it to the client.
     */
    @Override
    public void onClientRequestFullUpdate()
    {
        // Get the handler
       // HandlerItemEnergyCell cellHandler = this.getCellHandler();

        // Did we get the handler?
      /*  if( cellHandler != null )
        {
            // Send the viewing mode
            Packet_C_EnergyCellTerminal.sendViewingModes( this.player, cellHandler.getSortingMode(), cellHandler.getViewMode() );
        }

        // Send the list
        if( this.hostChest.isPowered() )
        {
            Packet_C_EnergyCellTerminal.sendFullList( this.player, this.repo.getAll() );
        }
        else
        {
            Packet_C_EnergyCellTerminal.sendFullList( this.player, new ArrayList<IEnergyStack>() );

        }*/
    }

    @Override
    public void onClientRequestSortModeChange( final EntityPlayer player, final boolean backwards )
    {
        // Get the handler
       /* HandlerItemEnergyCell cellHandler = this.getCellHandler();
        if( cellHandler != null )
        {
            // Change the sorting mode
            SortMode sortingMode;
            if( backwards )
            {
                sortingMode = cellHandler.getSortingMode().previousMode();
            }
            else
            {
                sortingMode = cellHandler.getSortingMode().nextMode();
            }

            // Inform the handler of the change
            cellHandler.setSortingMode( sortingMode );

            // Send confirmation back to client
            Packet_C_EnergyCellTerminal.sendViewingModes( player, sortingMode, cellHandler.getViewMode() );
        }*/
    }

    @Override
    public void onClientRequestViewModeChange( final EntityPlayer player, final boolean backwards )
    {
        // Get the handler
     /*   HandlerItemEnergyCell cellHandler = this.getCellHandler();
        if( cellHandler != null )
        {

            // Change the view mode
            ViewItems viewMode = Platform.rotateEnum( cellHandler.getViewMode(), backwards, Settings.VIEW_MODE.getPossibleValues() );

            // Inform the handler of the change
            cellHandler.setViewMode( viewMode );

            // Send confirmation back to client
            Packet_C_EnergyCellTerminal.sendViewingModes( player, cellHandler.getSortingMode(), viewMode );
        }*/
    }

    /**
     * Drops any items in the import and export inventory.
     */
    @Override
    public void onContainerClosed( final EntityPlayer player )
    {
        // Drop anything in the work slots.
        if( EffectiveSide.isServerSide() )
        {
            for( int i = 0; i < 2; i++ ) {
            }
        }

        // Call super
        super.onContainerClosed( player );
    }



    @Override
    public boolean onFilterReceive(AIPart part) {
        return false;
    }

    @Override
    public void postChange(IBaseMonitor<IAEEnergyStack> iBaseMonitor, Iterable<IAEEnergyStack> iterable, IActionSource iActionSource) {

    }

    @Override
    public void onListUpdate() {

    }
}
