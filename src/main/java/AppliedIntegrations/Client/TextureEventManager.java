package AppliedIntegrations.Client;


import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @Author Azazell
 */
public class TextureEventManager {
	@SubscribeEvent
	public void textureStich(TextureStitchEvent.Pre event) {
		// This used to register gui-container textures in atlas of minecraft
		event.getMap().registerSprite(new ResourceLocation(AppliedIntegrations.modid + ":gui/slots/UpgradeSlotIcon"));
		event.getMap().registerSprite(new ResourceLocation(AppliedIntegrations.modid + ":gui/slots/network_card_slot"));
		event.getMap().registerSprite(new ResourceLocation(AppliedIntegrations.modid + ":gui/slots/server_cell_slot"));
	}
}
