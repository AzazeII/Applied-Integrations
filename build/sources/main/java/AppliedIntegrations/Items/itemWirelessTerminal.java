package AppliedIntegrations.Items;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.Optional;

import java.util.List;
@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux", striprefs = true)
})

public class itemWirelessTerminal extends AIItemRegistrable implements INetworkEncodable,IAEItemPowerStorage, IEnergyContainerItem {
    private static String EncryptionKey;

    private static int MaxStorage = 32000;
    private double currentStorage=0;
    private EnergyStorage storage = new EnergyStorage(32000,1000);

    public itemWirelessTerminal(String name){
        super(name);

        this.setMaxStackSize(1);

    }
    //@Override
    public void addInformation(final ItemStack is, final EntityPlayer player, final List displayList, final boolean advancedItemTooltips ) {
        displayList.add(I18n.translateToLocal("Energy Stored")+": "+this.getAECurrentPower(is)+" - "+(this.getAECurrentPower(is)/this.getAEMaxPower(is))*100+"%");
        if(this.EncryptionKey != null){
            displayList.add(I18n.translateToLocal("Linked"));
        }else{
            displayList.add(I18n.translateToLocal("UnLinked"));
        }
    }
    @Override
    public String getEncryptionKey(ItemStack item) {
        return this.EncryptionKey;
    }

    @Override
    public void setEncryptionKey(ItemStack item, String encKey, String name) {
        this.EncryptionKey = encKey;
    }

    @Override
    public double injectAEPower(ItemStack itemStack, double v, Actionable actionable) {
        return currentStorage += v;
    }

    @Override
    public double extractAEPower(ItemStack itemStack, double v, Actionable actionable) {
        return currentStorage -= v;
    }

    @Override
    public double getAEMaxPower(ItemStack is) {
        return this.currentStorage;
    }

    @Override
    public double getAECurrentPower(ItemStack is) {
        return this.MaxStorage;
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack is) {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive,simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract,simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return storage.getMaxEnergyStored();
    }
}
