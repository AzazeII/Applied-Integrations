package AppliedIntegrations.Client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * @Author Azazell
 */
public class AITileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	protected static Tessellator tessellator = Tessellator.getInstance();
	protected static BufferBuilder builder = tessellator.getBuffer();

	protected static float[][] defaultUV = {{1, 1}, {1, 0}, {0, 0}, {0, 1}};

	protected float[][] caesarShift(float[][] uvMap) {
		// Derange array. Move element at n position to n+1 position
		// {0,1},{2,3} -> {2,3},{0,1}
		// Specially for UV map it will rotate UV face, if starting UV face was:
		// A -> a B -> b C -> c D -> d
		// Then shifted will be:
		// A -> d B -> a C -> c D -> a
		// Create copy of UV map
		float[][] copy = uvMap.clone();

		for (int i = 0; i < uvMap.length; i++) {
			if (i == uvMap.length - 1) {
				copy[0] = uvMap[uvMap.length - 1];
			} else {
				copy[i + 1] = uvMap[i];
			}
		}

		return copy;
	}

	/**
	 * Create a quad with given positions and texels
	 *
	 * @param posTex array of all world positions. Inner array MUST have 3 float variables. x, y, z. Array must have 4 inner arrays
	 * @param uvTex  array of all texture positions. Inner array MUST have 2 float variables. U, V. Array must have 4 inner arrays
	 */
	protected void drawQuadWithUV(float[][] posTex, float[][] uvTex) {
		builder.begin(GL_QUADS, POSITION_TEX);

		if (posTex.length != 4 || uvTex.length != 4) {
			throw new IllegalStateException("Position and UV vertices array length must be 4");
		}

		for (int i = 0; i < posTex.length; i++) {
			float[] pos = posTex[i];
			float[] tex = uvTex[i];
			builder.pos(pos[0], pos[1], pos[2]).tex(tex[0], tex[1]).endVertex();
		}

		tessellator.draw();
	}

	protected void prepareMatrix(double x, double y, double z) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
	}

	protected void pushMatrix(double x, double y, double z) {
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.color(0, 0, 0);
		GlStateManager.popMatrix();
	}

	protected void setLightAmbient(TileEntity te) {
		int light = Minecraft.getMinecraft().world.getCombinedLight(te.getPos(), 0);
		int u = light % 65536;
		int v = light / 65536;

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}
}
