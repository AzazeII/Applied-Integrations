package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import appeng.client.render.FacingToRotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import static AppliedIntegrations.Blocks.Additions.BlockMEPylon.FACING;
import static net.minecraft.util.EnumFacing.*;
import static org.lwjgl.opengl.GL11.*;

public class TileMEPylonRenderer extends TileEntitySpecialRenderer<TileMEPylon> {

    @Override
    public void render(TileMEPylon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Get crystal rendering side
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(FACING).rotateY();
        // Save matrix to stack
        GlStateManager.pushMatrix();
        // Set color
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        // Disable cull
        GlStateManager.disableCull();
        // Move gl pointer to x, y, z
        GlStateManager.translate(x + 0.5 + facing.getFrontOffsetX(), y + 0.5 + facing.getFrontOffsetY(), z + 0.5 + facing.getFrontOffsetZ());
        // Set color to black
        GlStateManager.color(0, 0, 0, 1);
        // Disable 2D texturing
        GlStateManager.disableTexture2D();
        // Disable auto enlightment
        GlStateManager.disableLighting();
        // Rotate on X, or Z axis
        if(facing.getAxis() == Axis.X){
            GlStateManager.rotate(90, facing.getFrontOffsetX(), 0,0);
        }else if(facing.getAxis() == Axis.Z){
            GlStateManager.rotate(90, 0, 0,facing.getFrontOffsetZ());
        }
        // Rotate crystal on Y axis
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
