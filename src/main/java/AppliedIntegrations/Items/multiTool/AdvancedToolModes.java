package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import appeng.core.AppEng;
import net.minecraft.util.ResourceLocation;

/**
 * @Author Azazell
 */
public enum AdvancedToolModes {
    ENTROPY_MANIPULATOR(0, new ResourceLocation(AppEng.MOD_ID, "textures/items/entropy_manipulator.png")),
    NETWORK_TOOL_MONITOR(1, new ResourceLocation(AppEng.MOD_ID, "textures/items/network_tool.png")),
    COLOR_APPLICATOR(2, new ResourceLocation(AppEng.MOD_ID, "textures/items/color_applicator.png")),
    MEMORY_CARD(3, new ResourceLocation(AppEng.MOD_ID, "textures/items/memory_card.png")),
    WRENCH(4, new ResourceLocation(AppliedIntegrations.modid, "textures/items/advanced_network_tool/network_tool_off.png"));

    public int index;
    public ResourceLocation texture;

    AdvancedToolModes(int i, ResourceLocation location){
        index = i;
        texture = location;
    }

    public AdvancedToolModes getNext(boolean reverse){
        if(reverse) {
            return values()[this.index-1];
        }else {
            return values()[this.index+1];
        }
    }
}
