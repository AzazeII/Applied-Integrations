package AppliedIntegrations.Parts;

import AppliedIntegrations.API.Grid.ICraftingIssuerHost;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.GuiEnergyTerminalDuality;
import AppliedIntegrations.Gui.SortMode;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketTerminalChange;
import AppliedIntegrations.Render.TextureManager;
import AppliedIntegrations.Utils.AIPrivateInventory;
import appeng.api.config.ViewItems;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEColor;
import appeng.api.util.IConfigManager;
import appeng.client.texture.CableBusTextures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

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

	private AIPrivateInventory inventory = new AIPrivateInventory( AppliedIntegrations.modid + ".part.Energy.terminal", 2, 64 )
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
	protected AIPrivateInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public int cableConnectionRenderTo()
	{
		return 1;
	}

	@Override
	public void getBoxes( final IPartCollisionHelper helper )
	{
		helper.addBox( 2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D );

		helper.addBox( 4.0D, 4.0D, 13.0D, 12.0D, 12.0D, 14.0D );

		helper.addBox( 5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 13.0D );
	}

	@Override
	public IIcon getBreakingTexture()
	{
		return TextureManager.ENERGY_TERMINAL.getTextures()[3];
	}

	@Override
	public Object getClientGuiElement( final EntityPlayer player )
	{
		return new GuiEnergyTerminalDuality((ContainerEnergyTerminal)this.getServerGuiElement(player),this,player);
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



	/**
	 * Determines how much power the part takes for just
	 * existing.
	 */
	@Override
	public double getIdlePowerUsage()
	{
		return PartEnergyTerminal.IDLE_POWER_DRAIN;
	}

	public AIPrivateInventory getInventory()
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
	public Object getServerGuiElement( final EntityPlayer player )
	{
		return new ContainerEnergyTerminal( player, this );
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
	public boolean onActivate(EntityPlayer player, Vec3 position) {

			if(isActive()) {
				player.openGui(AppliedIntegrations.instance, 7, this.getHostTile().getWorldObj(), this.getHostTile().xCoord, this.getHostTile().yCoord, this.getHostTile().zCoord);
				this.ShouldUpdate = true;
			}

		return true;
	}



	/**
	 * Called to read our saved state
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

	@SideOnly(Side.CLIENT)
	@Override
	public void renderInventory( final IPartRenderHelper helper, final RenderBlocks renderer )
	{
		Tessellator ts = Tessellator.instance;

		IIcon side = TextureManager.ENERGY_TERMINAL.getTextures()[3];
		helper.setTexture(side);
		helper.setBounds(4, 4, 13, 12, 12, 14);
		helper.renderInventoryBox(renderer);
		helper.setTexture(side, side, side, TextureManager.BUS_BACK.getTexture(),
				side, side);
		helper.setBounds(2, 2, 14, 14, 14, 16);
		helper.renderInventoryBox(renderer);

		ts.setBrightness(13 << 20 | 13 << 4);

		helper.setInvColor(0xFFFFFF);
		helper.renderInventoryFace(TextureManager.BUS_BACK.getTexture(),
				ForgeDirection.SOUTH, renderer);

		helper.setBounds(3, 3, 15, 13, 13, 16);
		helper.setInvColor(AEColor.Transparent.blackVariant);
		helper.renderInventoryFace(TextureManager.ENERGY_TERMINAL.getTextures()[0],
				ForgeDirection.SOUTH, renderer);
		helper.setInvColor(AEColor.Transparent.mediumVariant);
		helper.renderInventoryFace(TextureManager.ENERGY_TERMINAL.getTextures()[1],
				ForgeDirection.SOUTH, renderer);
		helper.setInvColor(AEColor.Transparent.whiteVariant);
		helper.renderInventoryFace(TextureManager.ENERGY_TERMINAL.getTextures()[2],
				ForgeDirection.SOUTH, renderer);

		helper.setBounds(5, 5, 12, 11, 11, 13);
		renderInventoryBusLights(helper, renderer);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderStatic( final int x, final int y, final int z, final IPartRenderHelper helper, final RenderBlocks renderer )
	{
		Tessellator tessellator = Tessellator.instance;

		IIcon side = TextureManager.ENERGY_TERMINAL.getTextures()[3];

		// Main block
		helper.setTexture( side, side, side, side, side, side );
		helper.setBounds( 2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F );
		helper.renderBlock( x, y, z, renderer );

		// Light up if active
		if( this.isActive() )
		{
			Tessellator.instance.setBrightness( super.ACTIVE_FACE_BRIGHTNESS );
		}

		// Dark corners
		tessellator.setColorOpaque_I( this.getHost().getColor().blackVariant );
		helper.renderFace( x, y, z, PartEnergyStorageMonitor.darkCornerTexture.getIcon(), ForgeDirection.SOUTH, renderer );

		// Light corners
		tessellator.setColorOpaque_I( this.getHost().getColor().mediumVariant );
		helper.renderFace( x, y, z, PartEnergyStorageMonitor.lightCornerTexture.getIcon(), ForgeDirection.SOUTH, renderer );

		// Main face
		tessellator.setColorOpaque_I( this.getHost().getColor().whiteVariant );
		helper.renderFace( x, y, z, CableBusTextures.PartConversionMonitor_Bright.getIcon(), ForgeDirection.SOUTH, renderer );

		tessellator.setColorOpaque_I( this.getHost().getColor().mediumVariant );
		helper.renderFace( x, y, z, TextureManager.ENERGY_TERMINAL.getTexture(), ForgeDirection.SOUTH, renderer );
		// Borders
		helper.renderFace( x, y, z, TextureManager.ENERGY_TERMINAL.getTextures()[4], ForgeDirection.SOUTH, renderer );
		// Cable lights
		helper.setBounds( 5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F );
		this.renderStaticBusLights( x, y, z, helper, renderer );

	}

	/**
	 * Called to save our state
	 */
	@Override
	public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
	{
		// Call super
		super.writeToNBT( data, saveType );

		// Only write NBT data if saving, or wrenched.
		if( ( saveType != PartItemStack.World ) && ( saveType != PartItemStack.Wrench ) )
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
	// all next methods ignored
	@Override
	public void updateWatcher(IStackWatcher newWatcher) {

	}

	@Override
	public void onStackChange(IItemList o, IAEStack fullStack, IAEStack diffStack, BaseActionSource src, StorageChannel chan) { }

	@Override
	public ItemStack getIcon() {
		return null;
	}

	@Override
	public void launchGUI(EntityPlayer player) {

	}

	@Override
	public IMEMonitor<IAEItemStack> getItemInventory() {
		return null;
	}

	@Override
	public IMEMonitor<IAEFluidStack> getFluidInventory() {
		return this.getEnergyProvidingInventory();
	}

	@Override
	public IConfigManager getConfigManager() {
		return null;
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
}