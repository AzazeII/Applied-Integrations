package AppliedIntegrations.Items.AdvancedNetworkTool;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.implementations.items.IAEWrench;
import appeng.helpers.IMouseWheelItem;
import appeng.hooks.IBlockTool;
import appeng.items.tools.powered.ToolEntropyManipulator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class AdvancedNetworkTool extends AIItemRegistrable implements IMouseWheelItem, IAEWrench, IBlockTool {

    private ToolEntropyManipulator entropyWrapper = new ToolEntropyManipulator();

    private AdvancedToolModes currentMode = AdvancedToolModes.WRENCH;

    public AdvancedNetworkTool(String reg) {
        super(reg);

        this.setCreativeTab(AppliedIntegrations.AI);

        this.setMaxStackSize(1);
    }

    @Override
    public void onWheel(ItemStack is, boolean up) {
        currentMode = currentMode.cycleMode(up);

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Switching mode to: " + currentMode.name()));
    }

    @Override
    public boolean canWrench(ItemStack wrench, EntityPlayer player, BlockPos pos) {
        return currentMode == AdvancedToolModes.WRENCH;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack is, EntityPlayer p, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Check if mode is entropy manipulator mode
        if(currentMode == AdvancedToolModes.ENTROPY_MANIPULATOR) {
            // Pass call to wrapper
            return entropyWrapper.onItemUse(is, p, w, pos, hand, side, hitX, hitY, hitZ);
        }

        // Fail
        return EnumActionResult.FAIL;
    }
}
