package AppliedIntegrations.Items.AdvancedNetworkTool;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.implementations.items.IAEWrench;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.helpers.IMouseWheelItem;
import appeng.hooks.IBlockTool;
import appeng.items.tools.powered.ToolEntropyManipulator;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.util.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.MessageFormat;
import java.util.List;

import static net.minecraft.init.Blocks.AIR;

/**
 * @Author Azazell
 */
public class AdvancedNetworkTool extends AIItemRegistrable implements IMouseWheelItem, IAEWrench, IAEItemPowerStorage {

    private ToolEntropyManipulator entropyWrapper = new ToolEntropyManipulator();

    private AdvancedToolModes currentMode = AdvancedToolModes.WRENCH;

    public AdvancedNetworkTool(String reg) {
        super(reg);

        this.setCreativeTab(AppliedIntegrations.AI);

        this.setMaxStackSize(1);
    }

    @Override
    public void onWheel(ItemStack is, boolean up) {
        // Pass cycle
        currentMode = currentMode.cycleMode(up);

        // Notify player
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Switching mode to: " + currentMode.name()));
    }

    @Override
    public boolean canWrench(ItemStack wrench, EntityPlayer player, BlockPos pos) {
        return currentMode == AdvancedToolModes.WRENCH;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World w, final EntityPlayer p, final EnumHand hand ) {
        // Check if mode is entropy manipulator mode
        if(currentMode == AdvancedToolModes.ENTROPY_MANIPULATOR) {
            // Pass call to wrapper
            return entropyWrapper.onItemRightClick(w, p, hand);
        }

        // Fail
        return new ActionResult<>( EnumActionResult.FAIL, p.getHeldItem( hand ) );
    }

    @Override
    public double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        // Pass call to wrapper
        return entropyWrapper.injectAEPower(stack, amount, mode);
    }

    @Override
    public double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        // Pass call to wrapper
        return entropyWrapper.extractAEPower(stack, amount, mode);
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        // Pass call to wrapper
        return entropyWrapper.getAEMaxPower(stack);
    }

    @Override
    public double getAECurrentPower(ItemStack stack) {
        // Pass call to wrapper
        return entropyWrapper.getAECurrentPower(stack);
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack stack) {
        // Pass call to wrapper
        return entropyWrapper.getPowerFlow(stack);
    }
}
