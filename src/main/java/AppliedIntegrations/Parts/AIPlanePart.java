package AppliedIntegrations.Parts;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.tile.HoleStorageSystem.TimeHandler;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.client.EffectType;
import appeng.core.AppEng;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AIPlanePart extends AIPart implements IGridTickable {

    protected static final int ENERGY_TRANSFER = 800;
    private TimeHandler lightningHandler = new TimeHandler();

    protected List<Entity> currentEntities = new ArrayList<>();

    public AIPlanePart(PartEnum associatedPart, SecurityPermissions... interactionPermissions) {
        super(associatedPart, interactionPermissions);
    }

    protected abstract void doWork(int ticksSinceLastCall);

    @Override
    public final void onEntityCollision(Entity entity) { }

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
        bch.addBox( 5, 5, 14, 11, 11, 15 );
        bch.addBox( 1, 1, 15, 15, 15, 16 );
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
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 1, false, false);
    }

    @Nonnull
    @Override
    public final TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int i) {
        // Get the world
        World world = this.hostTile.getWorld();

        // Get our location
        int x = this.hostTile.getPos().getX();
        int y = this.hostTile.getPos().getY();
        int z = this.hostTile.getPos().getZ();

        // Get box
        AxisAlignedBB bb = new AxisAlignedBB(x-1, y-1, z-1, x+1, y+1, z+1);

        // Get current entities
        currentEntities = world.getEntitiesWithinAABB(Entity.class, bb);

        // Check if entity list not empty
        if(!currentEntities.isEmpty()){
            // Get node
            IGridNode node = getGridNode(AEPartLocation.INTERNAL);
            // Check notNull
            if (node != null) {
                // Get grid
                IGrid grid = node.getGrid();
                // Check not null
                if(grid != null) {
                    // Post update
                    grid.postEvent(new MENetworkCellArrayUpdate());
                }
            }
        }

        // Pass func to child classes
        doWork(i);

        return TickRateModulation.SAME;
    }

    protected void spawnLightning(Entity workingEntity) {
        // If time passed
        if(lightningHandler.hasTimePassed(getHostTile().getWorld(), 1))
            // Spawn effect
            AppEng.proxy.spawnEffect(EffectType.Lightning, hostTile.getWorld(), workingEntity.posX,
                    workingEntity.posY, workingEntity.posZ, null);
    }
}
