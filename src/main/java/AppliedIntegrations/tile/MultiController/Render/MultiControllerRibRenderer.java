package AppliedIntegrations.tile.MultiController.Render;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileFullRenderer;
import AppliedIntegrations.tile.MultiController.TileMultiControllerRib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;

import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.Axis.Y;
import static net.minecraft.util.EnumFacing.Axis.Z;

/**
 * @Author Azazell
 */
public class MultiControllerRibRenderer extends AITileFullRenderer<TileMultiControllerRib> {
	private static final ResourceLocation side = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame.png"); // (1)
	private static final ResourceLocation directionalSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_alt_b.png"); // (2)
	private static final ResourceLocation offSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_off.png"); // (3)
	private static final ResourceLocation offDirectionalSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_off_a.png"); // (4)
	private static final EnumFacing[] axisDirections = {SOUTH, WEST, UP};

	// Tile ---> Axis Map
	private static LinkedHashMap<TileMultiControllerRib, Axis> tileAxisMap = new LinkedHashMap<>();

	@Override
	public void render(TileMultiControllerRib te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		prepareMatrix(x, y, z);
		GlStateManager.disableLighting();
		setLightAmbient(te);
		GlStateManager.scale(1, 1, 1);
		bindTileTexture(te);

		// Quad #1 (x - static) EAST
		drawQuadWithUV(new float[][]{
				{0.5F, -0.5F, 0.5F},
				{0.5F, 0.5F, 0.5F},
				{0.5F, 0.5F, -0.5F},
				{0.5F, -0.5F, -0.5F}
		}, translateAxisToUV(te, EAST));

		// Quad #2 (-x - static) WEST
		drawQuadWithUV(new float[][]{
				{-0.5F, -0.5F, 0.5F},
				{-0.5F, 0.5F, 0.5F},
				{-0.5F, 0.5F, -0.5F},
				{-0.5F, -0.5F, -0.5F}
		}, translateAxisToUV(te, WEST));

		// Quad #3 (y - static) UP
		drawQuadWithUV(new float[][]{
				{0.5F, 0.5F, -0.5F},
				{0.5F, 0.5F, 0.5F},
				{-0.5F, 0.5F, 0.5F},
				{-0.5F, 0.5F, -0.5F}
		}, translateAxisToUV(te, UP));

		// Quad #4 (-y - static) DOWN
		drawQuadWithUV(new float[][]{
				{0.5F, -0.5F, -0.5F},
				{0.5F, -0.5F, 0.5F},
				{-0.5F, -0.5F, 0.5F},
				{-0.5F, -0.5F, -0.5F}
		}, translateAxisToUV(te, DOWN));

		// Quad #5 (z - static) SOUTH
		drawQuadWithUV(new float[][]{
				{0.5F, -0.5F, 0.5F},
				{0.5F, 0.5F, 0.5F},
				{-0.5F, 0.5F, 0.5F},
				{-0.5F, -0.5F, 0.5F}
		}, translateAxisToUV(te, SOUTH));

		// Quad #6 (-z - static) NORTH
		drawQuadWithUV(new float[][]{
				{0.5F, -0.5F, -0.5F},
				{0.5F, 0.5F, -0.5F},
				{-0.5F, 0.5F, -0.5F},
				{-0.5F, -0.5F, -0.5F}
		}, translateAxisToUV(te, NORTH));

		GlStateManager.enableLighting();
		pushMatrix(x, y, z);
	}

	private void bindTileTexture(TileMultiControllerRib te) {
		for (EnumFacing side : axisDirections) {
			if (te.getWorld().getTileEntity(te.getPos().offset(side)) instanceof TileMultiControllerRib) {
				if (te.getWorld().getTileEntity(te.getPos().offset(side.getOpposite())) instanceof TileMultiControllerRib) {
					Minecraft.getMinecraft().renderEngine.bindTexture(bindDirectionalTexture(te));
					tileAxisMap.put(te, side.getAxis());
					break;
				} else {
					Minecraft.getMinecraft().renderEngine.bindTexture(bindNondirectionalTexture(te));
				}
			} else {
				Minecraft.getMinecraft().renderEngine.bindTexture(bindNondirectionalTexture(te));
			}
		}
	}

	private float[][] translateAxisToUV(TileMultiControllerRib te, EnumFacing side) {
		Axis axis = tileAxisMap.get(te);
		if (axis == null) {
			return defaultUV;
		}

		if (axis == Y) {
			return caesarShift(defaultUV);
		} else if (axis == Z) {
			if (side.getAxis() == Y) {
				return caesarShift(defaultUV);
			}
		}

		return defaultUV;
	}

	private ResourceLocation bindDirectionalTexture(TileMultiControllerRib te) {
		if (!te.isActive) {
			return offDirectionalSide;
		}
		return directionalSide;
	}

	private ResourceLocation bindNondirectionalTexture(TileMultiControllerRib te) {
		if (!te.isActive) {
			return offSide;
		}
		return side;
	}
}