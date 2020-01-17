package AppliedIntegrations.tile.HoleStorageSystem.render;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.tile.entities.EntitySingularity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.glu.Sphere;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class EntityBlackHoleRenderer extends Render<EntitySingularity> {
	public EntityBlackHoleRenderer(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySingularity te, double x, double y, double z, float entityYaw, float partialTicks) {
		// Successfully copied from TileSingularityRenderer, Z..
		GlStateManager.pushMatrix();
		GlStateManager.color(0, 0, 0, 1);
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.scale(3, 3, 3);

		new Sphere().draw((float) 0.53, 16, 16);

		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySingularity entity) {
		return new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/black.png");
	}
}
