package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileFullRenderer;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ServerSecurityRenderer extends AITileFullRenderer<TileServerSecurity> {

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

        // Bind "on" texture
        bindTexture(top);
    }

    @Override
    public void render(TileServerSecurity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // Get tile rotation data
        AEPartLocation up = AEPartLocation.fromFacing(te.getUp()); // (1)
        AEPartLocation forward = AEPartLocation.fromFacing(te.getForward()); // (2)

        // Save matrix to stack
        prepareMatrix(x, y, z);

        // Configure light blend
        setLightAmbient();

        // Rescale render
        GlStateManager.scale(1,1,1);

        // Bind side texture 4 next 2 quads
        Minecraft.getMinecraft().renderEngine.bindTexture(side);

        // Quad #1 (x - static) EAST
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, 0.5F},
                {0.5F, 0.5F, 0.5F},
                {0.5F, 0.5F, -0.5F},
                {0.5F, -0.5F, -0.5F},
        }, defaultUV);

        // Quad #2 (-x - static) WEST
        drawQuadWithUV(new float[][] {
                {-0.5F, -0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, -0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, defaultUV);


        // Re-pick texture
        bindTopTexture(te);
        // Quad #3 (y - static) UP
        drawQuadWithUV(new float[][] {
                {0.5F, 0.5F, -0.5F},
                {0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, -0.5F},
        }, defaultUV);

        Minecraft.getMinecraft().renderEngine.bindTexture(bottom);
        // Quad #4 (-y - static) DOWN
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, -0.5F},
                {0.5F, -0.5F, 0.5F},
                {-0.5F, -0.5F, 0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, defaultUV);

        // Bind side texture 4 next 2 quads
        Minecraft.getMinecraft().renderEngine.bindTexture(side);
        // Quad #5 (z - static) SOUTH
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, 0.5F},
                {0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, -0.5F, 0.5F},
        }, defaultUV);

        // Quad #6 (-z - static) NORTH
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, -0.5F},
                {0.5F, 0.5F, -0.5F},
                {-0.5F, 0.5F, -0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, defaultUV);

        pushMatrix(x, y, z);
    }
}
