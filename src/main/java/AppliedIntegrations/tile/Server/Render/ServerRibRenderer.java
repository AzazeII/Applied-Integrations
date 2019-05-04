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

import java.util.LinkedHashMap;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.Axis.Y;
import static net.minecraft.util.EnumFacing.Axis.Z;
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
    private static Tessellator tessellator = Tessellator.getInstance();

    // Get buffered builder
    private static BufferBuilder builder = tessellator.getBuffer();

    private static float[][] defaultUV = {
            {1, 1},
            {1, 0},
            {0, 0},
            {0, 1}
    };

    private ResourceLocation bindDirectionalTexture(TileServerRib te) {
        // Check if node is not active
        if (!te.isActive)
            return offDirectionalSide;
        // Return active directional side
        return directionalSide;
    }

    private ResourceLocation bindNondirectionalTexture(TileServerRib te) {
        // Check if node is not active
        if (!te.isActive)
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
                    Minecraft.getMinecraft().renderEngine.bindTexture(bindNondirectionalTexture(te));
                }
            } else {
                // Make rib non-directional
                Minecraft.getMinecraft().renderEngine.bindTexture(bindNondirectionalTexture(te));
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
        // Change light mapping to match current position of tile
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 210, 210);
    }

    private float[][] caesarShift(float[][] uvMap) {
        // Derange array. Move element at n position to n+1 position
        // {0,1},{2,3} -> {2,3},{0,1}
        // Specially for UV map it will rotate UV face, if starting UV face was:
        // A -> a B -> b C -> c D -> d
        // Then shifted will be:
        // A -> d B -> a C -> c D -> a
        // Create copy of UV map
        float[][] copy = uvMap.clone();

        // Iterate until i < length
        for (int i = 0; i < uvMap.length; i++){
            // Avoid index out of bound exception
            // Check if i = length - 1
            if ( i == uvMap.length - 1) {
                // Make first element equal to last
                copy[0] = uvMap[uvMap.length - 1];
            } else {
                // Make i+1 element equal to i element
                copy[i + 1] = uvMap[i];
            }
        }

        return copy;
    }

    private float[][] translateAxisToUV(TileServerRib te, EnumFacing side) {
        // Get tile line axis
        Axis axis = tileAxisMap.get(te);

        // Check not null
        if (axis == null)
            // Return basic UV state
            return defaultUV;

        // Check if axis is Y
        if (axis == Y)
            // Shift default UV
            return caesarShift(defaultUV);
        else if (axis == Z)
            // Check if side is placed on axis Y
            if (side.getAxis() == Y)
                // Shift default UV
                return caesarShift(defaultUV);

        return defaultUV;
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

        // Quad #1 (x - static) EAST
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, 0.5F},
                {0.5F, 0.5F, 0.5F},
                {0.5F, 0.5F, -0.5F},
                {0.5F, -0.5F, -0.5F},
        }, translateAxisToUV(te, EAST));

        // Quad #2 (-x - static) WEST
        drawQuadWithUV(new float[][] {
                {-0.5F, -0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, -0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, translateAxisToUV(te, WEST));

        // Quad #3 (y - static) UP
        drawQuadWithUV(new float[][] {
                {0.5F, 0.5F, -0.5F},
                {0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, -0.5F},
        }, translateAxisToUV(te, UP));

        // Quad #4 (-y - static) DOWN
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, -0.5F},
                {0.5F, -0.5F, 0.5F},
                {-0.5F, -0.5F, 0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, translateAxisToUV(te, DOWN));

        // Quad #5 (z - static) SOUTH
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, 0.5F},
                {0.5F, 0.5F, 0.5F},
                {-0.5F, 0.5F, 0.5F},
                {-0.5F, -0.5F, 0.5F},
        }, translateAxisToUV(te, SOUTH));

        // Quad #6 (-z - static) NORTH
        drawQuadWithUV(new float[][] {
                {0.5F, -0.5F, -0.5F},
                {0.5F, 0.5F, -0.5F},
                {-0.5F, 0.5F, -0.5F},
                {-0.5F, -0.5F, -0.5F},
        }, translateAxisToUV(te, NORTH));

        pushMatrix(x, y, z);
    }
}