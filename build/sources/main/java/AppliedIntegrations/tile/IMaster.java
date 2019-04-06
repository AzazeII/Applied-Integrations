package AppliedIntegrations.tile;

import appeng.api.networking.IGridNode;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Iterator;

/**
 * @Author Azazell
 */
public interface IMaster {
    IMaster readMaster(NBTTagCompound compound);

    void writeMaster(NBTTagCompound compound);

    Iterator<IGridNode> getMultiblockNodes();
}
