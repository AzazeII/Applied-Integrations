package AppliedIntegrations.tile.HoleStorageSystem.render;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.util.glu.Cylinder;

import static AppliedIntegrations.Blocks.Additions.BlockMEPylon.FACING;
import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;
import static net.minecraft.util.EnumFacing.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Azazell
 */
public class TileMEPylonRenderer extends TileEntitySpecialRenderer<TileMEPylon> {
	private Cylinder c = new Cylinder();
	private boolean isRadiusChanged = false;
	private float workingRadius = 0;
	private float lastRadius = 0;

	@Override
	public void render(TileMEPylon te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.hasSingularity()) {
			EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(FACING).rotateY();
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableCull();

			if (te.operatedTile instanceof TileBlackHole) {
				GlStateManager.color(0, 0, 0, 1);
			} else {
				GlStateManager.color(1, 1, 1, 1);
			}

			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();

			GlStateManager.translate(x + 0.5 + facing.getFrontOffsetX(), y + 0.5 + facing.getFrontOffsetY(), z + 0.5 + facing.getFrontOffsetZ());
			TileEntity tile = (TileEntity) te.operatedTile;

			BlockPos vec = tile.getPos().add(0.5, 0.5, 0.5).
					subtract(te.getPos().add(0.5 + facing.getFrontOffsetX(), 0.5 + facing.getFrontOffsetY(), 0.5 + facing.getFrontOffsetZ()));

			if (facing == NORTH) {
				glRotatef(180, 0, 1, 0);
			} else if (facing == WEST) {
				glRotatef(270, 0, 1, 0);
			} else if (facing == EAST) {
				glRotatef(90, 0, 1, 0);
			}

			float radius = (te.getBeamState() / (float) Math.min((AIConfig.pylonDrain * AIConfig.maxPylonDistance), Float.MAX_VALUE)) / 2;
			if (radius != lastRadius) {
				lastRadius = radius;
				isRadiusChanged = true;
			}

			// Update data, if radius > 0, and it not updated yet
			if (radius > 0 && isRadiusChanged) {
				workingRadius = radius;
				isRadiusChanged = false;
			}

			if (!te.drainsEnergy()) {
				if (workingRadius >= 0) {
					workingRadius -= 0.002;
				}
			} else {
				workingRadius = radius;
			}

			if (workingRadius >= 0) {
				// Draw cylinder
				if (facing.getAxis() == X) {
					c.draw(workingRadius, workingRadius, Math.abs(vec.getX()), 16, 16);
				}

				if (facing.getAxis() == Z) {
					c.draw(workingRadius, workingRadius, Math.abs(vec.getZ()), 16, 16);
				}

				if (facing == NORTH) {
					glRotatef(-180, 0, 1, 0);
				} else if (facing == WEST) {
					glRotatef(-270, 0, 1, 0);
				} else if (facing == EAST) {
					glRotatef(-90, 0, 1, 0);
				}
			}

			if (facing.getAxis() == X) {
				glRotatef(90, 0, 0, 1);
			}

			if (facing.getAxis() == Z) {
				glRotatef(90, 1, 0, 0);
			}

			// Rescale render
			GlStateManager.scale(0.5, 0.5, 0.5);

			// Drawing crystal on top of pylon
			glBegin(GL_TRIANGLES);
			glVertex3d(1, 0, 0);
			glVertex3d(0, 1, 0);
			glVertex3d(0, 0, 1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(1, 0, 0);
			glVertex3d(0, 1, 0);
			glVertex3d(0, 0, -1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(-1, 0, 0);
			glVertex3d(0, 1, 0);
			glVertex3d(0, 0, 1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(-1, 0, 0);
			glVertex3d(0, 1, 0);
			glVertex3d(0, 0, -1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(1, 0, 0);
			glVertex3d(0, -1, 0);
			glVertex3d(0, 0, 1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(1, 0, 0);
			glVertex3d(0, -1, 0);
			glVertex3d(0, 0, -1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(-1, 0, 0);
			glVertex3d(0, -1, 0);
			glVertex3d(0, 0, 1);
			glEnd();

			glBegin(GL_TRIANGLES);
			glVertex3d(-1, 0, 0);
			glVertex3d(0, -1, 0);
			glVertex3d(0, 0, -1);
			glEnd();

			GlStateManager.enableCull();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.color(1, 1, 1);
			GlStateManager.popMatrix();
		}
	}
}
