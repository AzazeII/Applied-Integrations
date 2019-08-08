package AppliedIntegrations.Gui.MultiController.SubGui.Buttons;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerTerminal;
import AppliedIntegrations.Gui.MultiController.GuiMultiControllerTerminal;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.api.AIApi;
import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;

/**
 * @Author Azazell
 */

// this one is special
public class GuiStorageChannelButton extends GuiMultiControllerButton {
	// Current storage channel of button
	private IStorageChannel<? extends IAEStack<?>> channel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);

	public GuiStorageChannelButton(GuiMultiControllerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {

		super(terminal, ID, xPosition, yPosition, width, height, text);
	}

	public void cycleChannel() {
		// Check if channel is last channel in list
		if (channel == ContainerMultiControllerTerminal.channelList.get(ContainerMultiControllerTerminal.channelList.size() - 1)) {

			// Make channel first in list
			channel = ContainerMultiControllerTerminal.channelList.get(0);
		} else {
			// Make channel next in list
			channel = ContainerMultiControllerTerminal.channelList.get(ContainerMultiControllerTerminal.channelList.indexOf(channel) + 1);
		}
	}

	public IStorageChannel<? extends IAEStack<?>> getChannel() {

		return channel;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		// Check if host GUI has no card
		if (!host.isCardValid()) {
			return;
		}

		// Get Api
		AIApi api = Objects.requireNonNull(AIApi.instance());

		// Disable lighting
		GL11.glDisable(GL11.GL_LIGHTING);

		// Full white
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		// Bind background sprite
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png"));

		// Draw texture
		drawTexturedModalRect(x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2);

		// Bind current sprite from channel
		Minecraft.getMinecraft().renderEngine.bindTexture(api.getSpriteFromChannel(channel));

		// Draw texture
		drawTexturedModalRect(x, y + 1, api.getSpriteU(channel), api.getSpriteV(channel), 16, 16);

		// Re-enable lighting
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void getTooltip(List<String> tip) {
		// Check if container has no network tool in slot
		if (!host.isCardValid()) {
			return;
		}

		// Add header
		tip.add("Storage Channel");

		// Split string to array
		String[] splitted = channel.getClass().getCanonicalName().split("\\.");

		// Add current channel name
		tip.add(splitted[splitted.length - 1]);
	}
}
