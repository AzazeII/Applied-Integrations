package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static AppliedIntegrations.Items.multiTool.OverlayEntropyManipulator.ScrollDirection.DOWN;
import static AppliedIntegrations.Items.multiTool.OverlayEntropyManipulator.ScrollDirection.UP;
import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Azazell
 */
public class OverlayEntropyManipulator {

    private static Minecraft mc = Minecraft.getMinecraft();

    private EntityPlayer player;

    public OverlayEntropyManipulator(EntityPlayer p) {
        // Set player
        this.player = p;

        // Register event listener
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        // Check not null
        if(player == null)
            return;

        // Get held stack in main hand
        ItemStack stack = player.getHeldItemMainhand();

        // Get item from stack
        Item item = stack.getItem();

        // Check if stack instanceof advanced network tool
        if (item instanceof AdvancedNetworkTool) {
            // Get tool
            AdvancedNetworkTool tool = (AdvancedNetworkTool) item;

            // Set color to passive white
            GlStateManager.color(1, 1, 1, 1);
            // Disable lighting
            GlStateManager.disableLighting();
            // Enable 2d texturing
            GlStateManager.enableTexture2D();

            // Bind texture, depending on current mode
            mc.renderEngine.bindTexture(tool.currentMode.texture);

            // Get scaled screen resolution
            ScaledResolution resolution = new ScaledResolution(mc);

            // Get scaled width and height
            int w = resolution.getScaledWidth() / 2;
            int h = resolution.getScaledHeight() / 2;

            // Start drawing quads
            glBegin(GL_QUADS);

            // Add vertices
            glVertex3d(w - 24, h + 16, 0);
            glVertex3d(w - 8,h + 16, 0);
            glVertex3d(w - 24, h - 16, 0);
            glVertex3d(w - 8,h - 16, 0);

            // End drawing quads
            glEnd();
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        // Scroll direction
        ScrollDirection dir = null;

        // Check not null and player is sneaking
        if (player != null && player.isSneaking()) {
            // Get held stack in main hand
            ItemStack stack = player.getHeldItemMainhand();

            // Get item from stack
            Item item = stack.getItem();

            // Check if stack instanceof advanced network tool
            if (item instanceof AdvancedNetworkTool) {
                // Get tool
                AdvancedNetworkTool tool = (AdvancedNetworkTool) item;

                if(event.getDwheel() < 0)
                    tool.triggerState(UP);
                else if(event.getDwheel() > 0)
                    tool.triggerState(DOWN);


                // Cancel event
                event.setCanceled(true);
            }
        }
    }

    enum ScrollDirection{
        DOWN,
        UP
    }

}
