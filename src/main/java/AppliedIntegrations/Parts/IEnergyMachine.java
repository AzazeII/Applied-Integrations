package AppliedIntegrations.Parts;

import AppliedIntegrations.API.LiquidAIEnergy;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

// @Author Azazell
public interface IEnergyMachine
{
    void updateFilter(LiquidAIEnergy energy, int index);
}
