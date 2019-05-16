package AppliedIntegrations.Parts.Botania;

import AppliedIntegrations.Helpers.ManaInterfaceDuality;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Botania.IManaInterface;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.api.IEnergyInterfaceDuality;
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

import static appeng.api.networking.ticking.TickRateModulation.IDLE;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "vazkii.botania.api.mana.spark.ISparkAttachable", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.mana.spark.ISparkEntity", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.mana.IManaReceiver", modid = "botania", striprefs = true),

})
/**
 * @Author Azazell
 */ public class PartManaInterface extends PartEnergyInterface implements IManaReceiver, ISparkAttachable, IManaInterface {

	private final int capacity = 100000;
	private int currentMana = 0;
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
		if (currentMana > capacity) {
			currentMana = capacity;
		}
		if (currentMana < 0) {
			currentMana = 0;
		}
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return true;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		if (!getWorld().isRemote) {

		}
		return true;
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.STORAGE_INTERFACE_MANA_HAS_CHANNEL;
			} else {
				return PartModelEnum.STORAGE_INTERFACE_MANA_ON;
			}
		}
		return PartModelEnum.STORAGE_INTERFACE_MANA_OFF;
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int TicksSinceLastCall) {
		if (!getWorld().isRemote) {
			try {
				if (isManaFiltered) {
					doExtractDualityWork(Actionable.MODULATE);
				} else {
					doInjectDualityWork(Actionable.MODULATE);
				}
			} catch (NullNodeConnectionException e) {

			}
		}
		return IDLE;
	}

	@Override
	public IEnergyInterfaceDuality getDuality() {
		return new ManaInterfaceDuality(this);
	}

	@Override
	public boolean canAttachSpark(ItemStack itemStack) {
		return true;
	}

	@Override
	public void attachSpark(ISparkEntity iSparkEntity) {
	}

	@Override
	public int getAvailableSpaceForMana() {
		return Math.max(0, capacity - getCurrentMana());
	}

	@Override
	public int getCurrentMana() {
		return currentMana;
	}

	@Override
	public ISparkEntity getAttachedSpark() {
		List<Entity> sparks = getHostTile().getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getHostTile().getPos().up(), getHostTile().getPos().up().add(1, 1, 1)), Predicates.instanceOf(ISparkEntity.class));
		if (sparks.size() == 1) {
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
	 * @param resource   Resource to be extracted
	 * @param actionable Simulate of Modulate?
	 * @return amount extracted
	 */
	public int ExtractMana(int resource, Actionable actionable) {
		if (node == null) {
			return 0;
		}
		IGrid grid = node.getGrid();

		IStorageGrid storage = (IStorageGrid) grid.getCache(IStorageGrid.class);

		IAEManaStack notRemoved = (IAEManaStack) storage.getInventory(getManaChannel()).extractItems(new AEManaStack(resource), actionable, new MachineSource(this));

		if (notRemoved == null) {
			return (int) resource;
		}
		return (int) (resource - notRemoved.getStackSize());
	}

	/**
	 * @param resource   Resource to be injected
	 * @param actionable Simulate or modulate?
	 * @return amount injected
	 */
	public int InjectMana(int resource, Actionable actionable) {
		if (node == null) {
			return 0;
		}
		IGrid grid = node.getGrid(); // check grid node

		IStorageGrid storage = grid.getCache(IStorageGrid.class);

		IAEManaStack returnAmount = storage.getInventory(this.getManaChannel()).injectItems(new AEManaStack(resource), actionable, new MachineSource(this));

		if (returnAmount == null) {
			return (int) resource;
		}
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

	private IManaStorageChannel getManaChannel() {
		return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
	}
}
