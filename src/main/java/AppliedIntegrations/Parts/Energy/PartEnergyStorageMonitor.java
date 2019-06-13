package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.AEApi;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.parts.IPartStorageMonitor;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.client.render.TesrRenderHelper;
import appeng.core.localization.PlayerMessages;
import appeng.me.GridAccessException;
import appeng.util.IWideReadableNumberConverter;
import appeng.util.Platform;
import appeng.util.ReadableNumberConverter;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

import static appeng.parts.reporting.PartStorageMonitor.*;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * @Author Azazell
 */

// TODO Rewrite EVERYTHING here!
/*
    1. onActivate mechanics
    2. Cell container mechanics
    3. Actually showing energy
    4. Packets
*/
public class PartEnergyStorageMonitor extends AIRotatablePart implements IStackWatcherHost, IPowerChannelState, IPartStorageMonitor {

	private static final String KEY_IS_LOCKED = "#IS_LOCKED";
	private static final String KEY_STACK_TAG = "#STACK_TAG";
	private static final IWideReadableNumberConverter NUMBER_CONVERTER = ReadableNumberConverter.INSTANCE;

	private String lastHumanReadableText;
	private IAEEnergyStack currentStack;
	private IStackWatcher watcher;
	private boolean isLocked;

	public PartEnergyStorageMonitor() {
		super(PartEnum.EnergyStorageMonitor);
	}

	private void onStackWatchUpdate() throws GridAccessException {
		// Check not null
		if (watcher == null)
			return;

		// Reset watcher
		watcher.reset();

		// Check not null
		if (currentStack == null)
			return;

		// Add stack to watches
		watcher.add(currentStack);

		// Update currently watched stack
		queryStackUpdate();
	}

