package AppliedIntegrations.Utils;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @Author Azazell
 */
public final class EffectiveSide
{

	private static FMLCommonHandler FCH = FMLCommonHandler.instance();

	// Checks thread for client
	public static final boolean isClientSide()
	{
		return FCH.getEffectiveSide().isClient();
	}

	// Checks thread for server
	public static final boolean isServerSide()
	{
		return FCH.getEffectiveSide().isServer();
	}

	// Get side
	public static final Side side()
	{
		return FCH.getEffectiveSide();
	}
}
