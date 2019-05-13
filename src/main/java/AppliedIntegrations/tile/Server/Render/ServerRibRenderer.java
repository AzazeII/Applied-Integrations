package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Client.AITileFullRenderer;
import AppliedIntegrations.tile.Server.TileServerRib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;

import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.Axis.Y;
import static net.minecraft.util.EnumFacing.Axis.Z;

public class ServerRibRenderer extends AITileFullRenderer<TileServerRib> {

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
        // Save matrix to stack
        prepareMatrix(x, y, z);

        // Configure light blend
        setLightAmbient();

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