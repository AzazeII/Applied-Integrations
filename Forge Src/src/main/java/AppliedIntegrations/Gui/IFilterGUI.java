package AppliedIntegrations.Gui;

import AppliedIntegrations.API.LiquidAIEnergy;

import javax.annotation.Nonnull;
import java.util.List;
/**
 * @Author Azazell
 */
public interface IFilterGUI {
    void updateEnergies( LiquidAIEnergy energy, int index);
}
