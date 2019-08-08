package AppliedIntegrations.Gui.Widgets;
import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketTabChange;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @Author Azazell
 */
public class WidgetGuiTab extends AIWidget {
	private static final ResourceLocation INV_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private static final int ICON_OFFSET_X = 6;
	private static final int ICON_OFFSET_Y = 8;

	private final RenderItem itemRenderer;
	private final FontRenderer fontRenderer;
	private final int adjustentHeight;
	private final int width = 27;
	public final Enum tabEnum;
	private final ItemStack stack;
	public boolean isTabSelected;
	private String tabName;

	private WidgetGuiTab(IWidgetHost hostGUI, int xPos, int yPos, int adjustentHeight, boolean isSelected, Enum tabEnum,
	                    String tabName, ItemStack itemStack, RenderItem itemRenderer, FontRenderer fontRenderer) {
		super(hostGUI, xPos, yPos);
		this.tabEnum = tabEnum;
		this.isTabSelected = isSelected;
		this.tabName = tabName;
		this.adjustentHeight = adjustentHeight;
		this.itemRenderer = itemRenderer;
		this.fontRenderer = fontRenderer;
		this.stack = itemStack;
	}

	public WidgetGuiTab(IWidgetHost hostGUI, int xPos, int yPos, int adjustentHeight, boolean isSelected, Enum tabEnum,
	                    String tabName, Item item, RenderItem itemRenderer, FontRenderer fontRenderer) {
		this(hostGUI, xPos, yPos, adjustentHeight, isSelected, tabEnum, tabName, new ItemStack(item), itemRenderer, fontRenderer);
	}

	public WidgetGuiTab(IWidgetHost hostGUI, int xPos, int yPos, int adjustentHeight, boolean isSelected, Enum tabEnum,
	                    String tabName, Block block, RenderItem itemRender, FontRenderer fontRenderer) {
		this(hostGUI, xPos, yPos, adjustentHeight, isSelected, tabEnum, tabName, new ItemStack(block), itemRender, fontRenderer);
	}

	private int getHeight() {
		return isTabSelected ? 28 + adjustentHeight : 28;
	}

	private int getTextureY() {
		return isTabSelected ? 32 : 0;
	}

	public String getTabName() {
		return tabName;
	}

	public void mouseClicked() {
		NetworkHandler.sendToServer(new PacketTabChange(this.hostGUI.getSyncHost(), tabEnum));
		this.isTabSelected = true;
	}

	@Override
	public void drawWidget() {

		// Render widget background
		Minecraft.getMinecraft().renderEngine.bindTexture(INV_TABS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		drawTexturedModalRect(xPosition, yPosition, 0, getTextureY(), width, getHeight());

		// Render item
		this.itemRenderer.renderItemAndEffectIntoGUI(stack, xPosition + ICON_OFFSET_X, yPosition + ICON_OFFSET_Y);
		this.itemRenderer.renderItemOverlays(fontRenderer, stack, xPosition + ICON_OFFSET_X, yPosition + ICON_OFFSET_Y);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void getTooltip(List<String> tooltip) {

	}

	@Override
	public boolean isMouseOverWidget(final int mouseX, final int mouseY) {
		return AIGuiHelper.INSTANCE.isPointInGuiRegion(this.yPosition, this.xPosition,
				getHeight() - 1, width - 1, mouseX, mouseY, this.hostGUI.getLeft(), this.hostGUI.getTop());
	}
}
