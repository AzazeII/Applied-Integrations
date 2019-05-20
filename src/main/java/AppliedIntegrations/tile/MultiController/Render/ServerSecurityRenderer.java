package AppliedIntegrations.tile.MultiController.Render;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileFullRenderer;
import AppliedIntegrations.tile.MultiController.TileServerSecurity;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static appeng.api.util.AEPartLocation.*;
import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;

public class ServerSecurityRenderer extends AITileFullRenderer<TileServerSecurity> {

	// Init textures
	private static final ResourceLocation top = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_top.png"); // (1)

	private static final ResourceLocation topOff = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_top_off.png"); // (2)

	private static final ResourceLocation side = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_side.png"); // (3)

	private static final ResourceLocation bottom = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_bottom.png"); // (4)

	private static final float[][][] texturePositionMap = new float[][][]{{ // (-y - static) DOWN
			{0.5F, -0.5F, -0.5F}, {0.5F, -0.5F, 0.5F}, {-0.5F, -0.5F, 0.5F}, {-0.5F, -0.5F, -0.5F},}, { // (y - static) UP
			{0.5F, 0.5F, -0.5F}, {0.5F, 0.5F, 0.5F}, {-0.5F, 0.5F, 0.5F}, {-0.5F, 0.5F, -0.5F},}, { // (-z - static) NORTH
			{0.5F, -0.5F, -0.5F}, {0.5F, 0.5F, -0.5F}, {-0.5F, 0.5F, -0.5F}, {-0.5F, -0.5F, -0.5F},}, { //  z - static) SOUTH
			{0.5F, -0.5F, 0.5F}, {0.5F, 0.5F, 0.5F}, {-0.5F, 0.5F, 0.5F}, {-0.5F, -0.5F, 0.5F},}, { // (-x - static) WEST
			{-0.5F, -0.5F, 0.5F}, {-0.5F, 0.5F, 0.5F}, {-0.5F, 0.5F, -0.5F}, {-0.5F, -0.5F, -0.5F},}, { // (x - static) EAST
			{0.5F, -0.5F, 0.5F}, {0.5F, 0.5F, 0.5F}, {0.5F, 0.5F, -0.5F}, {0.5F, -0.5F, -0.5F},}};

	@Override
	public void render(TileServerSecurity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// Get tile rotation data
		AEPartLocation forward = AEPartLocation.fromFacing(te.getForward()); // (1)

		// Save matrix to stack
		prepareMatrix(x, y, z);

		// Disable lighting
		GlStateManager.disableLighting();

		// Configure light blend
		setLightAmbient(te);

		// Rescale render
		GlStateManager.scale(1, 1, 1);

		// Quad #1 (x - static) EAST
		drawDirectionalQuadWithUV(EAST, AEPartLocation.fromFacing(forward.getFacing().rotateAround(Z).rotateAround(Z).rotateAround(Z)), te);

		// Quad #2 (-x - static) WEST
		drawDirectionalQuadWithUV(WEST, AEPartLocation.fromFacing(forward.getFacing().rotateAround(Z)), te);

		// Quad #3 (y - static) UP
		drawDirectionalQuadWithUV(UP, forward, te);

		// Quad #4 (-y - static) DOWN
		drawDirectionalQuadWithUV(DOWN, forward.getOpposite(), te);

		// Quad #5 (z - static) SOUTH
		drawDirectionalQuadWithUV(SOUTH, AEPartLocation.fromFacing(forward.getFacing().rotateAround(X).rotateAround(X).rotateAround(X)), te);

		// Quad #6 (-z - static) NORTH
		drawDirectionalQuadWithUV(NORTH, AEPartLocation.fromFacing(forward.getFacing().rotateAround(X)), te);


		// Enable lighting
		GlStateManager.enableLighting();

		// Push matrix to stack
		pushMatrix(x, y, z);
	}

	/**
	 * @param textureSide Side for texture getting
	 * @param actualSide  Side for texture drawing
	 */
	private void drawDirectionalQuadWithUV(AEPartLocation textureSide, AEPartLocation actualSide, TileServerSecurity te) {
		// Bind texture depending on side
		// Check if side is south, west, north or east
		if (textureSide != UP && textureSide != DOWN)
		// Bind side texture
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(side);
		}

		// Check if side is up
		if (textureSide == UP)
		// Bind top texture
		{
			bindTopTexture(te);
		}

		// Check if side is down
		if (textureSide == DOWN)
		// Bind bottom texture
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(bottom);
		}

		// Check if side isn't internal
		if (textureSide == INTERNAL || actualSide == INTERNAL)
		// Illegal state
		{
			throw new IllegalStateException("Side must be valid");
		}

		drawQuadWithUV(texturePositionMap[actualSide.ordinal()], defaultUV);
	}

	private void bindTopTexture(TileServerSecurity te) {
		// Check if server terminal has no master
		if (!te.hasMaster()) {
			// Bind off texture
			Minecraft.getMinecraft().renderEngine.bindTexture(topOff);
			return;
		}

		// Bind "on" texture
		Minecraft.getMinecraft().renderEngine.bindTexture(top);
	}
}
