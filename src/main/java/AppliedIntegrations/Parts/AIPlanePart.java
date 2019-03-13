package AppliedIntegrations.Parts;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.util.AECableType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class AIPlanePart extends AIPart implements IGridTickable {

    public Entity workingEntity;

    public AIPlanePart(PartEnum associatedPart, SecurityPermissions... interactionPermissions) {
        super(associatedPart, interactionPermissions);
    }

    @Override
    protected AIGridNodeInventory getUpgradeInventory() {
        return null;
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 2.0F;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 13, 11, 11, 14);
    }

    @Override
    public double getIdlePowerUsage() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public void onEntityCollision(Entity entity) {
        if(entity.getPosition().equals(new BlockPos(getX() + getSide().xOffset, getY() + getSide().yOffset, getZ() + getSide().zOffset))){
            workingEntity = entity;
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 1, false, false);
    }

    @Nonnull
    @Override
    public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int i) {
        if(workingEntity != null) {
            // Check if entity still near the bus
            if (workingEntity.getPosition().equals(new BlockPos(getX() + getSide().xOffset, getY() + getSide().yOffset, getZ() + getSide().zOffset))) {
                workingEntity = null;
            }
        }
        return TickRateModulation.SAME;
    }


}
