package AppliedIntegrations.Items.StorageCells;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import appeng.core.localization.GuiText;
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

public class ManaStorageCell extends AIItemRegistrable implements IStorageCell<IAEManaStack>, IBotaniaIntegrated {
    private int maxBytes;

    public ManaStorageCell(String registry, int maxBytes) {
        super(registry);
        this.maxBytes = maxBytes;
        this.setMaxStackSize(1);
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void addInformation( final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips )
    {
        ICellInventoryHandler<IAEManaStack> inventoryHandler = AEApi.instance().registries().cell().getCellInventory( stack, null, this.getChannel());
        if(inventoryHandler != null) {
            final ICellInventory<?> cellInventory = inventoryHandler.getCellInv();

            if (cellInventory != null) {
                // Show only bytes, since mana is only material which can be stored, and there is only 1 type available
                lines.add(cellInventory.getUsedBytes() + " " + GuiText.Of.getLocal() + ' ' + cellInventory.getTotalBytes() + ' ' + GuiText.BytesUsed.getLocal());
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            // Get the list of stored energies
            IItemList<IAEManaStack> cellEnergies = inventoryHandler.getAvailableItems(getChannel().createList());
            for( IAEManaStack currentStack : cellEnergies )
            {
                if( currentStack != null )
                {
                    // Add to the list
                    String energyInfo = TextFormatting.AQUA.toString() + "Mana x " + currentStack.getStackSize();
                    lines.add( energyInfo );
                }
            }
        } else {
            // Let the user know they can hold shift
            lines.add(TextFormatting.WHITE.toString() + "Hold" + TextFormatting.DARK_AQUA.toString() + " Shift " + TextFormatting.WHITE.toString() + "for");
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
    public boolean isBlackListed(@Nonnull ItemStack itemStack, @Nonnull IAEManaStack iaeManaStack) {
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
    public IStorageChannel<IAEManaStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
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
