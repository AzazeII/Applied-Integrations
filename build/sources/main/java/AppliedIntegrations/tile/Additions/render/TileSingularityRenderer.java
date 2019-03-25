package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;

public class TileSingularityRenderer extends TileEntitySpecialRenderer<TileBlackHole> {

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

        // Save matrix to stack
        GlStateManager.pushMatrix();
        // Set color to black
        GlStateManager.color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 1);
        // Move gl pointer to x,y,z
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        // Disable cull
        GlStateManager.disableCull();
        // Disable 2D texturing
        GlStateManager.disableTexture2D();
        // Disable auto enlightment
        GlStateManager.disableLighting();
        // Change drawing scale
        GlStateManager.scale(radius, radius, radius);

        // Draw sphere
        new Sphere().draw((float) 0.53, 16, 16);

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


        // My first renderer done! :)
    }
}
