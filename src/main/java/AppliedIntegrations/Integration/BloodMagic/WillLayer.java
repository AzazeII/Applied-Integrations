package AppliedIntegrations.Integration.BloodMagic;


import WayofTime.bloodmagic.soul.EnumDemonWillType;
import WayofTime.bloodmagic.soul.IDemonWillConduit;
import appeng.api.parts.IPart;
import appeng.api.parts.LayerBase;
import appeng.api.util.AEPartLocation;

public class WillLayer extends LayerBase implements IDemonWillConduit {
	@Override
	public int getWeight() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IDemonWillConduit) {
				return ((IDemonWillConduit) part).getWeight();
			}
		}

		return 0;
	}

	@Override
	public double fillDemonWill(EnumDemonWillType type, double amount, boolean doFill) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IDemonWillConduit) {
				return ((IDemonWillConduit) part).fillDemonWill(type, amount, doFill);
			}
		}

		return 0;
	}

	@Override
	public double drainDemonWill(EnumDemonWillType type, double amount, boolean doDrain) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IDemonWillConduit) {
				return ((IDemonWillConduit) part).drainDemonWill(type, amount, doDrain);
			}
		}

		return 0;
	}

	@Override
	public boolean canFill(EnumDemonWillType type) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IDemonWillConduit) {
				return ((IDemonWillConduit) part).canFill(type);
			}
		}

		return false;
	}

	@Override
	public boolean canDrain(EnumDemonWillType type) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IDemonWillConduit) {
				return ((IDemonWillConduit) part).canDrain(type);
			}
		}

		return false;
	}

	@Override
	public double getCurrentWill(EnumDemonWillType type) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof IDemonWillConduit) {
				return ((IDemonWillConduit) part).getCurrentWill(type);
			}
		}

		return 0;
	}
}
