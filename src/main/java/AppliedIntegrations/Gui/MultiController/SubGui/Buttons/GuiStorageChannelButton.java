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
public class GuiStorageChannelButton extends GuiMultiControllerButton {
	private IStorageChannel<? extends IAEStack<?>> channel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);

	public GuiStorageChannelButton(GuiMultiControllerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {

		super(terminal, ID, xPosition, yPosition, width, height, text);
	}

	public void cycleChannel() {
		if (channel == ContainerMultiControllerTerminal.channelList.get(ContainerMultiControllerTerminal.channelList.size() - 1)) {
			channel = ContainerMultiControllerTerminal.channelList.get(0);
		} else {
			channel = ContainerMultiControllerTerminal.channelList.get(ContainerMultiControllerTerminal.channelList.indexOf(channel) + 1);
		}
	}

	public IStorageChannel<? extends IAEStack<?>> getChannel() {
		return channel;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!host.isCardValid()) {
			return;
		}

		AIApi api = Objects.requireNonNull(AIApi.instance());
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png"));
		drawTexturedModalRect(x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2);

		Minecraft.getMinecraft().renderEngine.bindTexture(api.getSpriteFromChannel(channel));
		drawTexturedModalRect(x, y + 1, api.getSpriteU(channel), api.getSpriteV(channel), 16, 16);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void getTooltip(List<String> tip) {
		if (!host.isCardValid()) {
			return;
		}

		String[] splitted = channel.getClass().getCanonicalName().split("\\.");
		tip.add("Storage Channel");
		tip.add(splitted[splitted.length - 1]);
	}
}
