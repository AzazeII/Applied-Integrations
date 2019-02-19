package AppliedIntegrations.Items.StorageCells;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.IAppEngApi;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ICellRegistry;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.IClientHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

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

	}

	@Override
	public int getBytes(@Nonnull ItemStack itemStack) {
		return maxBytes;
	}

	@Override
	public int getBytesPerType(@Nonnull ItemStack itemStack) {
		return 8;
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
		return null;
	}

	@Override
	public IItemHandler getConfigInventory(ItemStack itemStack) {
		return null;
	}

	@Override
	public FuzzyMode getFuzzyMode(ItemStack itemStack) {
		return FuzzyMode.IGNORE_ALL;
	}

	@Override
	public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {

	}
}
