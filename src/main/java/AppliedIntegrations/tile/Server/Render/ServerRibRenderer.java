package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.Server.TileServerRib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static net.minecraft.util.EnumFacing.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class ServerRibRenderer extends AITileRenderer<TileServerRib> {

    // Initialize side variables
    private static final ResourceLocation side = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame.png"); // (1)
    private static final ResourceLocation directionalSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_alt_b.png"); // (2)

    private static final ResourceLocation offSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_off.png"); // (3)
    private static final ResourceLocation offDirectionalSide = new ResourceLocation(AppliedIntegrations.modid, "textures/blocks/server_frame_off_a.png"); // (4)

    // Tile ---> Axis Map
    private static LinkedHashMap<TileServerRib, Axis> tileAxisMap = new LinkedHashMap<>();

    private static final EnumFacing[] axisDirections = {
            SOUTH, WEST, UP
    };

    // Get tessellator instance
    private Tessellator tessellator = Tessellator.getInstance();

    // Get buffered builder
    private BufferBuilder builder = tessellator.getBuffer();


    private ResourceLocation bindDirectionalTexture(TileServerRib te) {
        // Check not null
        if (te.getGridNode() == null)
            return offDirectionalSide;
        // Check if node is not active
        else if (!te.getGridNode().isActive())
            return offDirectionalSide;
        // Return active directional side
        return directionalSide;
    }

    private ResourceLocation bindNondirectionaTexture(TileServerRib te) {
        // Check not null
        if (te.getGridNode() == null)
            return offSide;
            // Check if node is not active
        else if (!te.getGridNode().isActive())
            return offSide;
        // Return active non-directional side
        return side;
    }

    private void bindTileTexture(TileServerRib te) {
        // Iterate for each axis direction
        for (EnumFacing side : axisDirections){
            // Check if rib has same block at current side
            if (te.getWorld().getTileEntity(te.getPos().offset(side)) instanceof TileServerRib) {
                // Check if rib has same block at opposite side
                if (te.getWorld().getTileEntity(te.getPos().offset(side.getOpposite())) instanceof TileServerRib) {
                    // Make rib directional
                    Minecraft.getMinecraft().renderEngine.bindTexture(bindDirectionalTexture(te));

                    // Bind tile to current axis
                    tileAxisMap.put(te, side.getAxis());

                    // Break loop
                    break;
                } else {
                    // Make rib non-directional
                    Minecraft.getMinecraft().renderEngine.bindTexture(bindNondirectionaTexture(te));
                }
            } else {
                // Make rib non-directional
                Minecraft.getMinecraft().renderEngine.bindTexture(bindNondirectionaTexture(te));
            }
        }
    }


    /**
     * Create a quad with given positions and texels
     * @param posTex array of all world positions. Inner array MUST have 3 float variables. x, y, z. Array must have 4 inner arrays
     * @param uvTex array of all texture positions. Inner array MUST have 2 float variables. U, V. Array must have 4 inner arrays
     */
    private void drawQuadWithUV(float[][] posTex, float[][] uvTex) {
        // Start drawing quads
        builder.begin(GL_QUADS, POSITION_TEX);

        // Assert length != 4
        if (posTex.length != 4 || uvTex.length != 4)
            // Quit program
            throw new IllegalStateException("Position and UV vertices array length must be 4");

        // Iterate for each pos array in posTex
        for (int i = 0; i < posTex.length; i++){
            // Get current position
            float[] pos = posTex[i];

            // Get current tex
            float[] tex = uvTex[i];

            // Add position
            builder.pos(pos[0], pos[1], pos[2]).tex(tex[0], tex[1]).endVertex();
        }

        tessellator.draw();
    }


    private void setAmbient(TileServerRib te) {
        // Get combined light near block
        int light = te.getWorld().getCombinedLight (te.getPos(), 0);

        // Get UV light mapping
        int lightU = light % 65536; // (1) U, left part from division by 65536
        int lightV = light / 65536; // (2) V, actual division by 65536

        // Change light mapping to match current position of tile
        OpenGlHelper.setLightmapTextureCoords (OpenGlHelper.lightmapTexUnit, (float) lightU, (float) lightV);
    }

    @Override
    public void render(TileServerRib te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        // Save matrix to stack
        prepareMatrix(x, y, z);
        GlStateManager.enableTexture2D();

        // Change ambient color (lighting)
        setAmbient(te);

        // Rescale render
        GlStateManager.scale(1,1,1);

        // Bind side texture 4 next 6 quads
        bindTileTexture(te);

        // Quad #1 (x - static)
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, 0.5F},
                {0.5F, 0.5F, 0.5F},
                {0.5F, 0.5F, -0.5F},
                {0.5F, -0.5F, -0.5F},
        }, new float[][] {
                {1, 1},
                {1, 0},
                {0, 0},
                {0, 1}
        });
        // Quad #2 (x - static)
        drawQuadWithUV(new float[][] {
                {-0.5F, -0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, -0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, new float[][] {
                {1, 1},
                {1, 0},
                {0, 0},
                {0, 1}
        });
        // Quad #3 (y - static)
        drawQuadWithUV(new float[][] {
                {0.5F, 0.5F, -0.5F},
                {0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, -0.5F},
        }, new float[][] {
                {1, 1},
                {1, 0},
                {0, 0},
                {0, 1}
        });
        // Quad #4 (y - static)
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, -0.5F},
                {0.5F, -0.5F, 0.5F},
                {-0.5F, -0.5F, 0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, new float[][] {
                {1, 1},
                {1, 0},
                {0, 0},
                {0, 1}
        });
        // Quad #5 (z - static)
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, 0.5F},
                {0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, -0.5F, 0.5F},
        }, new float[][] {
                {1, 1},
                {1, 0},
                {0, 0},
                {0, 1}
        });
        // Quad #6 (z - static)
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, -0.5F},
                {0.5F, 0.5F, -0.5F},
                {-0.5F, 0.5F, -0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, new float[][] {
                {1, 1},
                {1, 0},
                {0, 0},
                {0, 1}
        });

        pushMatrix(x, y, z);
    }
}