package AppliedIntegrations.Items;

import AppliedIntegrations.AppliedIntegrations;
import appeng.api.config.AccessRestriction;
import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class itemWirelessTerminal extends Item implements INetworkEncodable,IAEItemPowerStorage,IEnergyContainerItem {
    private static String EncryptionKey;

    private static int MaxStorage = 32000;
    private double currentStorage=0;
    private EnergyStorage storage = new EnergyStorage(32000,1000);

    public itemWirelessTerminal(){

        this.setTextureName(AppliedIntegrations.modid+":EnergyWirelessTerminal");

        this.setUnlocalizedName("WirelessEnergyTerminal");

        this.setMaxStackSize(1);

    }
    @Override
    public void addInformation(final ItemStack is, final EntityPlayer player, final List displayList, final boolean advancedItemTooltips ) {
        displayList.add(StatCollector.translateToLocal("Energy Stored")+": "+this.getAECurrentPower(is)+" - "+(this.getAECurrentPower(is)/this.getAEMaxPower(is))*100+"%");
        if(this.EncryptionKey != null){
            displayList.add(StatCollector.translateToLocal("Linked"));
        }else{
            displayList.add(StatCollector.translateToLocal("UnLinked"));
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
    public double injectAEPower(ItemStack is, double amt) {
        return currentStorage += amt;
    }

    @Override
    public double extractAEPower(ItemStack is, double amt) {
        return currentStorage -= amt;
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
