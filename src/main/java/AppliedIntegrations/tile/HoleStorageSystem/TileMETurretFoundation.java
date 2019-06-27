package AppliedIntegrations.tile.HoleStorageSystem;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketVectorSync;
import AppliedIntegrations.tile.AITile;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.Platform;
import appeng.util.item.ItemList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.of;
import static net.minecraft.util.EnumFacing.DOWN;

/**
 * @Author Azazell
 */
public class TileMETurretFoundation extends AITile implements ICellContainer {
	private enum Ammo {
		MatterBall(1),
		PaintBall(1),
		Singularity(25);

		int cooldown;

		Ammo(int cooldown) {
			this.cooldown = cooldown;
		}
	}

	public TileMETurretFoundation(){
		super();

		this.getProxy().setValidSides(of(DOWN));
	}

	// Direction for rendering turret tower
	public BlockPos renderingDirection = new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

	private ItemList storedAmmo = new ItemList();

	public boolean activate(EnumHand hand, EntityPlayer p) {
		// Only call when player clicking with right hand
		if (hand == EnumHand.MAIN_HAND) {
			// Call only on server
			if (Platform.isServer()) {
				// Update only on server
				this.renderingDirection = p.getPosition();

				// Notify client
				NetworkHandler.sendToAllInRange(new PacketVectorSync(this.renderingDirection, this.getPos()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));

				// True result
				return true;
			}
		}

		return false;
	}

	@Override
	public void blinkCell(int i) {

	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {

		if (!getGridNode().isActive()) {
			return new ArrayList<>();
		}

		if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
			return singletonList(new IMEInventoryHandler<IAEItemStack>() {

				@Override
				public IAEItemStack injectItems(IAEItemStack input, Actionable actionable, IActionSource iActionSource) {
					// Check not null
					if (input == null) {
						return null;
					}

					// Check stack size
					if (input.getStackSize() == 0) {
						return null;
					}

					// Check can accept
					if (!canAccept(input)) {
						return input;
					}

					// Modulate inject
					if (actionable == Actionable.MODULATE) {
						// Add stack
						storedAmmo.add(input);
					}

					return null;
				}

				@Override
				public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {

					return null;
				}

				@Override
				public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {

					return null;
				}

				@Override
				public IStorageChannel<IAEItemStack> getChannel() {

					return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
				}

				@Override
				public AccessRestriction getAccess() {

					return AccessRestriction.READ_WRITE;
				}

				@Override
				public boolean isPrioritized(IAEItemStack iaeItemStack) {

					return false;
				}

				@Override
				public boolean canAccept(IAEItemStack iaeItemStack) {

					// Get item stack's item
					Item item = iaeItemStack.getItem();

					// Get optional items
					Optional<Item> optionalMatterBall = AEApi.instance().definitions().materials().matterBall().maybeItem();
					Optional<Item> optionalBlackHoleBall = AEApi.instance().definitions().materials().matterBall().maybeItem();

					return (optionalBlackHoleBall.isPresent() && item == optionalBlackHoleBall.get()) ||

							(optionalMatterBall.isPresent()) && item == optionalMatterBall.get();
				}

				@Override
				public int getPriority() {
					// TODO: 2019-03-25 Add priority
					return 0;
				}

				@Override
				public int getSlot() {
					// Ignored
					return 0;
				}

				@Override
				public boolean validForPass(int i) {
					// Ignored
					return true;
				}
			});
		}

		return new ArrayList<>();
	}

	@Override
	public int getPriority() {

		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {

	}
}
