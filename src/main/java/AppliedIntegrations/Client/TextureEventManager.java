package AppliedIntegrations.Client;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TextureEventManager {
    @SubscribeEvent
    public void textureStich(TextureStitchEvent.Pre event) {
        // Register sprite atlas for texture of upgrade slot
        event.getMap().registerSprite(new ResourceLocation(AppliedIntegrations.modid + ":gui/slots/UpgradeSlotIcon"));
    }
}
