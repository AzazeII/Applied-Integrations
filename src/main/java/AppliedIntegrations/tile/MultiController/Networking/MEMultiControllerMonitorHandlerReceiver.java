package AppliedIntegrations.tile.MultiController.Networking;


import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;

/**
 * @Author Azazell
 */
public class MEMultiControllerMonitorHandlerReceiver<T extends IAEStack<T>> implements IMEMonitorHandlerReceiver<T> {
	private final TileMultiControllerCore host;

	private final IStorageChannel<? extends IAEStack<?>> channel;

	public MEMultiControllerMonitorHandlerReceiver(TileMultiControllerCore tileMultiControllerCore, IStorageChannel<? extends IAEStack<?>> channel) {
		this.host = tileMultiControllerCore;
		this.channel = channel;
	}

	@Override
	public boolean isValid(Object verificationToken) {
		// True, as this receiver SHOULD BE destroyed by tile server core
		// at multi-block destroy
		return true;
	}

	@Override
	public void postChange(IBaseMonitor<T> monitor, Iterable<T> change, IActionSource actionSource) {
		try {
			// As api says:
			//	 * Used to inform the network of alterations to the storage system that fall outside of the standard Network
			//   * operations, Examples, ME Chest inputs from the world, or a Storage Bus detecting modifications made to the chest
			//   * by an outside force.
			// So, currently alteration should be explicitly called,
			// as adjacent network storage grid can't get storage updates from main network storage grid
			host.postNetworkAlterationsEvents(channel, change, new MachineSource(host));
		}catch (GridAccessException ignored) {}
	}

	@Override
	public void onListUpdate() {

	}
}
