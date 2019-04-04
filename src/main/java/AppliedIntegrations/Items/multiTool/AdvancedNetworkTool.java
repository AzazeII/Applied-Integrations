package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.AIItemRegistrable;
import appeng.api.implementations.items.IAEWrench;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 */
public class AdvancedNetworkTool extends AIItemRegistrable implements IAEWrench {

    public AdvancedToolModes currentMode = AdvancedToolModes.NETWORK_TOOL_MONITOR;

    public AdvancedNetworkTool(String reg) {
        super(reg);

        this.setCreativeTab(AppliedIntegrations.AI);

        this.setMaxStackSize(1);
    }


    @Override
    public boolean canWrench(ItemStack itemStack, EntityPlayer entityPlayer, BlockPos blockPos) {
        return ((AdvancedNetworkTool)itemStack.getItem()).currentMode == AdvancedToolModes.WRENCH;
    }

    public void triggerState(OverlayEntropyManipulator.ScrollDirection dir) {
        // Get next state depending on direction
        currentMode = currentMode.getNext(dir == OverlayEntropyManipulator.ScrollDirection.DOWN);
    }
}
