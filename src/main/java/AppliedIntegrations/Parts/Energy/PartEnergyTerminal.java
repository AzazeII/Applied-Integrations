package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Grid.ICraftingIssuerHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.GuiEnergyTerminalDuality;
import AppliedIntegrations.Gui.SortMode;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketTerminalChange;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.config.ViewItems;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static appeng.api.networking.ticking.TickRateModulation.IDLE;

/**
 * @Author Azazell
 */
public class PartEnergyTerminal
		extends AIRotatablePart implements IStackWatcherHost,ICraftingIssuerHost,IGridTickable
{

	/**
	 * How much AE power is required to keep the part active.
	 */
	private static final double IDLE_POWER_DRAIN = 0.5D;

	/**
	 * NBT Keys
	 */
	private static final String NBT_KEY_SORT_MODE = "sortMode", NBT_KEY_INVENTORY = "slots", NBT_KEY_VIEW_MODE = "ViewMode";

	/**
	 * Default sorting mode for the terminal.
	 */
	private static final SortMode DEFAULT_SORT_MODE = SortMode.ALPHABETIC;

	/**
	 * Default view mode for the terminal.
	 */
	private static final ViewItems DEFAULT_VIEW_MODE = ViewItems.ALL;

	/**
	 * List of currently opened containers.
	 */
	private List<ContainerEnergyTerminal> listeners = new ArrayList<ContainerEnergyTerminal>();

	/**
	 * The sorting mode used to display Energies.
	 */
	private SortMode Current = DEFAULT_SORT_MODE;

	/**
	 * The viewing mode used to display Energies.
	 */
	private ViewItems viewMode = DEFAULT_VIEW_MODE;

	/**
	 * The selected Energy in the GUI.
	 * Only stored while the part is loaded.
	 */
	public LiquidAIEnergy selectedEnergy = null;

	private AIGridNodeInventory inventory = new AIGridNodeInventory( AppliedIntegrations.modid + ".part.Energy.terminal", 2, 64 )
	{
		@Override
		public boolean isItemValidForSlot( final int slotId, final ItemStack itemStack )
		{
			return Utils.getEnergyFromItemStack(itemStack) != null;
		}
	};
	private boolean ShouldUpdate = false;

	public PartEnergyTerminal()
	{
		super( PartEnum.EnergyTerminal );
	}

	/**
	 * Informs all open containers to update their respective clients
	 * that the mode has changed.
	 */
	private void notifyListenersOfModeChanged()
	{
		for( ContainerEnergyTerminal listener : this.listeners )
		{

		}
	}
	private void changeListenersStorage(){
		for( ContainerEnergyTerminal listener : this.listeners ){
			NetworkHandler.sendTo(new PacketTerminalChange(this.getEnergyProvidingInventory().getStorageList()),(EntityPlayerMP)listener.player);
		}
	}

	public void addListener( final ContainerEnergyTerminal container )
	{
		if( container instanceof ContainerEnergyTerminal )
		{
			this.listeners.add( (ContainerEnergyTerminal)container );
		}
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}


	@Override
	public void getBoxes( final IPartCollisionHelper helper )
	{
		helper.addBox( 2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D );
		helper.addBox( 4.0D, 4.0D, 13.0D, 12.0D, 12.0D, 14.0D );
		helper.addBox( 5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 13.0D );
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered())
			if (this.isActive())
				return PartModelEnum.TERMINAL_HAS_CHANNEL;
			else
				return PartModelEnum.TERMINAL_ON;
		return PartModelEnum.TERMINAL_OFF;
	}

	@Override
	public void getDrops( final List<ItemStack> drops, final boolean wrenched )
	{
		// Inventory is saved when wrenched.
		if( wrenched )
		{
			return;
		}

		// Loop over inventory
		for( int slotIndex = 0; slotIndex < 2; slotIndex++ )
		{
			// Get the stack at this index
			ItemStack slotStack = this.inventory.getStackInSlot( slotIndex );

			// Did we get anything?
			if( slotStack != null )
			{
				// Add to drops
				drops.add( slotStack );
			}
		}
	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 2;
	}


	/**
	 * Determines how much power the part takes for just
	 * existing.
	 */
	@Override
	public double getIdlePowerUsage()
	{
		return PartEnergyTerminal.IDLE_POWER_DRAIN;
	}

	public AIGridNodeInventory getInventory()
	{
		return this.inventory;
	}

	/**
	 * Light level based on if terminal is active.
	 */
	@Override
	public int getLightLevel()
	{
		return( this.isActive() ? AIPart.ACTIVE_TERMINAL_LIGHT_LEVEL : 0 );
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	/**
	 * Gets the current sorting mode
	 *
	 * @return
	 */
	public SortMode getSortingMode()
	{
		return this.Current;
	}

	/**
	 * Gets the view mode.
	 *
	 * @return
	 */
	public ViewItems getViewMode()
	{
		return this.viewMode;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d position) {

			if(isActive()) {
				player.openGui(AppliedIntegrations.instance, 7, this.getHostTile().getWorld(), this.getHostTile().getPos().getX()
						, this.getHostTile().getPos().getY(), this.getHostTile().getPos().getZ());
				this.ShouldUpdate = true;
			}

		return true;
	}



	/**
	 * Called to read our saved stateProp
	 */
	@Override
	public void readFromNBT( final NBTTagCompound data )
	{
		// Call super
		super.readFromNBT( data );

		// Read the sorting mode
		if( data.hasKey( NBT_KEY_SORT_MODE ) )
		{
			//this.sortMode = EnergyStackComparator.EnergyStackComparatorMode.VALUES[data.getInteger( NBT_KEY_SORT_MODE )];
		}


		
	}

	public void removeListener( final ContainerEnergyTerminal containerEnergyTerminal )
	{
		this.listeners.remove( containerEnergyTerminal );
	}

	/**
	 * Called to save our stateProp
	 */
	@Override
	public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
	{
		// Call super
		super.writeToNBT( data, saveType );

		// Only write NBT data if saving, or wrenched.
		if( ( saveType != PartItemStack.WORLD ) && ( saveType != PartItemStack.WRENCH ) )
		{
			return;
		}

		// Write the sorting mode
	//	if( this.sortMode != DEFAULT_SORT_MODE )
		{
		//	data.setInteger( NBT_KEY_SORT_MODE, this.sortMode.ordinal() );
		}

		// Write view mode
		if( this.viewMode != DEFAULT_VIEW_MODE )
		{
			data.setInteger( NBT_KEY_VIEW_MODE, this.viewMode.ordinal() );
		}
	}

	@Override
	public  <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> chan) {
		// Getting Node
		if (getGridNode(AEPartLocation.INTERNAL) == null)
			return null;
		// Getting net of node
		IGrid grid = getGridNode(AEPartLocation.INTERNAL).getGrid();
		if (grid == null)
			return null;
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		if (storage == null)
			return null;
		// fluidInventory of cache
		return storage.getInventory(chan);
	}

	@Override
	public TickingRequest getTickingRequest(IGridNode node) {
		return new TickingRequest(1,1,false,false);
	}

	@Override
	public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall) {
		this.changeListenersStorage();
		return IDLE;
	}

	// all next methods ignored
	@Override
	public void updateWatcher(IStackWatcher newWatcher) {

	}

	@Override
	public void onStackChange(IItemList<?> iItemList, IAEStack<?> iaeStack, IAEStack<?> iaeStack1, IActionSource iActionSource, IStorageChannel<?> iStorageChannel) {

	}

	@Override
	public ItemStack getIcon() {
		return null;
	}

	@Override
	public void launchGUI(EntityPlayer player) {

	}


	@Override
	public IConfigManager getConfigManager() {
		return null;
	}

}