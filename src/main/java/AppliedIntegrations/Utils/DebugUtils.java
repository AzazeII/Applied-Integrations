package AppliedIntegrations.Utils;


import net.minecraft.client.Minecraft;

/**
 * @Author Azazell
 * This class isn't part of mod itself. It used only in debugger evaluation window
 */
public class DebugUtils {
	public static void dropGuiFrame() {

		Minecraft.getMinecraft().player.closeScreen();
	}
}