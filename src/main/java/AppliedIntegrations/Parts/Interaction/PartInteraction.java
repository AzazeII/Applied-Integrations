package AppliedIntegrations.Parts.Interaction;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Inventory.Manager.UpgradeInventoryManager;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.api.IInventoryHost;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static appeng.api.networking.ticking.TickRateModulation.SAME;
import static net.minecraft.entity.player.EntityPlayer.REACH_DISTANCE;
import static net.minecraft.util.EnumHand.MAIN_HAND;

/**
 * @Author Azazell
 */
public class PartInteraction extends AIPart implements IGridTickable, UpgradeInventoryManager.IUpgradeInventoryManagerHost, IInventoryHost{
	public enum EnumInteractionPlaneTabs {
		PLANE_FAKE_PLAYER_FILTER,
		PLANE_FAKE_PLAYER_INVENTORY
	}

	private FakePlayer fakePlayer;
	private static final double AE_DRAIN_PER_OPERATION = 0.5;
	private final static int MAX_FILTER_SIZE = 9;

	private static final int FAKE_PLAYER_INVENTORY_SIZE = 36;
	private static final int FAKE_PLAYER_ARMOR_INVENTORY_SIZE = 4;

	private static final String KEY_FILTER_INVENTORY = "#FILTER_INVENTORY_KEY";
	private static final String KEY_MAIN_INVENTORY = "#MAIN_INVENTORY_KEY";
	private static final String KEY_ARMOR_INVENTORY = "#ARMOR_INVENTORY_KEY";
	private static final String KEY_OFFHAND_INVENTORY = "#OFFHAND_INVENTORY_KEY";
	private static final String KEY_UPGRADE_INVENTORY = "#UPGRADE_INVENTORY_KEY";

	public AIGridNodeInventory filterInventory = new AIGridNodeInventory("Interaction Bus Filter", MAX_FILTER_SIZE, 1);

	public AIGridNodeInventory mainInventory =
			new AIGridNodeInventory("Interaction Bus Inventory", FAKE_PLAYER_INVENTORY_SIZE, 64, this);

	public AIGridNodeInventory armorInventory =
			new AIGridNodeInventory("Interaction Bus Armor Inventory", FAKE_PLAYER_ARMOR_INVENTORY_SIZE, 64, this);

	public AIGridNodeInventory offhandInventory =
			new AIGridNodeInventory("Interaction Bus Offhand Inventory", 1, 64, this);

	public UpgradeInventoryManager upgradeInventoryManager =  new UpgradeInventoryManager(this, "Interaction Bus Upgrade Inventory", 4);
	private UUID uniIdentifier;

	public PartInteraction() {
		super(PartEnum.InteractionPlane);
	}

