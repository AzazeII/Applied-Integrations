package AppliedIntegrations.Parts.P2P;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.util.AECableType;
import net.minecraft.tileentity.TileEntity;
import teamroots.embers.power.IEmberCapability;

import java.util.Vector;

public abstract class AIP2PTunnel<T> extends AIPart {
    private boolean isOutput;

    private Vector<T> outputs = new Vector<>();

    // Tile operated by this tunnel
    private TileEntity operand;

    public AIP2PTunnel(PartEnum associatedPart, SecurityPermissions... interactionPermissions) {
        super(associatedPart, interactionPermissions);
    }

    @Override
    public void getBoxes(IPartCollisionHelper helper) {

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

    public TileEntity getOperatedTile(Class<?> tileClass){
        if(tileClass.isInstance(getFacingTile())){
            return getFacingTile();
        }
        return null;
    }
}
