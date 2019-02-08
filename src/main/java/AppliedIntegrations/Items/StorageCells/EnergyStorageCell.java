package AppliedIntegrations.Items.StorageCells;
import AppliedIntegrations.API.AIApi;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import com.google.common.base.Preconditions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

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

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.isSneaking())
			return super.onItemRightClick(world, player, hand);
		ItemStack held = player.getHeldItem(hand);
		if (held.isEmpty())
			return super.onItemRightClick(world, player, hand);
		ICellInventoryHandler<IAEEnergyStack> handler = AEApi.instance().registries().cell().getCellInventory(held, null, this.getChannel());
		if (handler == null)
			throw new NullPointerException("Couldn't get ICellInventoryHandler for Essentia Cell");
		if (!handler.getAvailableItems(this.getChannel().createList()).isEmpty()) // Only try to separate cell if empty
			return super.onItemRightClick(world, player, hand);

		Optional<ItemStack> cellComponent = this.getComponentOfCell(held);
		Optional<ItemStack> emptyCasing = AEApi.instance().definitions().materials().emptyStorageCell().maybeStack(1);
		if (!cellComponent.isPresent() || !emptyCasing.isPresent())
			return super.onItemRightClick(world, player, hand);

		InventoryPlayer inv = player.inventory;

		if (hand == EnumHand.MAIN_HAND) // Prevent accidental deletion when in off hand
			inv.setInventorySlotContents(inv.currentItem, ItemStack.EMPTY);
		if (!inv.addItemStackToInventory(cellComponent.get()))
			player.dropItem(cellComponent.get(), false);

		if (!inv.addItemStackToInventory(emptyCasing.get()))
			player.dropItem(emptyCasing.get(), false);

		if (player.inventoryContainer != null)
			player.inventoryContainer.detectAndSendChanges();

		return ActionResult.newResult(EnumActionResult.SUCCESS, ItemStack.EMPTY);
	}

	private Optional<ItemStack> getComponentOfCell(ItemStack stack) {
		return null;
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
		return maxBytes;
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
