/*package AppliedIntegrations.Blocks.LogicBus.modeling;

import AppliedIntegrations.AppliedIntegrations;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.function.Function;

public class LogicBusModel implements IModel {
    private static final ModelResourceLocation MODEL_PORT =
            new ModelResourceLocation(AppliedIntegrations.modid+":block/logic_bus/logic_port" );

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of(MODEL_PORT);
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return LogicBusBakedModel.getRequiredTextures();
    }

    @Override
    public IBakedModel bake( IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
    {
        IBakedModel portModel = getBaseModel(MODEL_PORT, state, format, bakedTextureGetter );
        return new LogicBusBakedModel( format, portModel, bakedTextureGetter );
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    private IBakedModel getBaseModel( ResourceLocation model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
    {
        // Load the base model
        try
        {
            return ModelLoaderRegistry.getModel( model ).bake( state, format, bakedTextureGetter );
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}*/
