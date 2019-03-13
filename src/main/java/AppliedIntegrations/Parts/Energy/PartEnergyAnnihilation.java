package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartModel;
import appeng.parts.automation.PlaneModels;

public class PartEnergyAnnihilation extends AIPlanePart {
    private static final PlaneModels MODELS = new PlaneModels( "part/annihilation_plane_", "part/annihilation_plane_on_" );

    public PartEnergyAnnihilation() {
        super(PartEnum.EnergyAnnihilation, SecurityPermissions.INJECT);
    }

    @Override
    public IPartModel getStaticModels() {
        if(isPowered()){
            if(isActive()) {
                return PartModelEnum.ANNIHILATION_HAS_CHANNEL;
            }else {
                return PartModelEnum.ANNIHILATION_ON;
            }
        }
        return PartModelEnum.ANNIHILATION_OFF;
    }
}
