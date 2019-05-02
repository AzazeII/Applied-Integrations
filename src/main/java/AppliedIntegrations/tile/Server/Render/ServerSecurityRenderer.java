package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class ServerSecurityRenderer extends AITileRenderer<TileServerSecurity> {

    // Init textures
    private static final ResourceLocation top = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_top.png"); // (1)
    private static final ResourceLocation topOff = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_top_off.png"); // (2)
    private static final ResourceLocation side = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_side.png"); // (3)
    private static final ResourceLocation bottom = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_bottom.png"); // (4)

    private void bindTopTexture(TileServerSecurity te) {
        // Check not null
        if (te.getGridNode() == null){
            // Bind off texture
            Minecraft.getMinecraft().renderEngine.bindTexture(topOff);
            return;
        }

        // Check if node is active
        if (!te.getGridNode().isActive()){
            // Bind off texture
            Minecraft.getMinecraft().renderEngine.bindTexture(topOff);
            return;
        }

        // Bind on texture
        bindTexture(top);
    }

    @Override
    public void render(TileServerSecurity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        // Get tile rotation data
        AEPartLocation up = AEPartLocation.fromFacing(te.getUp()); // (1)
        AEPartLocation forward = AEPartLocation.fromFacing(te.getForward()); // (2)

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
        // Bind side texture 4 next 2 quads
        Minecraft.getMinecraft().renderEngine.bindTexture(side);
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
        // End drawing
        tessellator.draw();

        // Start drawing quads
        builder.begin(GL_QUADS, POSITION_TEX);
        // Re-pick texture
        bindTopTexture(te);
        // Quad #3 (y - static)
        builder.pos(0.5, 0.5, -0.5).tex(1,1).endVertex();
        builder.pos(0.5, 0.5, 0.5).tex(1,0).endVertex();
        builder.pos(-0.5, 0.5, 0.5).tex(0,0).endVertex();
        builder.pos(-0.5, 0.5, -0.5).tex(0,1).endVertex();
        // End drawing
        tessellator.draw();

        // Start drawing quads
        builder.begin(GL_QUADS, POSITION_TEX);
        // Re-pick texture
        Minecraft.getMinecraft().renderEngine.bindTexture(bottom);
        // Quad #4 (y - static)
        builder.pos(0.5, -0.5, -0.5).tex(1,1).endVertex();
        builder.pos(0.5, -0.5, 0.5).tex(1,0).endVertex();
        builder.pos(-0.5, -0.5, 0.5).tex(0,0).endVertex();
        builder.pos(-0.5, -0.5, -0.5).tex(0,1).endVertex();
        // End drawing
        tessellator.draw();

        // Start drawing quads
        builder.begin(GL_QUADS, POSITION_TEX);
        // Bind side texture 4 next 2 quads
        Minecraft.getMinecraft().renderEngine.bindTexture(side);
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
