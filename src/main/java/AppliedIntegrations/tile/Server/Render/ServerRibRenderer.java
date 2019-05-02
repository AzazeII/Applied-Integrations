package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.Server.TileServerRib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class ServerRibRenderer extends AITileRenderer<TileServerRib> {

    // Initialize side variables
    private static final ResourceLocation side = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame.png"); // (1)
    private static final ResourceLocation directionalSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_alt_b.png"); // (2)
    private static final ResourceLocation offSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_off.png"); // (2)
    private static final ResourceLocation offDirectionalSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_off_a.png"); // (2)

    private void bindTileTexture(TileServerRib te) {
        // Check if tile has master
        if (te.hasMaster()){
            // -- Directional Branch -- //

            // -- Directional Branch -- //
        } else {
            // -- Non-directional Branch -- //

            // -- Non-directional Branch -- //
        }
    }

    @Override
    public void render(TileServerRib te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        // Save matrix to stack
        prepareMatrix(x, y, z);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GlStateManager.enableTexture2D();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);

        // Rescale render
        GlStateManager.scale(1,1,1);

        // Get tessellator instance
        Tessellator tessellator = Tessellator.getInstance();
        // Get buffered builder
        BufferBuilder builder = tessellator.getBuffer();

        // Start drawing quads
        builder.begin(GL_QUADS, POSITION_TEX);
        // Bind side texture 4 next 6 quads
        bindTileTexture(te);

        // Quad #1 (x - static)
        builder.pos(0.5, -0.5, 0.5).tex(1,1).endVertex();
        builder.pos(0.5, 0.5, 0.5).tex(1,0).endVertex();
        builder.pos(0.5, 0.5, -0.5).tex(0,0).endVertex();
        builder.pos(0.5, -0.5, -0.5).tex(0,1).endVertex();
        // Quad #2 (x - static)
        builder.pos(-0.5, -0.5, 0.5).tex(1,1).endVertex();
        builder.pos(-0.5, 0.5, 0.5).tex(1,0).endVertex();
        builder.pos(-0.5, 0.5, -0.5).tex(0,0).endVertex();
        builder.pos(-0.5, -0.5, -0.5).tex(0,1).endVertex();
        // Quad #3 (y - static)
        builder.pos(0.5, 0.5, -0.5).tex(1,1).endVertex();
        builder.pos(0.5, 0.5, 0.5).tex(1,0).endVertex();
        builder.pos(-0.5, 0.5, 0.5).tex(0,0).endVertex();
        builder.pos(-0.5, 0.5, -0.5).tex(0,1).endVertex();
        // Quad #4 (y - static)
        builder.pos(0.5, -0.5, -0.5).tex(1,1).endVertex();
        builder.pos(0.5, -0.5, 0.5).tex(1,0).endVertex();
        builder.pos(-0.5, -0.5, 0.5).tex(0,0).endVertex();
        builder.pos(-0.5, -0.5, -0.5).tex(0,1).endVertex();
        // Quad #5 (z - static)
        builder.pos(0.5, -0.5, 0.5).tex(1,1).endVertex();
        builder.pos(0.5, 0.5, 0.5).tex(1,0).endVertex();
        builder.pos(-0.5, 0.5, 0.5).tex(0,0).endVertex();
        builder.pos(-0.5, -0.5, 0.5).tex(0,1).endVertex();
        // Quad #6 (z - static)
        builder.pos(0.5, -0.5, -0.5).tex(1,1).endVertex();
        builder.pos(0.5, 0.5, -0.5).tex(1,0).endVertex();
        builder.pos(-0.5, 0.5, -0.5).tex(0,0).endVertex();
        builder.pos(-0.5, -0.5, -0.5).tex(0,1).endVertex();
        // End drawing
        tessellator.draw();

        GL11.glPopAttrib();

        pushMatrix(x, y, z);
    }
}
