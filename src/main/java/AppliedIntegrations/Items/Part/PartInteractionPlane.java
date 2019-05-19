package AppliedIntegrations.Items.Part;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.util.AECableType;
import net.minecraft.entity.Entity;

/**
 * @Author Azazell
 */
public class PartInteractionPlane extends AIPart {
	public PartInteractionPlane(PartEnum associatedPart) {
		super(associatedPart);
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes(IPartCollisionHelper helper) {

	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType cable) {
		return 0;
	}
}
