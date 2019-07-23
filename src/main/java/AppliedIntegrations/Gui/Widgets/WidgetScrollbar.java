package AppliedIntegrations.Gui.Widgets;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import appeng.client.gui.widgets.IScrollSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @Author Azazell
 */
public class WidgetScrollbar extends AIWidget {
	private double step;
	private int minScroll;
	private int maxScroll;

	public WidgetScrollbar(IWidgetHost hostGUI, int xPos, int yPos) {
		super(hostGUI, xPos, yPos);
		this.minScroll = yPos;
	}

	public double onWheel(double diff) {
		// Calculate difference
		diff = Math.max( Math.min( -diff, step ), -step );

		// Memorize old position
		double oldPos = yPosition;

		// Change current scroll value
		yPosition = (int) Math.min( Math.max(yPosition + diff, minScroll), maxScroll);

		return oldPos != yPosition ? diff : 0;
	}

	public WidgetScrollbar setMaxScroll(int maxScroll) {
		this.maxScroll = maxScroll;
		return this;
	}

	public WidgetScrollbar setScrollStep(double scrollStep) {
		this.step = scrollStep;
		return this;
	}

	@Override
	public void drawWidget() {
		// Disable lighting
		GL11.glDisable(GL11.GL_LIGHTING);

		// Full white
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		// Bind texture of scrollbar in creative tab
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tabs.png"));

		// Nullify color
		GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Draw bound texture
		drawTexturedModalRect(xPosition, yPosition, 244, 0, 12, 15);

		// Re-enable lighting
		GL11.glEnable(GL11.GL_LIGHTING);
	}


	@Override
	public void getTooltip(List<String> tooltip) {
		// Ignored
	}

	public static class GuiScrollbar extends AIWidget implements IScrollSource {

		private static final int scrollHeight = 15;
		private static final int width = 12;
		private int pageSize = 1;

		private int maxScroll = 0;
		private int currentScroll = 0;
		private int scrollFieldHeight = 88;

		public GuiScrollbar(IWidgetHost hostGUI, int xPos, int yPos) {
			super(hostGUI, xPos, yPos);
			//this.minScroll = yPos;
		}

		@Override
		public void drawWidget() {
			Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation("minecraft",
					"textures/gui/container/creative_inventory/tabs.png"));
			GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );

			final int offset = this.currentScroll * (scrollFieldHeight - scrollHeight) / this.maxScroll;
			this.drawTexturedModalRect( this.xPosition, offset + this.yPosition, 232, 0, width, scrollHeight );
		}

		@Override
		public void getTooltip(List<String> tooltip) {}

		public void setRange( final int max, final int pageSize ) {
			this.maxScroll = max;
			this.pageSize = pageSize;
			this.applyRange();
		}

		private void applyRange() {
			this.currentScroll = Math.max( Math.min( this.currentScroll, this.maxScroll ), 0 );
		}

		@Override
		public int getCurrentScroll() {
			return this.currentScroll;
		}

		public void click(final int x, final int y) {
			if( x > this.xPosition && x <= this.xPosition + width ) {
				if( y > this.yPosition && y <= this.yPosition + this.scrollFieldHeight) {
					this.currentScroll = ( y - this.yPosition );
					this.currentScroll = this.currentScroll * 2 * maxScroll / this.scrollFieldHeight;
					this.currentScroll = ( this.currentScroll + 1 ) >> 1;
					this.applyRange();
				}
			}
		}

		public int wheel(int delta ) {
			int oldScroll = currentScroll;
			delta = Math.max(Math.min(-delta, 1), -1);
			this.currentScroll += delta * this.pageSize;
			this.applyRange();

			return currentScroll != oldScroll ? delta : 0;
		}
	}
}
