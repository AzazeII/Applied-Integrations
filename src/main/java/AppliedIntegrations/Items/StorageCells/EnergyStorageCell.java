package AppliedIntegrations.Items.StorageCells;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import appeng.items.contents.CellConfig;
import appeng.items.contents.CellUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @Author Azazell
 */
public class EnergyStorageCell extends AIItemRegistrable implements IStorageCell<IAEEnergyStack> {

	private int maxBytes;

	public EnergyStorageCell(String registry, int maxBytes) {
		super(registry);
		this.maxBytes = maxBytes;
		this.setMaxStackSize(1);
	}

	@SideOnly( Side.CLIENT )
	@Override
	public void addInformation( final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips )
	{
		ICellInventoryHandler<IAEEnergyStack> inventoryHandler = AEApi.instance().registries().cell().getCellInventory( stack, null, this.getChannel());
		AEApi.instance().client().addCellInformation( inventoryHandler, lines );

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
			// Get the list of stored energies
			IItemList<IAEEnergyStack> cellEnergies = inventoryHandler.getAvailableItems(getChannel().createList());
			for( IAEEnergyStack currentStack : cellEnergies )
			{
				if( currentStack != null )
				{
					// Add to the list
					String energyInfo = TextFormatting.RED.toString() + currentStack.getStack().getEnergyName() + " x " + currentStack.getStackSize();
					lines.add( energyInfo.toUpperCase() );
				}
			}
		} else {
			// Let the user know they can hold shift
			lines.add(TextFormatting.WHITE.toString() + "Hold" + TextFormatting.DARK_RED.toString() + " Shift " + TextFormatting.WHITE.toString() + "for");
		}
	}

	@Override
	public int getBytes(@Nonnull ItemStack itemStack) {
		return maxBytes;
	}

	@Override
	public int getBytesPerType(@Nonnull ItemStack itemStack) {
		return 1;
	}

	@Override
	public int getTotalTypes(@Nonnull ItemStack itemStack) {
		return 1;
	}

	@Override
	public boolean isBlackListed(@Nonnull ItemStack itemStack, @Nonnull IAEEnergyStack iaeEnergyStack) {
		return false;
	}

	@Override
	public boolean storableInStorageCell() {
		return false;
	}

	@Override
	public boolean isStorageCell(@Nonnull ItemStack itemStack) {
		return true;
	}

	@Override
	public double getIdleDrain() {
		return 1;
	}

	@Nonnull
	@Override
	public IStorageChannel<IAEEnergyStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class);
	}

	@Override
	public boolean isEditable(ItemStack itemStack) {
		return true;
	}

	@Override
	public IItemHandler getUpgradesInventory(ItemStack itemStack) {
		return new CellUpgrades(itemStack, 2);
	}

	@Override
	public IItemHandler getConfigInventory(ItemStack itemStack) {
		return new CellConfig( itemStack );
	}

	@Override
	public FuzzyMode getFuzzyMode(ItemStack itemStack) {
		return FuzzyMode.IGNORE_ALL;
	}

	@Override
	public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {

	}
}
