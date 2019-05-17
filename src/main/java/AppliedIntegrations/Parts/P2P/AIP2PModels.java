package AppliedIntegrations.Parts.P2P;


import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.parts.PartModel;
import net.minecraft.util.ResourceLocation;

/**
 * @Author Azazell
 *
 * This class is copy of class @(link P2PModels) from AE2, but for AI p2p tunnel parts
 */
public class AIP2PModels {

	public static final ResourceLocation MODEL_STATUS_OFF = new ResourceLocation( AppEng.MOD_ID, "part/p2p/p2p_tunnel_status_off" );
	public static final ResourceLocation MODEL_STATUS_ON = new ResourceLocation( AppEng.MOD_ID, "part/p2p/p2p_tunnel_status_on" );
	public static final ResourceLocation MODEL_STATUS_HAS_CHANNEL = new ResourceLocation( AppEng.MOD_ID, "part/p2p/p2p_tunnel_status_has_channel" );
	public static final ResourceLocation MODEL_FREQUENCY = new ResourceLocation( AppEng.MOD_ID, "part/builtin/p2p_tunnel_frequency" );

	private final IPartModel modelsOff;
	private final IPartModel modelsOn;
	private final IPartModel modelsHasChannel;

	public AIP2PModels( ResourceLocation frontModel ) {
		this.modelsOff = new PartModel( MODEL_STATUS_OFF, MODEL_FREQUENCY, frontModel );
		this.modelsOn = new PartModel( MODEL_STATUS_ON, MODEL_FREQUENCY, frontModel );
		this.modelsHasChannel = new PartModel( MODEL_STATUS_HAS_CHANNEL, MODEL_FREQUENCY, frontModel );
	}

	public IPartModel getModel( boolean hasPower, boolean hasChannel ) {
		if( hasPower && hasChannel ) {
			return this.modelsHasChannel;
		} else if( hasPower ) {
			return this.modelsOn;
		} else {
			return this.modelsOff;
		}
	}
}
