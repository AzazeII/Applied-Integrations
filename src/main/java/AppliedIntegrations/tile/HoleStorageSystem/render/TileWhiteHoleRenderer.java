package AppliedIntegrations.tile.HoleStorageSystem.render;

import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;

/**
 * @Author Azazell
 */
public class TileWhiteHoleRenderer extends AITileRenderer<TileWhiteHole> {

    private Sphere sphere = new Sphere();

    private Color getColor(){
        // Color of singularity, moved here because of IntelIJIdea feature with color picker
        return new Color(1,1,1);
    }

    @Override
    public void render(TileWhiteHole te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Now the fun begins :upside_down:
        // Get radius from tile
        double radius = te.getHoleRadius();
        // Add cosine of world time (always <= 1) to radius
        radius += Math.cos(te.getWorld().getWorldTime()) * 0.01;

        // Pass preparing to super() function
        prepareMatrix(x, y, z);
        // Change drawing scale
        GlStateManager.scale(radius, radius, radius);

        // Draw sphere
        sphere.draw((float) 0.53, 16, 16);

        // Re-enable all states of Opengl:
        // Cull
        GlStateManager.enableCull();
        // Enable lighting
        GlStateManager.enableLighting();
        // texture2d
        GlStateManager.enableTexture2D();
        // Repick color
        GlStateManager.color(1,1,1);
        // End drawing
        GlStateManager.popMatrix();
    }
}
