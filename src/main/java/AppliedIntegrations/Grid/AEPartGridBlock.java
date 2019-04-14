package AppliedIntegrations.Grid;

import AppliedIntegrations.Parts.AIPart;
import appeng.api.networking.*;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;
/**
 * @Author Azazell
 */
public class AEPartGridBlock
        implements IGridBlock
{
    /**
     * The part using this gridblock.
     */
    protected AIPart part;

    /**
     * Create the gridblock for the specified part.
     *
     * @param part
     */
    public AEPartGridBlock( final AIPart part )
    {
        this.part = part;
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides()
    {
        return EnumSet.noneOf( EnumFacing.class );
    }

    public IEnergyGrid getEnergyGrid()
    {
        // Get the Grid
        IGrid grid = this.getGrid();

        // Ensure we have a Grid
        if( grid == null )
        {
            return null;
        }

        // Return the energy Grid
        return grid.getCache( IEnergyGrid.class );
    }

    /**
     * Returns the energy monitor for the current Grid.
     *
     * @return
     */
    public IMEMonitor<IAEFluidStack> getEnergyMonitor()
    {
        // Get the Grid.
        IGrid grid = this.getGrid();

        // Ensure there is a Grid
        if( grid == null )
        {
            return null;
        }

        // Get the energy monitor from the cache.
        return grid.getCache(IStorageGrid.class);
    }

    /**
     * Return that we require a channel to function.
     */
    @Override
    public EnumSet<GridFlags> getFlags()
    {
        return EnumSet.of( GridFlags.REQUIRE_CHANNEL );
    }

    public final IGrid getGrid()
    {
        // Get the Grid node
        IGridNode node = this.part.getGridNode();

        // Ensure we have a node
        if( node != null )
        {
            // Get the Grid
            return node.getGrid();
        }

        return null;
    }

    /**
     * Returns the color of the Grid.
     */
    @Override
    public AEColor getGridColor()
    {
        // Return transparent.
        return AEColor.TRANSPARENT;
    }

    /**
     * Gets how much power the part is using.
     */
    @Override
    public double getIdlePowerUsage()
    {
        return this.part.getIdlePowerUsage();
    }

    /**
     * Gets the location of the part.
     */
    @Override
    public DimensionalCoord getLocation()
    {
        return this.part.getLocation();
    }

    /**
     * Gets the part
     */
    @Override
    public IGridHost getMachine()
    {
        return this.part;
    }

    /**
     * Gets the an itemstack based on the parts stateProp.
     */
    @Override
    public ItemStack getMachineRepresentation()
    {
        return this.part.getItemStack( PartItemStack.NETWORK );
    }

    /**
     * Gets the security Grid
     *
     * @return
     */
    public ISecurityGrid getSecurityGrid()
    {
        // Get the Grid.
        IGrid grid = this.getGrid();

        // Do we have a Grid?
        if( grid == null )
        {
            return null;
        }

        // Get the security Grid from the cache.
        return (ISecurityGrid)grid.getCache( ISecurityGrid.class );
    }

    /**
     * Gets the storage Grid.
     *
     * @return
     */
    public IStorageGrid getStorageGrid()
    {
        // Get the Grid.
        IGrid grid = this.getGrid();

        // Do we have a Grid?
        if( grid == null )
        {
            return null;
        }

        // Get the storage Grid from the cache.
        return (IStorageGrid)grid.getCache( IStorageGrid.class );
    }

    @Override
    public void gridChanged()
    {
        // Ignored
    }

    /**
     * Parts are not world accessable
     */
    @Override
    public boolean isWorldAccessible()
    {
        return false;
    }

    @Override
    public void onGridNotification( final GridNotification notification )
    {
        // Ignored
    }

    /**
     * Called to update the Grid and the channels used.
     */
    @Override
    public final void setNetworkStatus( final IGrid grid, final int usedChannels )
    {
    }

}