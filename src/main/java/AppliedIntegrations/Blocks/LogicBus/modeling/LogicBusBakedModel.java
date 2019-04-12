/*package AppliedIntegrations.Blocks.LogicBus.modeling;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlocksEnum;
import appeng.client.render.cablebus.CubeBuilder;
import appeng.core.AppEng;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class LogicBusBakedModel implements IBakedModel {

    private static final ResourceLocation TEXTURE_RIB =
            new ResourceLocation( AppliedIntegrations.modid, "blocks/block_logic_rib" );

    private static final ResourceLocation TEXTURE_PORT =
            new ResourceLocation( AppliedIntegrations.modid, "blocks/block_logic_port" );

    private static final ResourceLocation TEXTURE_CABLE_GLASS
            = new ResourceLocation( AppEng.MOD_ID, "parts/cable/glass/transparent" );

    private static final ResourceLocation TEXTURE_COVERED_CABLE
            = new ResourceLocation( AppEng.MOD_ID, "parts/cable/covered/transparent" );

    public static final ModelResourceLocation variantTagPort
            = new ModelResourceLocation( "appliedintegrations:blocklogicbusport");
    public static final ModelResourceLocation variantTagRib
            = new ModelResourceLocation("appliedintegrations:blocklogicbusribs");

    private static final float DEFAULT_RENDER_MIN = 2.0f;
    private static final float DEFAULT_RENDER_MAX = 14.0f;

    private final TextureAtlasSprite ribTexture;
    private final TextureAtlasSprite portTexture;
    private final TextureAtlasSprite glassCableTexture;
    private final TextureAtlasSprite coveredCableTexture;
    private final IBakedModel baseModel;

    public LogicBusBakedModel(IBakedModel base){

        baseModel = base;

        ribTexture = bakedTextureGetter.apply(TEXTURE_RIB);
        portTexture = bakedTextureGetter.apply(TEXTURE_PORT);
        glassCableTexture = bakedTextureGetter.apply(TEXTURE_CABLE_GLASS);
        coveredCableTexture = bakedTextureGetter.apply(TEXTURE_COVERED_CABLE);
    }

    @Override
    public List<BakedQuad> getQuads( @Nullable IBlockState state, @Nullable EnumFacing side, long rand )
    {
        // Get the correct base model
        if( !( state instanceof IExtendedBlockState) )
        {
            return this.baseModel.getQuads( state, side, rand );
        }

        // Get extended state
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        // Get formed state from extended by using property
        LogicBusState formedState = extendedBlockState.getValue( ModeledLogicBus.stateProp );

        return this.getQuads( formedState, state, side, rand );
    }

    private List<BakedQuad> getQuads( LogicBusState formedState, IBlockState state, EnumFacing side, long rand )
    {
        // Create cube builder
        CubeBuilder builder = new CubeBuilder(new VertexFormat());

        // Check if block is port
        if( state.getBlock() == BlocksEnum.BLBPort.b)
        {
            // Sides where cables should be rendered
            Set<EnumFacing> sides = formedState.getSidesWithSlave();

            // Render cables
            this.renderCableAt( builder, 0.11f * 16, this.glassCableTexture, 0.141f * 16, sides );

            this.renderCableAt( builder, 0.188f * 16, this.coveredCableTexture, 0.1875f * 16, sides );
            builder.setTexture( ribTexture );
            builder.addCube( DEFAULT_RENDER_MIN, DEFAULT_RENDER_MIN, DEFAULT_RENDER_MIN, DEFAULT_RENDER_MAX, DEFAULT_RENDER_MAX, DEFAULT_RENDER_MAX );
        }
        else
        {
            // If Tile is corner
            if( formedState.isCorner() )
            {
                // render one cable
                this.renderCableAt( builder, 0.188f * 16, this.coveredCableTexture, 0.05f * 16, formedState.getSidesWithSlave() );

                builder.setTexture( portTexture );
                builder.addCube( DEFAULT_RENDER_MIN, DEFAULT_RENDER_MIN, DEFAULT_RENDER_MIN, DEFAULT_RENDER_MAX, DEFAULT_RENDER_MAX, DEFAULT_RENDER_MAX );
            }
            else
            {
                builder.setTexture( portTexture );

                builder.addCube( 0, DEFAULT_RENDER_MIN, DEFAULT_RENDER_MIN, 16, DEFAULT_RENDER_MAX, DEFAULT_RENDER_MAX );

                builder.addCube( DEFAULT_RENDER_MIN, 0, DEFAULT_RENDER_MIN, DEFAULT_RENDER_MAX, 16, DEFAULT_RENDER_MAX );

                builder.addCube( DEFAULT_RENDER_MIN, DEFAULT_RENDER_MIN, 0, DEFAULT_RENDER_MAX, DEFAULT_RENDER_MAX, 16 );
            }
        }

        return builder.getOutput();
    }

    private void renderCableAt( CubeBuilder builder, float thickness, TextureAtlasSprite texture, float pull, Set<EnumFacing> connections )
    {
        // Set texture to provided texture
        builder.setTexture( texture );

        // Check if connections contain WEST
        if( connections.contains( EnumFacing.WEST ) )
        {
            builder.addCube( 0, 8 - thickness, 8 - thickness, 8 - thickness - pull, 8 + thickness, 8 + thickness );
        }

        // Check if connections contain EAST
        if( connections.contains( EnumFacing.EAST ) )
        {
            builder.addCube( 8 + thickness + pull, 8 - thickness, 8 - thickness, 16, 8 + thickness, 8 + thickness );
        }

        // Check if connections contain NORTH
        if( connections.contains( EnumFacing.NORTH ) )
        {
            builder.addCube( 8 - thickness, 8 - thickness, 0, 8 + thickness, 8 + thickness, 8 - thickness - pull );
        }

        // Check if connections contain SOUTH
        if( connections.contains( EnumFacing.SOUTH ) )
        {
            builder.addCube( 8 - thickness, 8 - thickness, 8 + thickness + pull, 8 + thickness, 8 + thickness, 16 );
        }

        // Check if connections contain DOWN
        if( connections.contains( EnumFacing.DOWN ) )
        {
            builder.addCube( 8 - thickness, 0, 8 - thickness, 8 + thickness, 8 - thickness - pull, 8 + thickness );
        }

        // Check if connections contain UP
        if( connections.contains( EnumFacing.UP ) )
        {
            builder.addCube( 8 - thickness, 8 + thickness + pull, 8 - thickness, 8 + thickness, 16, 8 + thickness );
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return baseModel.getOverrides();
    }

    public static Collection<ResourceLocation> getRequiredTextures() {
        List<ResourceLocation> list = new ArrayList<>();

        list.add(TEXTURE_PORT);
        list.add(TEXTURE_RIB);
        list.add(TEXTURE_COVERED_CABLE);
        list.add(TEXTURE_CABLE_GLASS);

        return list;
    }

}*/
