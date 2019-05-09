package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.api.AIApi;
import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */

// this one is special
public class GuiStorageChannelButton extends AIGuiButton {

    // Current storage channel of button
    private IStorageChannel<? extends IAEStack<?>> channel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);

    // Array list of all storage channels registered
    private final List<IStorageChannel<? extends IAEStack<?>>> channelList = new ArrayList<>(AEApi.instance().storage().storageChannels());

    public GuiStorageChannelButton(int ID, int xPosition, int yPosition, int width, int height, String text) {
        super(ID, xPosition, yPosition, width, height, text);
    }

    public void cycleChannel() {
        // Check if channel is last channel in list
        if (channel == channelList.get(channelList.size() - 1)){

            // Make channel first in list
            channel = channelList.get(0);
        } else {
            // Make channel next in list
            channel = channelList.get(channelList.indexOf(channel) + 1);
        }

    }

    public IStorageChannel<? extends IAEStack<?>> getChannel() {
        return channel;
    }

    public List<IStorageChannel<? extends IAEStack<?>>> getChannelList() {
        return channelList;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        // ---Drawing #1---//
        // Isolate changes from drawing #2
        GlStateManager.pushMatrix();

        // Disable lighting
        GL11.glDisable( GL11.GL_LIGHTING );

        // Full white
        GL11.glColor3f( 1.0F, 1.0F, 1.0F );

        // Bind to the gui texture
        Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png" ) );

        // Draw background of button
        drawTexturedModalRect( x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2 );

        // Isolate changes from drawing #2
        GlStateManager.popMatrix();
        // ---Drawing #1---//



        // ---Drawing #2---//
        // Isolate changes from drawing #1
        GlStateManager.pushMatrix();

        // Disable lighting
        GL11.glDisable( GL11.GL_LIGHTING );

        // Full white
        GL11.glColor3f( 1.0F, 1.0F, 1.0F );

        // Bind current sprite from channel
        Minecraft.getMinecraft().renderEngine.bindTexture(AIApi.instance().getSpriteFromChannel(channel));

        // Draw foreground of button
        drawTexturedModalRect( x, y, 0, 0, AIWidget.WIDGET_SIZE - 2, AIWidget.WIDGET_SIZE - 2);

        // Re-enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );

        // Isolate changes from drawing #1
        GlStateManager.popMatrix();
        // ---Drawing #2---//
    }

    @Override
    public void getTooltip(List<String> tip) {
        // Add header
        tip.add("Storage Channel");

        // Add current channel name
        tip.add(channel.getClass().getCanonicalName());
    }
}
