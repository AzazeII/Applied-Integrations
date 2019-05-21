package AppliedIntegrations.Parts.Interaction;


import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.AEApi;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.util.AECableType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @Author Azazell
 */
public class PartInteractionPlane extends AIPart implements ICellContainer {
	private InteractionPlaneHandler handler = new InteractionPlaneHandler(this);

	public PartInteractionPlane() {
		super(PartEnum.InteractionPlane);
	}

	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos pos, BlockPos changedPos) {
		super.onNeighborChanged(iBlockAccess, pos, changedPos);

		// Update position in handler
		handler.setPos(changedPos);
	}


	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.INTERACTION_HAS_CHANNEL;
			} else {
				return PartModelEnum.INTERACTION_ON;
			}
		}
		return PartModelEnum.INTERACTION_OFF;
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		// Interface-like boxes
		bch.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
		bch.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 14.0D);

		// Additional boxes
		bch.addBox(2.0D, 8.0D, 14.0D, 14.0D, 8.0D, 16.0D);
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
		return 2F;
	}

	@Override
	public void blinkCell(int slot) {
		// Ignored
	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		// Check if channel isn't item storage channel
		if (channel != AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class))
			// Empty list
			return new ArrayList<>();

		return singletonList(this.handler);
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
		// Check if inventory not null
		if (iCellInventory != null) {
			// Persist inventory
			iCellInventory.persist();
		}
	}
}
