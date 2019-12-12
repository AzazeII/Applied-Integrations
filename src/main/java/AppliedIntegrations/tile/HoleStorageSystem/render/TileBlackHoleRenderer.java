package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

/**
 * @Author Azazell
 */
public class TileBlackHoleRenderer extends TileSingularityRenderer<TileBlackHole> {
	@Override
	public void render(TileBlackHole te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// Now the fun begins :upside_down:
		// Get radius from tile
		double radius = te.getBlackHoleRadius();
		// Add cosine of world time (always <= 1) to radius
		radius += Math.cos(te.getWorld().getWorldTime()) * 0.01;

		// Pass preparing to super() function
		prepareMatrix(x, y, z);
		GlStateManager.color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 1);
		GlStateManager.scale(radius, radius, radius);
		GlStateManager.disableFog();

		// Draw sphere
		sphere.draw((float) 0.53, 16, 16);

		// Check if we have entangled singularity, so we need to render our entanglement
		if (te.entangledHole != null) {
			renderLinkage(te.entangledHole, te);
		}

		GlStateManager.enableFog();
		pushMatrix(x, y, z);
	}

	private Color getColor() {
		return new Color(0, 0, 0);
	}
}
