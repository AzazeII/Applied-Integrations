package AppliedIntegrations.Items.StorageCells;
import AppliedIntegrations.AEFeatures.GuiText;
import AppliedIntegrations.API.*;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Inventory.HandlerItemEnergyCell;

import AppliedIntegrations.Inventory.HandlerItemEnergyCellCreative;
import appeng.api.*;

import java.util.List;


import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.implementations.tiles.IMEChest;
import appeng.api.networking.security.PlayerSource;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEFluidStack;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;
/**
 * @Author Azazell
 */
public class EnergyStorageCell extends Item implements ICellHandler {
	private static final int CELL_STATUS_MISSING = 0, CELL_STATUS_HAS_ROOM = 1, CELL_STATUS_TYPES_FULL = 2, CELL_STATUS_FULL = 3;
	/*
	 * String array displays suffixes of cell E.t. Energy Cell 1k
	 */

	public static final String[] suffixes = { "1k", "4k", "16k", "64k", "256k", "1024k", "4096k", "16384k", "Creative" };
	/*
	 *  spaces-_-
	 */
	public static final int maxTypes(ItemStack itemStack){
		if(itemStack.getItemDamage() != 9){
			return 1;
		}else{
		    return Integer.MAX_VALUE;
		}
	}
	private void addContentsToCellDescription( final HandlerItemEnergyCell cellHandler, final List displayList, final EntityPlayer player )
	{
		// Get the list of stored energies
		List<IEnergyStack> cellEnergies = cellHandler.getStoredEnergy();
		for( IEnergyStack currentStack : cellEnergies )
		{
			if( currentStack != null )
			{
				// Add to the list
				String energyInfo = EnumChatFormatting.RED.toString() + currentStack.getEnergyName() + " x " + currentStack.getStackSize();
				displayList.add( energyInfo.toUpperCase() );
			}
		}

	}

