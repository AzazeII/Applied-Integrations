package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import static org.lwjgl.opengl.GL11.*;

public class TileMETurretRenderer extends TileEntitySpecialRenderer<TileMETurretFoundation> {

    @Override
    public void render(TileMETurretFoundation te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Save matrix to stack
        GlStateManager.pushMatrix();
        // Set color to black
        GlStateManager.color(0, 0, 0, 1);
        // Move gl pointer to x, y + 1.5 (one block upper), z
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
        // Disable cull
        GlStateManager.disableCull();
        // Disable 2D texturing
        GlStateManager.disableTexture2D();
        // Disable auto enlightment
        GlStateManager.disableLighting();
        // Scale render
        GlStateManager.scale(3, 3, 3);

        // Start drawing triangles
        glBegin(GL_LINES);
        // Add vertex A
        glVertex3d(0,0,0);
        // Add vertex B
        glVertex3d(Math.min(te.renderingDirection.getX(), 1), Math.min(te.renderingDirection.getY(), 1), Math.min(te.renderingDirection.getZ(), 1));
        // End drawing
        glEnd();

        // Re-enable all states of Opengl:
        // Cull
        GlStateManager.enableCull();
        // Enable lighting
        GlStateManager.enableLighting();
        // texture2d
        GlStateManager.enableTexture2D();
        // Repick color
        GlStateManager.color(1, 1, 1);

        // End drawing
        GlStateManager.popMatrix();
    }
}
