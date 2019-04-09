package AppliedIntegrations.Items;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.energy.EnergyStorage;

import java.util.List;

/**
 * @Author Azazell
 */
public class ItemWirelessTerminal extends AIItemRegistrable implements INetworkEncodable,IAEItemPowerStorage {
    private static String EncryptionKey;

    private static int MaxStorage = 32000;
    private double currentStorage=0;
    private EnergyStorage storage = new EnergyStorage(32000,1000);

    public ItemWirelessTerminal(String name){
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
        return EncryptionKey;
    }

    @Override
    public void setEncryptionKey(ItemStack item, String encKey, String name) {
        EncryptionKey = encKey;
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
}
