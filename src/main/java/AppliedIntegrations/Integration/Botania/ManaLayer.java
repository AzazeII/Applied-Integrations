package AppliedIntegrations.Integration.Botania;

import appeng.api.parts.IPart;
import appeng.api.parts.LayerBase;
import appeng.api.util.AEPartLocation;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

/**
 * @Author Azazell
 */
public class ManaLayer extends LayerBase implements IManaReceiver, ISparkAttachable {

	@Override
	public boolean isFull() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IManaReceiver) {
				return ((IManaReceiver) part).isFull();
			}
		}

		return true;
	}

	@Override
	public void recieveMana(int i) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IManaReceiver) {
				((IManaReceiver) part).recieveMana(i);
			}
		}
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IManaReceiver) {
				return ((IManaReceiver) part).canRecieveManaFromBursts();
			}
		}

		return false;
	}

	@Override
	public int getCurrentMana() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IManaReceiver) {
				return ((IManaReceiver) part).getCurrentMana();
			}
		}

		return 0;
	}

	@Override
	public boolean canAttachSpark(ItemStack itemStack) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ISparkAttachable) {
				return ((ISparkAttachable) part).canAttachSpark(itemStack);
			}
		}

		return false;
	}

	@Override
	public void attachSpark(ISparkEntity iSparkEntity) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ISparkAttachable) {
				((ISparkAttachable) part).attachSpark(iSparkEntity);
			}
		}
	}

	@Override
	public int getAvailableSpaceForMana() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ISparkAttachable) {
				return ((ISparkAttachable) part).getAvailableSpaceForMana();
			}
		}

		return 0;
	}

	@Override
	public ISparkEntity getAttachedSpark() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ISparkAttachable) {
				return ((ISparkAttachable) part).getAttachedSpark();
			}
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ISparkAttachable) {
				return ((ISparkAttachable) part).areIncomingTranfersDone();
			}
		}

		return false;
	}
}
