package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import static org.lwjgl.opengl.GL11.*;

public class TileMEPylonRenderer extends TileEntitySpecialRenderer<TileMEPylon> {

    @Override
    public void render(TileMEPylon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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
        // Rotate crystal
        GlStateManager.rotate(te.getWorld().getWorldTime(), 0, 1, 0);
        // Scale render
        GlStateManager.scale(0.5, 0.5, 0.5);
        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #1
        glVertex3d(1, 0, 0);
        glVertex3d(0, 1, 0);
        glVertex3d(0, 0, 1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #2
        glVertex3d(1, 0, 0);
        glVertex3d(0, 1, 0);
        glVertex3d(0, 0, -1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #3
        glVertex3d(-1, 0, 0);
        glVertex3d(0, 1, 0);
        glVertex3d(0, 0, 1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #4
        glVertex3d(-1, 0, 0);
        glVertex3d(0, 1, 0);
        glVertex3d(0, 0, -1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #5
        glVertex3d(1, 0, 0);
        glVertex3d(0, -1, 0);
        glVertex3d(0, 0, 1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #6
        glVertex3d(1, 0, 0);
        glVertex3d(0, -1, 0);
        glVertex3d(0, 0, -1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #7
        glVertex3d(-1, 0, 0);
        glVertex3d(0, -1, 0);
        glVertex3d(0, 0, 1);
        // End drawing
        glEnd();

        // Start drawing triangles
        glBegin(GL_TRIANGLES);
        // Triangle #8
        glVertex3d(-1, 0, 0);
        glVertex3d(0, -1, 0);
        glVertex3d(0, 0, -1);
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
