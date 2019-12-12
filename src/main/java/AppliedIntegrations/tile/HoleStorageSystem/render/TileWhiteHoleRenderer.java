package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @Author Azazell
 */
public class TileWhiteHoleRenderer extends TileSingularityRenderer<TileWhiteHole> {
	@Override
	public void render(TileWhiteHole te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// Now the fun begins :upside_down:
		// Get radius from tile
		double radius = te.getHoleRadius();

		// Add cosine of world time (always <= 1) to radius
		radius += Math.cos(te.getWorld().getWorldTime()) * 0.01;

		// Pass preparing to super() function
		prepareMatrix(x, y, z);
		GlStateManager.scale(radius, radius, radius);
		GlStateManager.disableFog();

		// Draw sphere
		sphere.draw((float) 0.53, 16, 16);

		// Check if we have entangled singularity, so we need to render our entanglement
		if (te.entangledHole != null) {
			renderLinkage(te.entangledHole, te);
		}

		// Re-enable all states of Opengl:
		GlStateManager.disableFog();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}
}
