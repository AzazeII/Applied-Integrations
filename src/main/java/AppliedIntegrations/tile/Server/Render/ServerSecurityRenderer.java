package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class ServerSecurityRenderer extends AITileRenderer<TileServerSecurity> {

    // Init textures
    private final ResourceLocation top = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_top.png"); // (1)
    private final ResourceLocation side = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_side.png"); // (2)
    private final ResourceLocation bottom = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/me_server_security_bottom.png"); // (3)

    @Override
    public void render(TileServerSecurity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        // Get tile rotation data
        AEPartLocation top = AEPartLocation.fromFacing(te.getUp()); // (1)
        AEPartLocation forward = AEPartLocation.fromFacing(te.getForward()); // (2)

        // Pass preparing to super() function
        GlStateManager.pushMatrix();
        // Move gl pointer to x,y,z
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        // Re-enable textures after method above
        GlStateManager.enableTexture2D();

        // Rescale render
        GlStateManager.scale(1,1,1);

        // Bind side texture 4 next 4 quads
        bindTexture(side);

        // # Sides # //
        // Start drawing quads
        glBegin(GL_QUADS);
        // Quads #1
        glVertex3d(0.5, 0.5, 0.5);
        glVertex3d(0.5, 0.5, -0.5);
        glVertex3d(0.5, -0.5, -0.5);
        glVertex3d(0.5, -0.5, 0.5);
        // End drawing
        glEnd();

        // Start drawing quads
        glBegin(GL_QUADS);
        // Quads #2
        glVertex3d(-0.5, 0.5, 0.5);
        glVertex3d(-0.5, 0.5, -0.5);
        glVertex3d(-0.5, -0.5, -0.5);
        glVertex3d(-0.5, -0.5, 0.5);
        // End drawing
        glEnd();

        // Start drawing quads
        glBegin(GL_QUADS);
        // Quads #3
        glVertex3d(0.5,0.5,0.5);
        glVertex3d(-0.5, 0.5, 0.5);
        glVertex3d(-0.5, -0.5, 0.5);
        glVertex3d(0.5, -0.5, 0.5);
        // End drawing
        glEnd();

        // Start drawing quads
        glBegin(GL_QUADS);
        // Quads #4
        glVertex3d(0.5,0.5,-0.5);
        glVertex3d(-0.5, 0.5, -0.5);
        glVertex3d(-0.5, -0.5, -0.5);
        glVertex3d(0.5, -0.5, -0.5);
        // End drawing
        glEnd();
        // # Sides # //

        // Push matrix with function from super-class
        pushMatrix(x, y, z);
    }
}
