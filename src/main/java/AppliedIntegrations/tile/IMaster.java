package AppliedIntegrations.tile;

import appeng.api.networking.IGridNode;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Iterator;

/**
 * @Author Azazell
 */
public interface IMaster {
    Iterator<IGridNode> getMultiblockNodes();
}
