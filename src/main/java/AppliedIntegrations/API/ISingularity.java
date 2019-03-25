package AppliedIntegrations.API;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 *
 * Class, used to mark any singularity/hole
 */
public interface ISingularity {
    void addMass(long l);

    void addStack(IAEStack<?> stack);

    IItemList<?> getList(Class<?> stackClassOperated);
}
