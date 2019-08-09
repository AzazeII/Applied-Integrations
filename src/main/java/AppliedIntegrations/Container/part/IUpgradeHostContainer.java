package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.Sync.ISyncHostHolder;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.YesNo;

/**
 * @Author Azazell
 */
public interface IUpgradeHostContainer extends ISyncHostHolder {
	void updateState(boolean redstoneControl, boolean compareFuzzy, boolean autoCrafting, RedstoneMode redstoneMode,
	                 FuzzyMode fuzzyMode, YesNo craftOnly, byte filterSize);
}
