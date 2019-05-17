package AppliedIntegrations.Client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 * Same as AITileRenderer, but don't disable lighting
 */
public class AITileFullRenderer<T extends TileEntity> extends AITileRenderer<T> {
	@Override
	protected void prepareMatrix(double x, double y, double z) {
		// Disable standard light
		RenderHelper.disableStandardItemLighting();

		// Create blend
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Enable blend
		GlStateManager.enableBlend();

		// Disable cull
		GlStateManager.disableCull();

		// Check if smooth lighting is enabled
		if (Minecraft.isAmbientOcclusionEnabled()) {
			// Smooth shade
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			// Flat shade
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		// Isolate changes
		GlStateManager.pushMatrix();

		// Translate
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
	}

	@Override
	protected void pushMatrix(double x, double y, double z) {
		// Isolate changes
		GlStateManager.popMatrix();

		// Enable standard light
		RenderHelper.enableStandardItemLighting();
	}
}
