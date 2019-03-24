/*package AppliedIntegrations.Client.render;

import AppliedIntegrations.Blocks.LogicBus.modeling.LogicBusBakedModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LogicBusModelBakeEventHandler {
    public static final LogicBusModelBakeEventHandler instance = new LogicBusModelBakeEventHandler();

    private LogicBusModelBakeEventHandler() {};

    // Called after all the other baked block models have been added to the modelRegistry
    // Allows us to manipulate the modelRegistry before BlockModelShapes caches them.
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event)
    {
        Object object =  event.getModelRegistry().getObject(LogicBusBakedModel.variantTagRib);
        if (object instanceof IBakedModel) {
            IBakedModel existingModel = (IBakedModel)object;
            LogicBusBakedModel customModel = new LogicBusBakedModel(existingModel);
            event.getModelRegistry().putObject(LogicBusBakedModel.variantTagRib, customModel);
        }
    }
}*/
