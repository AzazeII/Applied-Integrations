/*package AppliedIntegrations.Client.render;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.LogicBus.modeling.LogicBusModel;
import AppliedIntegrations.Utils.AILog;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class LogicBusModelLoader implements ICustomModelLoader {

    public final String MODEL_PATH = "models/block/logic_bus/";
    private IResourceManager resourceManager;

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if(!modelLocation.getResourceDomain().equals(AppliedIntegrations.modid))
            return false;
        if(modelLocation.getResourcePath().startsWith(MODEL_PATH))
            return true;
        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        String resourcePath = modelLocation.getResourcePath();

        if (!resourcePath.startsWith(MODEL_PATH)) {
            assert false : "loadModel expected " + MODEL_PATH + " but found " + resourcePath;
        }

        String modelName = resourcePath.substring(MODEL_PATH.length());

        if (modelName.equals("logic_ribs_formed") || modelName.equals("logic_port_formed")) {
            return new LogicBusModel();
        } else {
            return ModelLoaderRegistry.getMissingModel();
        }
    }
}*/
