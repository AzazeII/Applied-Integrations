package AppliedIntegrations.API;

import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import appeng.api.config.Actionable;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 *
 * Class, used to mark any singularity/hole
 */
public interface ISingularity {
    void addMass(long l);

    IAEStack<?> addStack(IAEStack<?> stack, Actionable actionable);

    IItemList<?> getList(IStorageChannel iStorageChannel);

    @SideOnly(CLIENT)
    void setMassFromServer(long mass);

    long getMass();

    boolean isEntangled();

    void setEntangledHole(ISingularity t);

    void addListener(TileMEPylon pylon);
}
