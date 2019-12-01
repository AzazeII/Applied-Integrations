package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Azazell
 */
public class TileMETurretRenderer extends AITileRenderer<TileMETurretFoundation> {
	@Override
	public void render(TileMETurretFoundation te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// Pass preparing to super function
		prepareMatrix(x, y, z);
		GlStateManager.scale(3, 3, 3);

		glBegin(GL_LINES);
		if (te.ammo != TileMETurretFoundation.Ammo.Singularity) {
			// Simple direction line
			final BlockPos direction = te.direction;
			glVertex3d(0, 0, 0);
			glVertex3d(direction.getX(), direction.getY(), direction.getZ());
		} else {
			// Rendering white/black hole trajectory lines
			final BlockPos blackHolePos = te.blackHolePos;
			glColor3d(0,0,0);
			glVertex3d(0, 0, 0);
			glVertex3d(blackHolePos.getX(), blackHolePos.getY(), blackHolePos.getZ());

			final BlockPos whiteHolePos = te.whiteHolePos;
			glColor3d(1,1,1);
			glVertex3d(0, 0, 0);
			glVertex3d(whiteHolePos.getX(), whiteHolePos.getY(), whiteHolePos.getZ());
		}
		glEnd();

		// Re-enable all states of after super function
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();
	}
}
