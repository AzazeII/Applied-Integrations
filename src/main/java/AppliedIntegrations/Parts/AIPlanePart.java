package AppliedIntegrations.Parts;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.tile.HoleStorageSystem.TimeHandler;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class AIPlanePart extends AIPart implements IGridTickable {

	protected static final int ENERGY_TRANSFER = 800;

	protected List<Entity> currentEntities = new ArrayList<>();

	private TimeHandler lightningHandler = new TimeHandler();

	public AIPlanePart(PartEnum associatedPart) {
		super(associatedPart);
	}

	@Override
	public final void onEntityCollision(Entity entity) {}

	@Override
	public float getCableConnectionLength(AECableType cable) {
		return 2.0F;
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(5, 5, 14, 11, 11, 15);
		bch.addBox(1, 1, 15, 15, 15, 16);
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode iGridNode) {
		return new TickingRequest(1, 1, false, false);
	}

	@Nonnull
	@Override
	public final TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int i) {
		World world = this.hostTile.getWorld();
		AEPartLocation hostSide = getHostSide();
		BlockPos offset = this.hostTile.getPos().offset(hostSide.getFacing());
		int x = offset.getX();
		int y = offset.getY();
		int z = offset.getZ();

		AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 0.5, y + 0.5, z + 0.5);

		currentEntities = world.getEntitiesWithinAABB(Entity.class, bb);
		if (!currentEntities.isEmpty()) {
			IGridNode node = getGridNode(AEPartLocation.INTERNAL);
			if (node != null) {
				IGrid grid = node.getGrid();
				grid.postEvent(new MENetworkCellArrayUpdate());
			}
		}

		doWork(i);

		return TickRateModulation.SAME;
	}

	protected abstract void doWork(int ticksSinceLastCall);

	protected void spawnLightning(Entity workingEntity) {
		if (lightningHandler.hasTimePassed(getHostTile().getWorld(), 1)) {
			AppEng.proxy.spawnEffect(EffectType.Lightning, hostTile.getWorld(), workingEntity.posX, workingEntity.posY, workingEntity.posZ, null);
		}
	}
}
