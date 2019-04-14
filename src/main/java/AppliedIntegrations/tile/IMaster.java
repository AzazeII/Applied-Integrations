package AppliedIntegrations.tile;

import appeng.api.networking.IGridNode;

import java.util.Iterator;

/**
 * @Author Azazell
 */
public interface IMaster {
    Iterator<IGridNode> getMultiblockNodes();
}
