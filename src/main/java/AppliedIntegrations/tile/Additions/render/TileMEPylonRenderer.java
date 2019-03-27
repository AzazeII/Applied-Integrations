package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.glu.Cylinder;

import static AppliedIntegrations.Blocks.Additions.BlockMEPylon.FACING;
import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;
import static org.lwjgl.opengl.GL11.*;

public class TileMEPylonRenderer extends TileEntitySpecialRenderer<TileMEPylon> {

    private Cylinder c = new Cylinder();

    @Override
    public void render(TileMEPylon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Check has singularity
        if (te.hasSingularity()) {
            // Get crystal rendering side
            EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(FACING).rotateY();
            // Save matrix to stack
            GlStateManager.pushMatrix();
            // Set color
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            // Disable cull
            GlStateManager.disableCull();

            // Get color based on current hole
            if(te.operatedTile instanceof TileBlackHole)
                // Set color to black
                GlStateManager.color(0, 0, 0, 1);
            else
                // Set color to white
                GlStateManager.color(1, 1, 1, 1);

            // Disable 2D texturing
            GlStateManager.disableTexture2D();
            // Disable auto enlightment
            GlStateManager.disableLighting();

            // Move gl pointer to x, y, z
            GlStateManager.translate(x + 0.5 + facing.getFrontOffsetX(), y + 0.5 + facing.getFrontOffsetY(), z + 0.5 + facing.getFrontOffsetZ());
            // Get tile
            TileEntity tile = (TileEntity) te.operatedTile;

            // Get vector
            BlockPos vec = tile.getPos().add(0.5, 0.5, 0.5).
                    subtract(te.getPos().add(0.5 + facing.getFrontOffsetX(), 0.5 + facing.getFrontOffsetY(), 0.5 + facing.getFrontOffsetZ()));

            // Rotate cylinder
            if (facing == NORTH) {
                glRotatef(180, 0, 1, 0);
            } else if (facing == WEST) {
                glRotatef(270, 0, 1, 0);
            } else if (facing == EAST) {
                glRotatef(90, 0, 1, 0);
            }

            // Draw cylinder
            if (facing.getAxis() == X)
                c.draw(0.3F, 0.3F, Math.abs(vec.getX()), 16, 16);
            if (facing.getAxis() == Z)
                c.draw(0.3F, 0.3F, Math.abs(vec.getZ()), 16, 16);

            // Rotate stack back
            if (facing == NORTH) {
                glRotatef(-180, 0, 1, 0);
            } else if (facing == WEST) {
                glRotatef(-270, 0, 1, 0);
            } else if (facing == EAST) {
                glRotatef(-90, 0, 1, 0);
            }

            // Rotate pylon rendering
            if (facing.getAxis() == X)
                glRotatef(90, 0, 0, 1);
            if (facing.getAxis() == Z)
                glRotatef(90, 1, 0, 0);

            // Rescale render
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
}
