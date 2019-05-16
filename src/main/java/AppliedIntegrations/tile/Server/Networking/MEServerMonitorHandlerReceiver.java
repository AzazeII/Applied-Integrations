package AppliedIntegrations.tile.Server.Networking;


import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.me.helpers.MachineSource;

public class MEServerMonitorHandlerReceiver<T extends IAEStack<T>> implements IMEMonitorHandlerReceiver<T> {
	private final TileServerCore host;

	private final IStorageChannel<? extends IAEStack<?>> channel;

	public MEServerMonitorHandlerReceiver(TileServerCore tileServerCore, IStorageChannel<? extends IAEStack<?>> channel) {

		this.host = tileServerCore;
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
		// As api says:
		//	 * Used to inform the network of alterations to the storage system that fall outside of the standard Network
		//   * operations, Examples, ME Chest inputs from the world, or a Storage Bus detecting modifications made to the chest
		//   * by an outside force.
		// So, currently alteration should be explicitly called,
		// as adjacent network storage grid can't get storage updates from main network storage grid
		host.postNetworkAlterationsEvents(channel, change, new MachineSource(host));
	}

	@Override
	public void onListUpdate() {

	}
}