	/**
	 * Creates the cell tooltip.
	 */
	@Override
	public void addInformation( final ItemStack energyCell, final EntityPlayer player, final List displayList, final boolean advancedItemTooltips )
	{
		// Get the contents of the cell
		IMEInventoryHandler<IAEFluidStack> handler = AEApi.instance().registries().cell()
				.getCellInventory( energyCell, null, StorageChannel.FLUIDS );

		// Ensure we have a cell inventory handler
		if( !( handler instanceof HandlerItemEnergyCell ) )
		{
			return;
		}

		// Cast to cell inventory handler
		HandlerItemEnergyCell cellHandler = (HandlerItemEnergyCell)handler;

		// Create the bytes tooltip
		String bytesTip = String.format(cellHandler.getUsedBytes()+ " "+StatCollector.translateToLocal("of")+" "+ cellHandler.getTotalBytes()+" "+StatCollector.translateToLocal("Used"));

		// Create the types tooltip
		String typesTip = String.format(cellHandler.getUsedTypes()+ " "+StatCollector.translateToLocal("of")+" "+ cellHandler.getTotalTypes()+" "+StatCollector.translateToLocal("Used") );

		displayList.add( bytesTip );
		displayList.add( typesTip );

		// Is the cell pre-formated?
		if( cellHandler.isPartitioned() )
		{
			displayList.add( GuiText.Partitioned.getLocal() );
		}
		if( cellHandler.getUsedTypes() > 0 ) {
			// Is shift being held?
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
				// Add information about the energy types in the cell
				this.addContentsToCellDescription(cellHandler, displayList, player);
			} else {
				// Let the user know they can hold shift
				displayList.add(EnumChatFormatting.WHITE.toString() + "Hold" + EnumChatFormatting.DARK_RED.toString() + " Shift " + EnumChatFormatting.WHITE.toString() + "for");
			}
		}

	}

	public static final int[] spaces = { 1024, 4096, 16348, 65536,262144,1048576,4194304,16777216,Integer.MAX_VALUE };
	private IIcon[] icons;
	public EnergyStorageCell() {
		// Add the handler to AE2
		AEApi.instance().registries().cell().addCellHandler( this );
		// Set max stack size to 1
		this.setMaxStackSize( 1 );

		// No damage
		this.setMaxDamage( 0 );

		// Has sub-types
		this.setHasSubtypes( true );
		

	this.setCreativeTab(AppliedIntegrations.AI);
	}
	public static int maxStorage(ItemStack is) {
		return spaces[is.getItemDamage()];
	}
	// icons
	@Override
	public IIcon getIconFromDamage(int dmg) {
		int j = MathHelper.clamp_int(dmg, 0, suffixes.length);
		return this.icons[j];
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab,List listSubItems) {
		for (int i = 0; i < suffixes.length; ++i) {
			listSubItems.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public EnumRarity getRarity(final ItemStack itemStack )
	{
		return AppliedIntegrations.LEGENDARY;
	}
	// names
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return  "ME " + suffixes[itemStack.getItemDamage()] + " Energy Cell";
	}
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		this.icons = new IIcon[suffixes.length];
	// Icons registry
		for (int i = 0; i < suffixes.length; ++i) {
			this.icons[i] = iconRegister.registerIcon(AppliedIntegrations.modid + ":EnergyCell." + suffixes[i]);
		}
	}

	@Override
	public boolean isCell(ItemStack is) {
		return is.getItem() == this;
	}

	@Override
	public IMEInventoryHandler getCellInventory(ItemStack is, ISaveProvider host, StorageChannel channel) {
		// Ensure the channel is fluid and there is an appropriate item.
		if( ( channel != StorageChannel.FLUIDS ) || !( is.getItem() instanceof EnergyStorageCell ) )
		{
			return null;
		}
		// Is the type creative?
		if( is.getItemDamage() == 8 )
		{
			// Return a creative handler.
			return new HandlerItemEnergyCellCreative( is, host );
		}

		// Return a standard handler.
		return new HandlerItemEnergyCell( is, host );
	}

	@Override
	public IIcon getTopTexture_Light() {
		return null;
	}

	@Override
	public IIcon getTopTexture_Medium() {
		return null;
	}

	@Override
	public IIcon getTopTexture_Dark() {
		return null;
	}

	@Override
	public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler, IMEInventoryHandler inv, ItemStack is, StorageChannel chan) {
		// Ensure this is the fluid channel
		if( chan != StorageChannel.FLUIDS )
		{
			return;
		}

		// Ensure we have a chest
		if( chest != null )
		{
			// Get a reference to the chest's inventories
			IStorageMonitorable monitorable = ( (IMEChest)chest ).getMonitorable( ForgeDirection.UNKNOWN, new PlayerSource( player, chest ) );

			// Ensure we got the inventories
			if( monitorable != null )
			{
				// Get the chest tile entity
				TileEntity chestEntity = (TileEntity)chest;

				// Show the terminal gui
				player.openGui(AppliedIntegrations.getInstance(),7,player.getEntityWorld(),((TileEntity) chest).xCoord,((TileEntity) chest).yCoord,((TileEntity) chest).zCoord);
			}
		}

	}

	@Override
	public int getStatusForCell(ItemStack is, IMEInventory handler) {
		// Do we have a handler?
		if( handler == null )
		{
			return this.CELL_STATUS_MISSING;
		}

		// Get the inventory handler
		HandlerItemEnergyCell cellHandler = (HandlerItemEnergyCell)handler;


		// Full bytes?
		if( cellHandler.getUsedBytes() == cellHandler.getTotalBytes() )
		{
			return this.CELL_STATUS_FULL;
		}

		// Full types?
		if( cellHandler.getUsedTypes() == cellHandler.getTotalTypes() )
		{
			return this.CELL_STATUS_TYPES_FULL;
		}

		return this.CELL_STATUS_HAS_ROOM;
	}

	@Override
	public double cellIdleDrain(ItemStack is, IMEInventory handler) {
		return is.getItemDamage()*0.5;
	}
}