	private void createFakePlayer() {
		World hostWorld = getHostWorld();

		if (hostWorld instanceof WorldServer) {
			// Generate UUID if no UUID exists already
			if (this.uniIdentifier == null) {
				this.uniIdentifier = UUID.randomUUID();

				IBlockState state = hostWorld.getBlockState(this.getHostPos());
				hostWorld.notifyBlockUpdate(getHostPos(), state, state, 3);
			}

			// Generate fake player
			GameProfile fakeProfile = new GameProfile(this.uniIdentifier, AppliedIntegrations.modid + "fake_player_interaction_bus");

			try {
				fakePlayer = FakePlayerFactory.get((WorldServer) hostWorld, fakeProfile);
			} catch(Exception e) {
				fakePlayer = null;
				return;
			}

			if (fakePlayer == null) {
				return;
			}

			// Configure fake player
			fakePlayer.onGround = true;
			fakePlayer.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(),
					new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer) {
				@SuppressWarnings("rawtypes")
				@Override
				public void sendPacket(@Nonnull Packet packetIn) {}
			};

			fakePlayer.setSilent(true);

			// Set player position
			fakePlayer.posX = getHostPos().getX();
			fakePlayer.posY = getHostPos().getY();
			fakePlayer.posZ = getHostPos().getZ();
			fakePlayer.getEntityAttribute(REACH_DISTANCE).setBaseValue(1);

			// Rotate player's head depending on host side
			// South is original zero value, so don't change it if host side equal to south
			fakePlayer.eyeHeight = 0;
			if (getHostSide() == AEPartLocation.WEST) {
				fakePlayer.rotationYaw = 90;
			} else if (getHostSide() == AEPartLocation.NORTH) {
				fakePlayer.rotationYaw = 180;
			} else if (getHostSide() == AEPartLocation.EAST) {
				fakePlayer.rotationYaw = 270;
			} else if (getHostSide() == AEPartLocation.DOWN) {
				fakePlayer.rotationPitch = 90;
			} else if (getHostSide() == AEPartLocation.UP) {
				fakePlayer.rotationPitch = -90;
			}

			// Sync our inventories with inventories of fake player
			onInventoryChanged();
		}
	}

	private void interactBlock(FakePlayer player, BlockPos facingPos) {
		ItemStack itemStack = player.getHeldItemMainhand();

		// Click on this block
		player.interactionManager.processRightClickBlock(player, getHostWorld(), itemStack,
				MAIN_HAND, facingPos, EnumFacing.UP, .5F, .5F, .5F);
	}

	private void interactEntity(FakePlayer player, BlockPos facingPos) {
		List<EntityLivingBase> ents = getHostWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(
				facingPos.getX() - 0.5, facingPos.getY() - 0.5, facingPos.getZ() - 0.5,
				facingPos.getX() + 0.5, facingPos.getY() + 0.5, facingPos.getZ() + 0.5));

		for (EntityLivingBase ent : ents) {
			player.interactOn(ent, MAIN_HAND);
		}
	}

	private void onItemUse(FakePlayer player, BlockPos facingPos) {
		// Trace result after clicking with item and replace current player stack with stack from result to make system inject return item right into it
		ActionResult<ItemStack> result = player.getHeldItemMainhand().getItem().onItemRightClick(getHostWorld(), player, MAIN_HAND);
		player.setHeldItem(MAIN_HAND, result.getResult());
	}

	private void onItemRightClick(FakePlayer player, BlockPos facingPos) {
		// Get ray tracing result and use it on onItemUse method
		RayTraceResult traceResult = getTraceResult(player);
		if(traceResult == null) {
			return;
		}

		player.getHeldItemMainhand().getItem().onItemUse(player, getHostWorld(), traceResult.getBlockPos(), MAIN_HAND, traceResult.sideHit,
				(float) traceResult.hitVec.x, (float) traceResult.hitVec.y, (float) traceResult.hitVec.z);
	}

	private RayTraceResult getTraceResult(FakePlayer player) {
		Vec3d position = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
		float pitch = player.rotationPitch;
		float yaw = player.rotationYaw;

		// Calculate trigonometric values from pitch and yaw
		float f4 = -MathHelper.cos(-pitch * 0.017453292F);
		float f5 = MathHelper.sin(-pitch * 0.017453292F);
		float f6 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI) * f4;
		float f7 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI) * f4;
		double range = player.getEntityAttribute(REACH_DISTANCE).getAttributeValue();

		// Calculate direction vector and ray trace blocks on this directions
		Vec3d direction = position.addVector((double)f6 * range, (double)f5 * range, (double)f7 * range);
		return getHostWorld().rayTraceBlocks(position, direction, true, false, false);
	}

	private void click(FakePlayer player, IGridNode node, BlockPos facingPos, BiConsumer<FakePlayer, BlockPos> method) {
		// Try to extract filtered item(s) from ME inventory and use it on operated tile and then inject output items(if any)
		for (ItemStack stack : filterInventory.slots) {
			// Don't operate with empty stack unless there is inverter card
			if (stack.isEmpty()) {
				continue;
			}

			player.setHeldItem(MAIN_HAND, stack.copy());

			// Simulate operations
			IStorageGrid cache = node.getGrid().getCache(IStorageGrid.class);
			IMEMonitor<IAEItemStack> inventory = cache.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
			AEItemStack input = AEItemStack.fromItemStack(player.getHeldItemMainhand());
			IAEItemStack extracted = inventory.extractItems(input, Actionable.SIMULATE, new MachineSource(this));

			try {
				double extractedAEPower = getProxy().getEnergy().extractAEPower(AE_DRAIN_PER_OPERATION, Actionable.SIMULATE, PowerMultiplier.CONFIG);
				if (extracted != null && extractedAEPower != 0) {
					// Modulate operations
					getProxy().getEnergy().extractAEPower(extractedAEPower, Actionable.MODULATE, PowerMultiplier.CONFIG);
					inventory.extractItems(extracted, Actionable.MODULATE, new MachineSource(this));

					// Click using given method
					method.accept(player, facingPos);
					injectClickResult(player, inventory);
				}
			} catch(GridAccessException ignored) {}
		}
	}

	private void injectClickResult(FakePlayer player, IMEMonitor<IAEItemStack> inventory) {
		IAEItemStack aeStack = AEItemStack.fromItemStack(player.getHeldItem(MAIN_HAND));

		if (aeStack == null || aeStack.getDefinition().isEmpty()) {
			return;
		}

		// Modulate item injection into ME system
		IAEItemStack notInjected = inventory.injectItems(aeStack, Actionable.MODULATE, new MachineSource(this));

		// Drop not injected item stack
		if (notInjected != null) {
			getHostWorld().spawnEntity(new EntityItem(getHostWorld(),
					0.5 + getHostPos().getX(),
					0.5 + getHostPos().getY(),
					0.2 + getHostPos().getZ(),
					notInjected.getDefinition().copy()));
		}
	}

	@Override
	public void getDrops(List<ItemStack> drops, boolean wrenched) {
		// Drop items from all inventories
		drops.addAll(Arrays.asList(mainInventory.slots));
		drops.addAll(Arrays.asList(armorInventory.slots));
		drops.addAll(Arrays.asList(offhandInventory.slots));
	}

	@Override
	public void onInventoryChanged() {
		// Ignored on client
		if (getHostWorld().isRemote) {
			return;
		}

		// Sync all inventories with inventory of fake player. Don't sync hand slot in main inventory
		InventoryPlayer inventoryPlayer = fakePlayer.inventory;
		for (int idx = 1; idx < mainInventory.getSizeInventory(); idx++) {
			inventoryPlayer.mainInventory.set(idx, mainInventory.getStackInSlot(idx));
		}
		for (int idx = 0; idx < armorInventory.getSizeInventory(); idx++) {
			inventoryPlayer.armorInventory.set(idx, armorInventory.getStackInSlot(idx));
		}
		for (int idx = 0; idx < offhandInventory.getSizeInventory(); idx++) {
			inventoryPlayer.offHandInventory.set(idx, offhandInventory.getStackInSlot(idx));
		}
	}

	@Override
	public void doSync(int filterSize, boolean redstoneControlled, int upgradeSpeedCount) {}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		// Read inventories from NBT
		this.filterInventory.readFromNBT(data.getTagList(KEY_FILTER_INVENTORY, 10));
		this.upgradeInventoryManager.upgradeInventory.readFromNBT(data.getTagList(KEY_UPGRADE_INVENTORY, 10));

		this.mainInventory.readFromNBT(data.getTagList(KEY_MAIN_INVENTORY, 10));
		this.armorInventory.readFromNBT(data.getTagList(KEY_ARMOR_INVENTORY, 10));
		this.offhandInventory.readFromNBT(data.getTagList(KEY_OFFHAND_INVENTORY, 10));
	}

	@Override
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
		super.writeToNBT(data, saveType);

		// Write inventories to NBT
		data.setTag(KEY_FILTER_INVENTORY, this.filterInventory.writeToNBT());
		data.setTag(KEY_UPGRADE_INVENTORY, this.upgradeInventoryManager.upgradeInventory.writeToNBT());

		data.setTag(KEY_MAIN_INVENTORY, this.mainInventory.writeToNBT());
		data.setTag(KEY_ARMOR_INVENTORY, this.armorInventory.writeToNBT());
		data.setTag(KEY_OFFHAND_INVENTORY, this.offhandInventory.writeToNBT());
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Activation logic is server sided
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInteraction, player, getHostSide(), getHostTile().getPos());
			}
		}
		return true;
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
		return upgradeInventoryManager.upgradeInventory;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		// Interface-like boxes
		bch.addBox(2.0D, 2.0D, 15.0D, 14.0D, 14.0D, 16.0D);
		bch.addBox(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 15.0D);
		bch.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 14.0D);
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

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		if (fakePlayer == null) {
			this.createFakePlayer();
			return TickRateModulation.IDLE;
		}

		BlockPos facingPos = getHostPos().offset(getHostSide().getFacing());

		// Nullify player's main hand for this run
		fakePlayer.setHeldItem(MAIN_HAND, ItemStack.EMPTY);

		// Do click on entity(ies), item(s) and block(s)
		click(fakePlayer, node, facingPos, this::onItemRightClick);
		click(fakePlayer, node, facingPos, this::interactEntity);
		click(fakePlayer, node, facingPos, this::interactBlock);
		click(fakePlayer, node, facingPos, this::onItemUse);

		return SAME;
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(1, 10, false, false);
	}
}
