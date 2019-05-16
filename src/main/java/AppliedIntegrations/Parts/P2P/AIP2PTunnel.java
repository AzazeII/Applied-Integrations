package AppliedIntegrations.Parts.P2P;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.util.AECableType;
import net.minecraft.tileentity.TileEntity;

import java.util.Vector;

/**
 * @Author Azazell
 */
public abstract class AIP2PTunnel<T> extends AIPart {
	private boolean isOutput;

	private Vector<T> outputs = new Vector<>();

	// tile operated by this tunnel
	private TileEntity operand;

	public AIP2PTunnel(PartEnum associatedPart, SecurityPermissions... interactionPermissions) {
		super(associatedPart);
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(5, 5, 12, 11, 11, 13);
		bch.addBox(3, 3, 13, 13, 13, 14);
		bch.addBox(2, 2, 14, 14, 14, 16);
	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 1;
	}

	protected boolean isOutput() {
		return this.isOutput;
	}

	public Vector<T> getOutputs() {
		return outputs;
	}

	public TileEntity getOperatedTile(Class<?> tileClass) {
		if (tileClass.isInstance(getFacingTile())) {
			return getFacingTile();
		}
		return null;
	}
}
