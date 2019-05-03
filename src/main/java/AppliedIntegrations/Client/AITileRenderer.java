package AppliedIntegrations.Client;

import AppliedIntegrations.tile.AITile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class AITileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    protected void prepareMatrix(double x, double y, double z) {
        // Save matrix to stack
        GlStateManager.pushMatrix();
        // Set color to black
        GlStateManager.color(1,1,1, 1);
        // Move gl pointer to x,y,z
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        // Disable cull
        GlStateManager.disableCull();
        // Disable 2D texturing
        GlStateManager.disableTexture2D();
        // Disable light
        GlStateManager.disableLighting();
    }

    protected void pushMatrix(double x, double y, double z) {
        // Re-enable all states of Opengl:
        // Cull
        GlStateManager.enableCull();
        // Enable lighting
        GlStateManager.enableLighting();
        // texture2d
        GlStateManager.enableTexture2D();
        // Repick color
        GlStateManager.color(0,0,0);
        // End drawing
        GlStateManager.popMatrix();
    }
}
