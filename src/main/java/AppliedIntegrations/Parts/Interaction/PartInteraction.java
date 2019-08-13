package AppliedIntegrations.Parts.Interaction;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.Part.Interaction.Buttons.ClickMode;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Inventory.Manager.UpgradeInventoryManager;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.api.IEnumHost;
import AppliedIntegrations.api.IInventoryHost;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.RedstoneMode;
import appeng.api.config.YesNo;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.helpers.NonNullArrayIterator;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import com.google.common.collect.ImmutableSet;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

import static appeng.api.networking.ticking.TickRateModulation.SAME;
import static net.minecraft.entity.player.EntityPlayer.REACH_DISTANCE;
import static net.minecraft.util.EnumHand.MAIN_HAND;

/**
 * @Author Azazell
 */
public class PartInteraction extends AIPart implements IGridTickable, IInventoryHost, IEnumHost, ICraftingRequester {
	public enum EnumInteractionPlaneTabs {
		PLANE_FAKE_PLAYER_FILTER,
		PLANE_FAKE_PLAYER_INVENTORY
	}

	private final static int MAX_FILTER_SIZE = 9;

	private static final int FAKE_PLAYER_INVENTORY_SIZE = 36;
	private static final int FAKE_PLAYER_ARMOR_INVENTORY_SIZE = 4;

	private static final String KEY_FILTER_INVENTORY = "#FILTER_INVENTORY_KEY";
	private static final String KEY_MAIN_INVENTORY = "#MAIN_INVENTORY_KEY";
	private static final String KEY_ARMOR_INVENTORY = "#ARMOR_INVENTORY_KEY";
	private static final String KEY_OFFHAND_INVENTORY = "#OFFHAND_INVENTORY_KEY";
	private static final String KEY_UPGRADE_INVENTORY = "#UPGRADE_INVENTORY_KEY";
	private static final String KEY_SNEAKING = "#SNEAKING";

	public FakePlayer fakePlayer;
	public AIGridNodeInventory filterInventory = new AIGridNodeInventory("Interaction Bus Filter", MAX_FILTER_SIZE, 1);

	public AIGridNodeInventory mainInventory =
			new AIGridNodeInventory("Interaction Bus Inventory", FAKE_PLAYER_INVENTORY_SIZE, 64, this);

	public AIGridNodeInventory armorInventory =
			new AIGridNodeInventory("Interaction Bus Armor Inventory", FAKE_PLAYER_ARMOR_INVENTORY_SIZE, 64, this);

	public AIGridNodeInventory offhandInventory =
			new AIGridNodeInventory("Interaction Bus Offhand Inventory", 1, 64, this);
	public UpgradeInventoryManager upgradeInventoryManager =  new UpgradeInventoryManager(this, "Interaction Bus Upgrade Inventory", 4);

	private Future<ICraftingJob>[] jobs = new Future[4];
	private ICraftingLink[] links = new ICraftingLink[4];
	private HashMap<ICraftingLink, BiConsumer<FakePlayer, BlockPos>> jobMethodMap = new HashMap<>();
	private List<ItemStack> injectionQueue = new ArrayList<>();

	private UUID uniIdentifier;
	private boolean lastRedstone;
	private boolean sneaking;

	public PartInteraction() {
		super(PartEnum.InteractionPlane);
	}

