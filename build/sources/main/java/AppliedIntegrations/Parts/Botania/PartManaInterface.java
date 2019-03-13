package AppliedIntegrations.Parts.Botania;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaChannel;
import AppliedIntegrations.API.Botania.IManaInterface;
import AppliedIntegrations.API.IInterfaceDuality;
import AppliedIntegrations.Helpers.ManaInterfaceDuality;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.grid.Mana.AEManaStack;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartModel;
import appeng.me.helpers.MachineSource;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Optional;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

import javax.annotation.Nonnull;
import java.util.List;

import static AppliedIntegrations.Utils.EffectiveSide.isServerSide;
import static appeng.api.networking.ticking.TickRateModulation.IDLE;

@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "vazkii.botania.api.mana.spark.ISparkAttachable", modid = "botania", striprefs = true),
        @Optional.Interface(iface = "vazkii.botania.api.mana.spark.ISparkEntity", modid = "botania", striprefs = true),
        @Optional.Interface(iface = "vazkii.botania.api.mana.IManaReceiver", modid = "botania", striprefs = true),

})
public class PartManaInterface extends PartEnergyInterface implements IManaReceiver, ISparkAttachable, IManaInterface {

    private int currentMana = 0;
    private final int capacity = 100000;

    private boolean isManaFiltered = false;

    public PartManaInterface() {
        super(PartEnum.ManaInterface, SecurityPermissions.INJECT, SecurityPermissions.EXTRACT);
    }

    @Override
    public boolean isFull() {
        return currentMana == capacity;
    }

    @Override
    public void recieveMana(int mana) {
        currentMana += mana;
        if(currentMana > capacity)
            currentMana = capacity;
        if(currentMana < 0)
            currentMana = 0;
    }

    @Nonnull
    @Override
    public IPartModel getStaticModels() {
        if (this.isPowered())
            if (this.isActive())
                return PartModelEnum.STORAGE_INTERFACE_MANA_HAS_CHANNEL;
            else
                return PartModelEnum.STORAGE_INTERFACE_MANA_ON;
        return PartModelEnum.STORAGE_INTERFACE_MANA_OFF;
    }

    @Override
    public IInterfaceDuality getDuality(){
        return new ManaInterfaceDuality(this);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int TicksSinceLastCall )
    {
        if(isServerSide()){
            try {
                if (isManaFiltered) {
                    DoExtractDualityWork(Actionable.MODULATE);
                } else {
                    DoInjectDualityWork(Actionable.MODULATE);
                }
            }catch (NullNodeConnectionException e){

            }
        }
        return IDLE;
    }

    @Override
    public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
        if(isServerSide()) {

        }
        return true;
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public int getCurrentMana() {
        return currentMana;
    }

    @Override
    public boolean canAttachSpark(ItemStack itemStack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity iSparkEntity) { }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(0, capacity - getCurrentMana());
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        List<Entity> sparks = getHostTile().getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getHostTile().getPos().up()
                , getHostTile().getPos().up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
        if(sparks.size() == 1) {
            Entity e = sparks.get(0);
            return (ISparkEntity) e;
        }

        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }
    /**
     * @param resource
     * 	Resource to be extracted
     * @param actionable
     * 	Simulate of Modulate?
     * @return
     * 	amount extracted
     */
    public int ExtractMana(int resource, Actionable actionable) {
        if(node == null)
            return 0;
        IGrid grid = node.getGrid();
        if (grid == null) {
            AILog.info("Grid cannot be initialized");
            return 0;
        }

        IStorageGrid storage = (IStorageGrid)grid.getCache(IStorageGrid.class);
        if (storage == null) {
            AILog.info("StorageGrid cannot be initialized");
            return 0;
        }

        IAEManaStack notRemoved = (IAEManaStack)storage.getInventory(getManaChannel()).extractItems(
                new AEManaStack(resource), actionable, new MachineSource(this));

        if (notRemoved == null)
            return (int)resource;
        return (int)(resource - notRemoved.getStackSize());
    }

    /**
     * @param resource
     * 	Resource to be injected
     * @param actionable
     * 	Simulate or modulate?
     * @return
     *  amount injected
     */
    public int InjectMana(int resource, Actionable actionable) {
        if(node == null)
            return 0;
        IGrid grid = node.getGrid(); // check grid node
        if (grid == null) {
            AILog.info("Grid cannot be initialized");
            return 0;
        }

        IStorageGrid storage = grid.getCache(IStorageGrid.class); // check storage gridnode
        if (storage == null && this.node.getGrid().getCache(IStorageGrid.class) == null) {
            AILog.info("StorageGrid cannot be initialized");
            return 0;
        }

        IAEManaStack returnAmount = storage.getInventory(this.getManaChannel()).injectItems(
                new AEManaStack(resource), actionable, new MachineSource(this));

        if (returnAmount == null)
            return (int) resource;
        return (int) (resource - returnAmount.getStackSize());
    }

    @Override
    public int getManaStored() {
        return currentMana;
    }

    @Override
    public void modifyManaStorage(int mana) {
        this.currentMana += mana;

        if (currentMana > capacity) {
            currentMana = capacity;
        } else if (currentMana < 0) {
            currentMana = 0;
        }
    }

    private IManaChannel getManaChannel() {
        return AEApi.instance().storage().getStorageChannel(IManaChannel.class);
    }
}
