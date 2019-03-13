package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.Parts.AIPlanePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartModel;

public class PartEnergyFormation extends AIPlanePart {
    public PartEnergyFormation() {
        super(PartEnum.EnergyFormation, SecurityPermissions.EXTRACT);
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
