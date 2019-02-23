package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.Parts.AIPlatePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartModel;

public class PartEnergyAnnihilation extends AIPlatePart {
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
