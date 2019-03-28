package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.*;

public class TileMETurretRenderer extends TileEntitySpecialRenderer<TileMETurretFoundation> {

    private ResourceLocation towerLeg = new ResourceLocation(AppliedIntegrations.modid, ":textures/blocks/server_frame_alt_a.png.png");

    // About -inf +inf, etc. +inf means point increased it's value on axis Ox, or Oy, or Oz, -inf is opposite
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
        // Disable auto enlightment
        GlStateManager.disableLighting();

        // Disable textures
        GlStateManager.disableTexture2D();

        // Start drawing lines
        glBegin(GL_LINES);
        // Add vertex A
        glVertex3d(0,0,0);

        // Get substracted vector
        Vec3d substracted = new Vec3d(te.renderingDirection.add(0.5,0.5,0.5).
                subtract(te.getPos().add(0.5,0.5,0.5))).normalize();

        // Add vertex B
        glVertex3d(substracted.x, substracted.y, substracted.z);

        // End drawing
        glEnd();

        //************Render Turret Tower************//

        // Enable textures
        GlStateManager.enableTexture2D();

        // Don't rescale drawing
        GlStateManager.disableRescaleNormal();

        // Start drawing cube

        // Height of tower's "leg"
        double height = 0.1;

        // Create new blend
        GlStateManager.blendFunc(770, 771);

        // Enable blending color
        GlStateManager.enableBlend();

        // Reset color
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );

        // Bind rendering texture
        bindTexture(towerLeg);

        // First edge
        glBegin(GL_QUADS); // from -X to +X on +Z
        // Add vertices
        glVertex3d(0.1,-1, 0.1); // X
        glVertex3d(-0.1,-1, 0.1); // X to -inf
        glVertex3d(-0.1,height, 0.1); // Y to +inf
        glVertex3d(0.1,height, 0.1); // X to +inf
        // End drawing
        glEnd();

        // second edge
        glBegin(GL_QUADS); // From -X to +X on -Z
        // Add vertices
        glVertex3d(0.1,-1, -0.1); // X
        glVertex3d(-0.1,-1, -0.1); // X to -inf
        glVertex3d(-0.1,height, -0.1); // Y to +inf
        glVertex3d(0.1,height, -0.1); // X to +inf
        // End drawing
        glEnd();

        // third edge
        glBegin(GL_QUADS); // From -Z to Z on +X
        // Add vertices
        glVertex3d(0.1,-1, 0.1); // Z
        glVertex3d(0.1,-1, -0.1); // Z to -inf
        glVertex3d(0.1,height, -0.1); // Y to -inf
        glVertex3d(0.1,height, 0.1); // Z to +inf
        // End drawing
        glEnd();

        // fourth edge
        glBegin(GL_QUADS);// From -Z to Z on -X
        // Add vertices
        glVertex3d(-0.1,-1, 0.1); // Z
        glVertex3d(-0.1,-1, -0.1); // Z to -inf
        glVertex3d(-0.1,height, -0.1); // Y to +inf
        glVertex3d(-0.1,height, 0.1); // Z to +inf
        // End drawing
        glEnd();

        //************Render Turret Tower************//

        // Re-enable all states of Opengl:
        // Cull
        GlStateManager.enableCull();

        // Enable lighting
        GlStateManager.enableLighting();

        // End drawing
        GlStateManager.popMatrix();
    }
}
