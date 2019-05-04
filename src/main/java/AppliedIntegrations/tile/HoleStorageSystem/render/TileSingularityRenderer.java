package AppliedIntegrations.tile.HoleStorageSystem.render;

import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;

/**
 * @Author Azazell
 */
public class TileSingularityRenderer extends AITileRenderer<TileBlackHole> {

    private Sphere sphere = new Sphere();

    private Color getColor(){
        // Color of singularity, moved here because of IntelIJIdea feature with color picker
        return new Color(0,0,0);
    }

    @Override
    public void render(TileBlackHole te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Now the fun begins :upside_down:
        // Get radius from tile
        double radius = te.getBlackHoleRadius();
        // Add cosine of world time (always <= 1) to radius
        radius += Math.cos(te.getWorld().getWorldTime()) * 0.01;

        // Pass preparing to super() function
        prepareMatrix(x, y, z);

        // Set color to black
        GlStateManager.color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 1);

        // Change drawing scale
        GlStateManager.scale(radius, radius, radius);

        // Draw sphere
        sphere.draw((float) 0.53, 16, 16);

        // Re-enable all states of Opengl:
        pushMatrix(x, y, z);
    }
}