	private void queryStackUpdate() throws GridAccessException {
		// Check not null
		if( currentStack == null ) {
			return;
		}

		// Find stack from grid inventory
		IAEEnergyStack output = getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class))
				.getStorageList().findPrecise( currentStack );

		// Replace stack size with stack size of stack from inventory
		currentStack.setStackSize( output == null ? 0 : output.getStackSize() );
	}

	@Override // Server side
	public void writeToStream( final ByteBuf data ) throws IOException {
		super.writeToStream( data );

		// Write is locked value
		data.writeBoolean(isLocked);

		// Write has stack
		data.writeBoolean(currentStack != null);

		// Check not null
		if (currentStack == null)
			return;

		// Write stack
		currentStack.writeToPacket(data);
	}

	@Override // Client side
	public boolean readFromStream( final ByteBuf data ) throws IOException {
		// Read is locked
		boolean isLocked = data.readBoolean();

		// Track change in current and written is locked data
		boolean redraw = isLocked != this.isLocked;

		// Check if stack exists
		if (data.readBoolean()) {
			// Read stack
			this.currentStack = AEEnergyStack.fromPacket(data);
		} else {
			// Nullify stack
			this.currentStack = null;
		}

		return super.readFromStream(data) || redraw;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void renderDynamic( double x, double y, double z, float partialTicks, int destroyStage ) {
		// Get currently displayed stack
		IAEEnergyStack stack = this.getDisplayed();

		// Check not null
		if( stack == null ) {
			return;
		}

		// Isolate changes
		GlStateManager.pushMatrix();

		// Translate pointer
		GlStateManager.translate( x + 0.5, y + 0.5, z + 0.5 );

		// Move to current facing
		TesrRenderHelper.moveToFace( getHostSide().getFacing() );

		// Get tesselator
		Tessellator tessellator = Tessellator.getInstance();

		// Get buffered builder
		BufferBuilder builder = tessellator.getBuffer();

		// Begin drawing quads
		builder.begin(GL_QUADS, POSITION_TEX);

		// Energy quad
		builder.pos(-0.2, -0.2, 0).endVertex(); // 1.
		builder.pos(-0.2, 0.2, 0).endVertex(); // 2.
		builder.pos(0.2, -0.2, 0).endVertex(); // 3.
		builder.pos(0.2, 0.2, 0).endVertex(); // 4.

		// End drawing
		tessellator.draw();

		// Isolate changes
		GlStateManager.popMatrix();
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() { return null; }

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(2, 2, 14, 14, 14, 16);
		bch.addBox(4, 4, 13, 12, 12, 14);
		bch.addBox(5, 5, 12, 11, 11, 13);
	}

	@Override
	public IPartModel getStaticModels() {
		// First condition -> Is locked---> Locked model
		//                             |--> Unlocked Model
		if( this.isActive() ) { // 1. Is active
			if( this.isLocked() ) { // Is locked
				return MODELS_LOCKED_HAS_CHANNEL; // Locked model
			} else {
				return MODELS_HAS_CHANNEL; // Unlocked model
			}
		} else if( this.isPowered() ) { // 2. Is powered
			if( this.isLocked() ) { // Is locked
				return MODELS_LOCKED_ON; // Locked model
			} else {
				return MODELS_ON; // Unlocked model
			}
		} else { // 3. Else
			if( this.isLocked() ) { // Is locked
				return MODELS_LOCKED_OFF; // Locked model
			} else {
				return MODELS_OFF; // Unlocked model
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		// Create stack tag
		NBTTagCompound stackTag = new NBTTagCompound();

		// Check not null
		if (currentStack != null) {
			// Write stack to sub-nbt tag
			currentStack.writeToNBT(stackTag);
		}

		// Write data to nbt
		data.setTag(KEY_STACK_TAG, stackTag);
		data.setBoolean(KEY_IS_LOCKED, isLocked); // 2. Is locked
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		// Read data from nbt
		currentStack = AEEnergyStack.fromNBT(data.getCompoundTag(KEY_STACK_TAG)); // 1. Sub stack tag & Stack
		isLocked = data.getBoolean(KEY_IS_LOCKED); // 2. Is locked
	}


	@Override
	public int getLightLevel() {
		return this.isActive() ? 0 : 1;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 2;
	}

	@Override
	public void updateWatcher(IStackWatcher w) {
		// Replace existing watcher
		this.watcher = w;

		try {
			// Call update
			onStackWatchUpdate();
		} catch (GridAccessException ignored) { }
	}

	@Override
	public boolean onShiftActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Ignored on client && Ignored if player has held item
		if( getHostWorld().isRemote ||  !player.getHeldItem( enumHand ).isEmpty() ) {
			return true;
		}

		// Ignored while inactive && Ignored if player has no permissions
		if( !this.getProxy().isActive() || !Platform.hasPermissions( this.getLocation(), player )) {
			return false;
		}

		// Toggle lock
		this.isLocked = !this.isLocked;

		// Notify player
		player.sendMessage( ( this.isLocked ? PlayerMessages.isNowLocked : PlayerMessages.isNowUnlocked ).get() );

		// Mark for save & update
		this.getHost().markForSave(); // 1. Save
		this.getHost().markForUpdate(); // 2. Update
		return true;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d position) {
		// Ignore all mechanics on client
		if( getHostWorld().isRemote ) {
			return true;
		}

		// Ignore mechanics if part isn't active
		if( !this.getProxy().isActive() ) {
			return false;
		}

		// Get permissions from player, if player has no permissions, then:
		// DENIED - https://www.deviantart.com/the-masked-max/art/Arstotzka-Emblem-Papers-Please-ASCII-Art-736130975
		if( !Platform.hasPermissions( this.getLocation(), player ) ) {
			return false;
		}

		// Check if part isn't locked
		if( !this.isLocked ) {
			// Get held stack
			ItemStack maybeEnergyStack = player.getHeldItem( enumHand );

			// Convert to energy
			LiquidAIEnergy energy = Utils.getEnergyFromItemStack(maybeEnergyStack);

			// Check if held stack represents any energy
			if (energy == null) {
				return false;
			}

			// Update stack with new energy stack
			currentStack = AEEnergyStack.fromStack(new EnergyStack(energy, 1));

			try {
				// Configure stack watcher
				onStackWatchUpdate();
			} catch (GridAccessException ignored) {}

			// Mark host for save & update
			this.getHost().markForSave(); // 1. Save
			this.getHost().markForUpdate(); // 2. Update
		} else {
			return super.onActivate( player, enumHand, position );
		}

		return true;
	}

	@Override
	public void onStackChange( final IItemList list, final IAEStack oldStack, final IAEStack diffStack, final IActionSource src, final IStorageChannel chan ) {
		// Check not null
		if (currentStack == null)
			return;

		// Replace stack size with size of old stack
		currentStack.setStackSize( oldStack == null ? 0 : oldStack.getStackSize() );

		// Get current stack size and convert it to readable form
		String humanReadableText = NUMBER_CONVERTER.toWideReadableForm( currentStack.getStackSize() );

		// Check if new human readable text isn't equal to last
		if( !humanReadableText.equals( lastHumanReadableText ) ) {
			// Update last text
			lastHumanReadableText = humanReadableText;

			// Mark for update to render new text
			getHost().markForUpdate();
		}
	}

	@Override
	public IAEEnergyStack getDisplayed() {
		return currentStack;
	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}

	@Override
	public boolean showNetworkInfo(RayTraceResult where) {
		return false;
	}
}