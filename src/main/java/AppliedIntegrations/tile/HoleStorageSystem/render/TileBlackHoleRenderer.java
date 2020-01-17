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
		double radius = te.getBlackHoleRadius();

		// This makes hole alter it's size
		radius += Math.cos(te.getWorld().getWorldTime()) * 0.01;

		prepareMatrix(x, y, z);
		GlStateManager.color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 1);
		GlStateManager.scale(radius, radius, radius);
		GlStateManager.disableFog();

		sphere.draw((float) 0.53, 16, 16);
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
