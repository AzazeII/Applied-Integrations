package AppliedIntegrations.tile;


import AppliedIntegrations.api.ISyncHost;
import appeng.api.networking.IGridNode;

import java.util.Iterator;

/**
 * @Author Azazell
 */
public interface IMaster extends ISyncHost {
	/**
	 * @return List of nodes in structure
	 */
	Iterator<IGridNode> getMultiblockNodes();
}
