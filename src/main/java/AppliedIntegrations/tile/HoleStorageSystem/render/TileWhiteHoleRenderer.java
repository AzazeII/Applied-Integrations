package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @Author Azazell
 */
public class TileWhiteHoleRenderer extends TileSingularityRenderer<TileWhiteHole> {
	@Override
	public void render(TileWhiteHole te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		double radius = te.getHoleRadius();

		// Make hole alter it's size
		radius += Math.cos(te.getWorld().getWorldTime()) * 0.01;

		prepareMatrix(x, y, z);
		GlStateManager.scale(radius, radius, radius);
		GlStateManager.disableFog();

		sphere.draw((float) 0.53, 16, 16);

		if (te.entangledHole != null) {
			renderLinkage(te.entangledHole, te);
		}

		GlStateManager.disableFog();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}
}
