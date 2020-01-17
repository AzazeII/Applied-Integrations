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
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
	}

	@Override
	protected void pushMatrix(double x, double y, double z) {
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}
}