	private void createFakePlayer() {
		if (fakePlayer != null) {
			return;
		}

		World hostWorld = getHostWorld();
		getProxy().getNode().updateState();

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
			fakePlayer.setSneaking(sneaking);
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

	private void startJob(int craftingIndex, IAEItemStack input, ICraftingGrid craftingGrid,
	                      MachineSource src) throws GridAccessException {
		jobs[craftingIndex] = craftingGrid.beginCraftingJob(getHostWorld(), getProxy().getGrid(), src, input, null);
	}

	private void submitJob(int craftingIndex, BiConsumer<FakePlayer, BlockPos> method, ICraftingGrid craftingGrid,
	                       Future<ICraftingJob> futureJob) throws InterruptedException, ExecutionException {
		ICraftingJob job = futureJob.isDone() ? futureJob.get() : null;

		if (job != null) {
			ICraftingLink link = craftingGrid.submitJob(job, this, null, false, new MachineSource(this));

			if (link == null) {
				return;
			}

			links[craftingIndex] = link;
			jobs[craftingIndex] = null;
			jobMethodMap.put(link, method);
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

	private void click(int craftingIndex, ItemStack stack, FakePlayer player, IGridNode node, BlockPos facingPos,
	                   BiConsumer<FakePlayer, BlockPos> method) {
		try {
			player.setHeldItem(MAIN_HAND, stack.copy());

			// Try to submit done crafting jobs
			ICraftingGrid craftingGrid = getProxy().getCrafting();
			Future<ICraftingJob> futureJob = jobs[craftingIndex];
			boolean hasJob = futureJob != null;

			if (hasJob) {
				submitJob(craftingIndex, method, craftingGrid, futureJob);
			}

			// Simulate operations
			IMEMonitor<IAEItemStack> inventory = getMEInventory();
			IAEItemStack input = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(player.getHeldItemMainhand());
			boolean beginJob = false;

			// Call auto crafting if craftOnly is set to yes and even if there is stored items in system
			if (!upgradeInventoryManager.autoCrafting || upgradeInventoryManager.craftOnly == YesNo.NO) {
				IAEItemStack extractedInput = inventory.extractItems(input, Actionable.SIMULATE, new MachineSource(this));

				if (extractedInput != null) {
					modulateClick(facingPos, method, inventory, extractedInput, true);
				} else if (upgradeInventoryManager.autoCrafting) {
					beginJob = true;
				}
			} else if (upgradeInventoryManager.craftOnly == YesNo.YES) {
				beginJob = true;
			}

			// Start new job if it isn't processing yet
			if (beginJob) {
				if (!hasJob) {
					startJob(craftingIndex, input, craftingGrid, new MachineSource(this));
				}
			}
		} catch(GridAccessException | ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void modulateClick(BlockPos facingPos, BiConsumer<FakePlayer, BlockPos> method,
	                           IMEMonitor<IAEItemStack> inventory, IAEItemStack extracted, boolean directInjection) throws GridAccessException {
		// Modulate operations
		inventory.extractItems(extracted, Actionable.MODULATE, new MachineSource(this));

		// Click using given method
		method.accept(fakePlayer, facingPos);

		// Directly inject click results or add it to injection list
		if (directInjection) {
			injectClickResult(fakePlayer.getHeldItemMainhand(), inventory);
		} else {
			// We need to add injection results to queue if #modulateClick is called from #injectClickResults.
			// If injection is called directly to #injectClickResult then simulation WILL NEVER return null value(so, it will never simulate
			// injection into ME system)
			addClickResultsToQueue(fakePlayer);
		}
	}

	@Nonnull
	private List<ItemStack> getFuzzyComparedItemList(ItemStack[] slots) throws GridAccessException {
		// Get stack list from ae inventory, add each fuzzy compared(to original item stack) item stack to return list
		IMEMonitor<IAEItemStack> inv = this.getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel( IItemStorageChannel.class ) );

		List<ItemStack> ret = new ArrayList<>();
		for (ItemStack stack : slots) {
			for (IAEItemStack fuzzyStack : inv.getStorageList().findFuzzy(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(stack), upgradeInventoryManager.fuzzyMode)) {
				ret.add(fuzzyStack.getDefinition());
			}
		}

		return ret;
	}

	private void addClickResultsToQueue(FakePlayer player) {
		ItemStack stack = player.getHeldItem(MAIN_HAND);

		if (stack.isEmpty()) {
			return;
		}

		injectionQueue.add(stack);
	}

	private void injectClickResult(ItemStack stack, IMEMonitor<IAEItemStack> inventory) throws GridAccessException {
		IAEItemStack aeStack = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(stack);

		if (aeStack == null || aeStack.getDefinition().isEmpty()) {
			return;
		}

		// Modulate injection of item stack after click operations
		IAEItemStack notInjected = Platform.poweredInsert(getProxy().getEnergy(), inventory, aeStack, new MachineSource(this));

		// Drop not injected item stack
		if (notInjected != null) {
			getHostWorld().spawnEntity(new EntityItem(getHostWorld(),
					0.5 + getHostPos().getX(),
					0.5 + getHostPos().getY(),
					0.2 + getHostPos().getZ(),
					notInjected.getDefinition().copy()));
		}
	}

	private void processTick(@Nonnull IGridNode node) throws GridAccessException {
		BlockPos facingPos = getHostPos().offset(getHostSide().getFacing());

		// Nullify player's main hand for this run
		fakePlayer.setHeldItem(MAIN_HAND, ItemStack.EMPTY);

		// Get fuzzy compared list of items from ME inventory
		List<ItemStack> list = upgradeInventoryManager.fuzzyCompare ? getFuzzyComparedItemList(filterInventory.slots) : Arrays.asList(
				filterInventory.slots);

		// Try to extract filtered item(s) from ME inventory and use it on operated tile and then inject output items(if any)
		for (ItemStack stack : list) {
			// Don't operate with empty stack
			if (stack.isEmpty()) {
				continue;
			}

			// Do click on entity(ies), item(s) and block(s)
			click(0, stack, fakePlayer, node, facingPos, this::onItemRightClick);
			click(1, stack, fakePlayer, node, facingPos, this::interactEntity);
			click(2, stack, fakePlayer, node, facingPos, this::interactBlock);
			click(3, stack, fakePlayer, node, facingPos, this::onItemUse);
		}
	}

	@Override
	public void getDrops(List<ItemStack> drops, boolean wrenched) {
		// Drop items from all inventories
		drops.addAll(Arrays.asList(mainInventory.slots));
		drops.addAll(Arrays.asList(armorInventory.slots));
		drops.addAll(Arrays.asList(offhandInventory.slots));
		drops.addAll(Arrays.asList(upgradeInventoryManager.upgradeInventory.slots));
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
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos posChanged) {
		IGridNode node = getProxy().getNode();

		try {
			// Process tick on redstone pulse
			if (this.upgradeInventoryManager.redstoneMode == RedstoneMode.SIGNAL_PULSE) {
				if (this.isReceivingRedstonePower() != this.lastRedstone) {
					this.lastRedstone = this.isReceivingRedstonePower();

					if (node == null || fakePlayer == null || !lastRedstone) {
						return;
					}

					this.processTick(node);
				}
			}
		} catch(GridAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		// Read inventories from NBT
		this.filterInventory.readFromNBT(data.getTagList(KEY_FILTER_INVENTORY, 10));
		this.upgradeInventoryManager.upgradeInventory.readFromNBT(data.getTagList(KEY_UPGRADE_INVENTORY, 10));

		this.mainInventory.readFromNBT(data.getTagList(KEY_MAIN_INVENTORY, 10));
		this.armorInventory.readFromNBT(data.getTagList(KEY_ARMOR_INVENTORY, 10));
		this.offhandInventory.readFromNBT(data.getTagList(KEY_OFFHAND_INVENTORY, 10));
		this.sneaking = data.getBoolean(KEY_SNEAKING);

		// Pass call to UIM
		this.upgradeInventoryManager.readFromNBT(data);
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
		data.setBoolean(KEY_SNEAKING, this.fakePlayer.isSneaking());

		// Pass call to UIM
		this.upgradeInventoryManager.writeToNBT(data);
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
		this.createFakePlayer();

		try {
			// Inject all items from queue
			for (ItemStack stackToInject : injectionQueue) {
				 injectClickResult(stackToInject, getMEInventory());
			}

			injectionQueue.clear();

			IItemList<IAEItemStack> list = getMEInventory().getStorageList();

			// Process ticking request
			if (this.canDoWork(upgradeInventoryManager.redstoneMode)) {
				processTick(node);
			}
		} catch(GridAccessException e) {
			e.printStackTrace();
		}

		return SAME;
	}

	private IMEMonitor<IAEItemStack> getMEInventory() throws GridAccessException {
		return getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel(
				IItemStorageChannel.class));
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(10, 10, false, false);
	}

	@Override
	public void setEnumVal(Enum val) {
		if (val instanceof ClickMode) {
			this.fakePlayer.setSneaking(val == ClickMode.SHIFT_CLICK);
		} else {
			this.upgradeInventoryManager.acceptVal(val);
		}
	}

	@Override
	public ImmutableSet<ICraftingLink> getRequestedJobs() {
		return ImmutableSet.copyOf(new NonNullArrayIterator<>(links));
	}

	@Override
	public void jobStateChange(ICraftingLink link) {
		// Find link and remove it
		for(int i = 0; i < links.length; i++) {
			if (links[i] == link) {
				links[i] = null;
			}
		}
	}

	@Override
	public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack items, Actionable mode) {
		try {
			// Modulate click with crafted items(honestly it is always only one item :D)
			BiConsumer<FakePlayer, BlockPos> method = jobMethodMap.get(link);

			if (method == null) {
				return null;
			}

			IMEMonitor<IAEItemStack> inventory = this.getProxy().getStorage().getInventory(
							AEApi.instance().storage().getStorageChannel( IItemStorageChannel.class ) );
			modulateClick(getHostPos().offset(getHostSide().getFacing()), method, inventory, items, false);
			jobMethodMap.remove(link);
		} catch(GridAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
