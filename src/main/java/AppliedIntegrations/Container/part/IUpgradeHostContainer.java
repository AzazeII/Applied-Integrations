package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.Sync.ISyncHostHolder;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;

/**
 * @Author Azazell
 */
public interface IUpgradeHostContainer extends ISyncHostHolder {
	void updateState(boolean redstoneControl, boolean compareFuzzy, FuzzyMode fuzzyMode, RedstoneMode redstoneMode, byte filterSize);
}
