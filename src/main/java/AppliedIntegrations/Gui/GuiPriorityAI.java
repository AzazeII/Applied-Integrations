package AppliedIntegrations.Gui;

import AppliedIntegrations.API.IPriorityHostExtended;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import appeng.client.gui.implementations.GuiPriority;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.GuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiPriorityAI extends GuiPriority {
    private IPriorityHostExtended host;
    private GuiButton originalTab;

    public GuiPriorityAI(InventoryPlayer inventory, IPriorityHostExtended priorityHost) {
        super(inventory, priorityHost);

        // Owner of original gui
        this.host = priorityHost;
    }

    public void initGui(){
        // Pass call to super
        super.initGui();
        // Add original tab button to button list
        this.buttonList.add( this.originalTab = new GuiTabButton( this.guiLeft + 154,
                                                    this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(),
                                                    this.itemRender ));
    }

    @Override
    protected void actionPerformed( final GuiButton btn ) throws IOException {
        super.actionPerformed( btn );

        // Check if button is original tab
        if( btn == originalTab) {
            // Switch gui to original gui
            NetworkHandler.sendToServer( new PacketGuiShift( host.getGui(), host ));
        }
    }
}
