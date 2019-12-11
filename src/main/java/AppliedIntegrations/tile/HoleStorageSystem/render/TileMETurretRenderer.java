package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Azazell
 */
public class TileMETurretRenderer extends AITileRenderer<TileMETurretFoundation> {
	@Override
	public void render(TileMETurretFoundation te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// Pass preparing to super function. Increase Y by one moving renderer to turret head
		prepareMatrix(x, y + 1, z);
		GlStateManager.scale(3, 3, 3);

		glBegin(GL_LINES);
		if (te.ammo != TileMETurretFoundation.Ammo.Singularity) {
			// Simple direction line
			final Vec3d direction = te.direction.add(new Vec3d(0,1,0));
			glVertex3d(0, 0, 0);
			glVertex3d(direction.x, direction.y, direction.z);
		} else {
			// Rendering white/black hole trajectory lines
			final Vec3d blackHolePos = te.blackHolePos;
			glColor3d(0,0,0);
			glVertex3d(0, 0, 0);
			glVertex3d(blackHolePos.x, blackHolePos.y, blackHolePos.z);

			final Vec3d whiteHolePos = te.whiteHolePos;
			glColor3d(1,1,1);
			glVertex3d(0, 0, 0);
			glVertex3d(whiteHolePos.x, whiteHolePos.y, whiteHolePos.z);

			// Render link between holes
			glColor3d(0,40 / 255f, 120 / 255f);
			glVertex3d(blackHolePos.x, blackHolePos.y, blackHolePos.z);
			glVertex3d(whiteHolePos.x, whiteHolePos.y, whiteHolePos.z);
		}
		glEnd();

		// Re-enable all states of after super function
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}
}
