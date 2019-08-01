package AppliedIntegrations.Gui.Widgets;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketTabChange;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @Author Azazell
 */
public class WidgetGuiTab extends AIWidget {
	private static final ResourceLocation INV_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private final int adjustentHeight;
	private final int width = 27;
	public final Enum tabEnum;
	public boolean isTabSelected;
	private String tabName;

	public WidgetGuiTab(IWidgetHost hostGUI, int xPos, int yPos, int adjustentHeight, boolean isSelected, Enum tabEnum, String tabName) {
		super(hostGUI, xPos, yPos);
		this.tabEnum = tabEnum;
		this.isTabSelected = isSelected;
		this.tabName = tabName;
		this.adjustentHeight = adjustentHeight;
	}

	private int getHeight() {
		return isTabSelected ? 28 + adjustentHeight : 28;
	}

	private int getTextureY() {
		return isTabSelected ? 32 : 0;
	}

	@Override
	public void drawWidget() {
		Minecraft.getMinecraft().renderEngine.bindTexture(INV_TABS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPosition, yPosition, 0, getTextureY(), width, getHeight());
	}

	@Override
	public void getTooltip(List<String> tooltip) {

	}

	@Override
	public boolean isMouseOverWidget(final int mouseX, final int mouseY) {
		return AIGuiHelper.INSTANCE.isPointInGuiRegion(this.yPosition, this.xPosition,
				getHeight() - 1, width - 1, mouseX, mouseY, this.hostGUI.getLeft(), this.hostGUI.getTop());
	}

	public String getTabName() {
		return tabName;
	}

	public void mouseClicked() {
		NetworkHandler.sendToServer(new PacketTabChange(this.hostGUI.getSyncHost(), tabEnum));
		this.isTabSelected = true;
	}
}
