package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.util.glu.Sphere;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Azazell
 */
public class TileWhiteHoleRenderer extends AITileRenderer<TileWhiteHole> {

	private Sphere sphere = new Sphere();

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
			glBegin(GL_LINES);
			BlockPos relativePos = te.entangledHole.getHostPos().subtract(te.getHostPos());

			glColor3d(0,40 / 255f, 120 / 255f);
			glVertex3d(0,0,0);
			glVertex3d(relativePos.getX(), relativePos.getY(), relativePos.getZ());
			glEnd();
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
