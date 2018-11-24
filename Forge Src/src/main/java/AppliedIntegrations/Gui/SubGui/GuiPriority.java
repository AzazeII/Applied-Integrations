package AppliedIntegrations.Gui.SubGui;

import AppliedIntegrations.AEFeatures.GuiText;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerPriority;
import AppliedIntegrations.Gui.AEStateIconsEnum;
import AppliedIntegrations.Gui.Buttons.GuiButtonAETab;
import AppliedIntegrations.Gui.GuiTextureManager;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Widgets.DigitTextField;

import appeng.helpers.IPriorityHost;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiPriority
        extends appeng.client.gui.implementations.GuiPriority
{
    public GuiPriority(InventoryPlayer inventoryPlayer, IPriorityHost te) {
        super(inventoryPlayer, te);
    }

}
