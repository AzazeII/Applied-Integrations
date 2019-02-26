package AppliedIntegrations.Items.Botania;

import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemEnum;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.features.INetworkEncodable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.util.IConfigManager;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;

import java.util.List;

public class MEManaMirror extends AIItemRegistrable implements IWirelessTermHandler, IAEItemPowerStorage, INetworkEncodable, IBotaniaIntegrated,  IManaItem, IManaTooltipDisplay{

    private double storage;
    private final double capacity = 16000;

    private int currentMana = 0;
    private int manaCapacity = 500000;
    private static String EncryptionKey;

    public MEManaMirror(String registry) {
        super(registry);
        this.setMaxStackSize(1);
        AEApi.instance().registries().wireless().registerWirelessHandler(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World w, final EntityPlayer player, final EnumHand hand )
    {
        AEApi.instance().registries().wireless().openWirelessTerminalGui( player.getHeldItem( hand ), w, player );
        return new ActionResult<>( EnumActionResult.SUCCESS, player.getHeldItem( hand ) );
    }

    @SideOnly( Side.CLIENT )
    @Override
    public boolean isFull3D()
    {
        return false;
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void addInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips )
    {
        lines.add(I18n.translateToLocal("Energy Stored")+": "+this.getAECurrentPower(stack)+" - "+(this.getAECurrentPower(stack)/this.getAEMaxPower(stack))*100+"%");
        if( stack.hasTagCompound() )
        {
            final NBTTagCompound tag = Platform.openNbtData( stack );
            if( tag != null )
            {
                final String encKey = tag.getString( "encryptionKey" );

                if( encKey == null || encKey.isEmpty() )
                {
                    lines.add( GuiText.Unlinked.getLocal() );
                }
                else
                {
                    lines.add( GuiText.Linked.getLocal() );
                }
            }
        }
        else
        {
            lines.add( I18n.translateToLocal( "AppEng.GuiITooltip.Unlinked" ) );
        }
    }

    @Override
    public double injectAEPower(ItemStack itemStack, double v, Actionable actionable) {
        double injected = Math.min(storage + v, capacity);
        if(actionable == Actionable.MODULATE){
            storage = injected;
        }
        return injected;
    }

    @Override
    public double extractAEPower(ItemStack itemStack, double v, Actionable actionable) {
        double extracted = Math.min(storage - v, capacity);
        if(actionable == Actionable.MODULATE){
            storage = extracted;
        }
        return extracted;
    }

    @Override
    public double getAEMaxPower(ItemStack itemStack) {
        return storage;
    }

    @Override
    public double getAECurrentPower(ItemStack itemStack) {
        return capacity;
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack itemStack) {
        return AccessRestriction.READ;
    }

    @Override
    public int getMana(ItemStack itemStack) {
        return currentMana;
    }

    @Override
    public int getMaxMana(ItemStack itemStack) {
        return manaCapacity;
    }

    @Override
    public void addMana(ItemStack itemStack, int i) {
        currentMana += i;
        if(currentMana > getMaxMana(itemStack))
            currentMana = getMaxMana(itemStack);
        if(currentMana < 0)
            currentMana = 0;
    }

    @Override
    public boolean canReceiveManaFromPool(ItemStack itemStack, TileEntity tileEntity) {
        return false;
    }

    @Override
    public boolean canReceiveManaFromItem(ItemStack itemStack, ItemStack itemStack1) {
        return false;
    }

    @Override
    public boolean canExportManaToPool(ItemStack itemStack, TileEntity tileEntity) {
        return true;
    }

    @Override
    public boolean canExportManaToItem(ItemStack itemStack, ItemStack itemStack1) {
        return true;
    }

    @Override
    public boolean isNoExport(ItemStack itemStack) {
        return false;
    }

    @Override
    public float getManaFractionForDisplay(ItemStack itemStack) {
        return currentMana/getMaxMana(itemStack);
    }

    @Override
    public String getEncryptionKey(ItemStack itemStack) {
        return EncryptionKey;
    }

    @Override
    public void setEncryptionKey(ItemStack itemStack, String s, String s1) {
        this.EncryptionKey = s;
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        return itemStack.getItem() == ItemEnum.ITEMMANAWIRELESSMIRROR.getItem();
    }

    @Override
    public boolean usePower(EntityPlayer entityPlayer, double v, ItemStack itemStack) {
        return this.extractAEPower( itemStack, v, Actionable.MODULATE ) >= v - 0.5;
    }

    @Override
    public boolean hasPower(EntityPlayer entityPlayer, double v, ItemStack itemStack) {
        return getAECurrentPower(itemStack) >= v;
    }

    @Override
    public IConfigManager getConfigManager(ItemStack itemStack) {
        return null;
    }
}
