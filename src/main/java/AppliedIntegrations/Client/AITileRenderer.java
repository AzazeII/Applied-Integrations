package AppliedIntegrations.Client;


import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * @Author Azazell
 */
public class AITileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	// Get tessellator instance
	protected static Tessellator tessellator = Tessellator.getInstance();

	// Get buffered builder
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

		// Iterate until i < length
		for (int i = 0; i < uvMap.length; i++) {
			// Avoid index out of bound exception
			// Check if i = length - 1
			if (i == uvMap.length - 1) {
				// Make first element equal to last
				copy[0] = uvMap[uvMap.length - 1];
			} else {
				// Make i+1 element equal to i element
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
		// Start drawing quads
		builder.begin(GL_QUADS, POSITION_TEX);

		// Assert length != 4
		if (posTex.length != 4 || uvTex.length != 4)
		// Quit program
		{
			throw new IllegalStateException("Position and UV vertices array length must be 4");
		}

		// Iterate for each pos array in posTex
		for (int i = 0; i < posTex.length; i++) {
			// Get current position
			float[] pos = posTex[i];

			// Get current tex
			float[] tex = uvTex[i];

			// Add position
			builder.pos(pos[0], pos[1], pos[2]).tex(tex[0], tex[1]).endVertex();
		}

		tessellator.draw();
	}

	protected void prepareMatrix(double x, double y, double z) {
		// Save matrix to stack
		GlStateManager.pushMatrix();
		// Set color to black
		GlStateManager.color(1, 1, 1, 1);
		// Move gl pointer to x,y,z
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		// Disable cull
		GlStateManager.disableCull();
		// Disable 2D texturing
		GlStateManager.disableTexture2D();
		// Disable light
		GlStateManager.disableLighting();
	}

	protected void pushMatrix(double x, double y, double z) {
		// Re-enable all states of Opengl:
		// Cull
		GlStateManager.enableCull();
		// Enable lighting
		GlStateManager.enableLighting();
		// texture2d
		GlStateManager.enableTexture2D();
		// Repick color
		GlStateManager.color(0, 0, 0);
		// End drawing
		GlStateManager.popMatrix();
	}

	protected void setLightAmbient() {
		// Get light UV
		float lightU = 240; // U
		float lightV = 240; // V

		// Set ambient
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, lightV);
	}
}
