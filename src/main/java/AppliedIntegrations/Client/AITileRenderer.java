package AppliedIntegrations.Client;

import AppliedIntegrations.tile.AITile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class AITileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    protected float[][] caesarShift(float[][] uvMap) {
        // Derange array. Move element at n position to n+1 position
        // {0,1},{2,3} -> {2,3},{0,1}
        // Specially for UV map it will rotate UV face, if starting UV face was:
        // A -> a B -> b C -> c D -> d
        // Then shifted will be:
        // A -> d B -> a C -> c D -> a
        // Create copy of UV map
        float[][] copy = uvMap.clone();

        // Iterate until i < length
        for (int i = 0; i < uvMap.length; i++){
            // Avoid index out of bound exception
            // Check if i = length - 1
            if ( i == uvMap.length - 1) {
                // Make first element equal to last
                copy[0] = uvMap[uvMap.length - 1];
            } else {
                // Make i+1 element equal to i element
                copy[i + 1] = uvMap[i];
            }
        }

        return copy;
    }

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
