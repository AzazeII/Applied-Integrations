package AppliedIntegrations.API;

import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import appeng.api.storage.data.IAEItemStack;
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

    void addStack(IAEStack<?> stack);

    IItemList<?> getList(Class<?> stackClassOperated);

    @SideOnly(CLIENT)
    void setMassFromServer(long mass);

    long getMass();

    boolean isEntangled();

    void setEntangledHole(ISingularity t);
}
