package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.util.glu.Sphere;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Azazell
 * Common renderer for both white and black hole singularities
 */
public class TileSingularityRenderer<T extends TileEntity> extends AITileRenderer<T> {
	protected static final Sphere sphere = new Sphere();

	protected static void renderLinkage(ISingularity to, ISingularity from) {
		glBegin(GL_LINES);
		BlockPos relativePos = to.getHostPos().subtract(from.getHostPos());

		glColor3d(0,40 / 255f, 120 / 255f);
		glVertex3d(0,0,0);
		glVertex3d(relativePos.getX(), relativePos.getY(), relativePos.getZ());
		glEnd();
	}
}
