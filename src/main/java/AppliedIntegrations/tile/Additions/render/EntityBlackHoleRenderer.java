package AppliedIntegrations.tile.Additions.render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.entities.EntityBlackHole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.glu.Sphere;

import javax.annotation.Nullable;

public class EntityBlackHoleRenderer extends Render<EntityBlackHole> {
    public EntityBlackHoleRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBlackHole entity) {
        return new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/black.png");
    }

    @Override
    public void doRender( EntityBlackHole te, double x, double y, double z, float entityYaw, float partialTicks)
    {
        // Successfully copied from TileSingularityRenderer, E..Z..

        // Now the fun begins :upside_down:
        // Save matrix to stack
        GlStateManager.pushMatrix();
        // Set color to black
        GlStateManager.color(0,0,0, 1);
        // Move gl pointer to x,y,z
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        // Disable cull
        GlStateManager.disableCull();
        // Disable 2D texturing
        GlStateManager.disableTexture2D();
        // Disable auto enlightment
        GlStateManager.disableLighting();
        // Change drawing scale
        GlStateManager.scale(3, 3, 3);

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
    }

}
