package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import static org.lwjgl.opengl.GL11.*;

public class TileMEPylonRenderer extends TileEntitySpecialRenderer<TileMEPylon> {

    @Override
    public void render(TileMEPylon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Render only if there is singularity
        if(te.hasSingularity()) {
            // Save matrix to stack
            GlStateManager.pushMatrix();
            // Set color to black
            GlStateManager.color(0, 0, 0, 1);
            // Move gl pointer to x,y,z
            GlStateManager.translate(x + 0.5, y + 1, z + 0.5);

            // Push attributes
            GlStateManager.pushAttrib();
            // Disable cull
            GlStateManager.disableCull();
            // Disable ligthing
            GlStateManager.disableLighting();

            // Move gl pointer to x,y,z
            GlStateManager.translate(x + 0.5, y + 1, z + 0.5);

            // Rotate crystal
            GlStateManager.rotate(te.getWorld().getWorldTime(), 0, 1, 0);

            glColor4f(0, 0, 0, 1);
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

            // Re-enable setting and pop attribute
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.popAttrib();

            // End drawing stack
            GlStateManager.popMatrix();
        }
    }
}
