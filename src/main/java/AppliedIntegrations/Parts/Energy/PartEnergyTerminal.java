package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketTerminalUpdate;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.config.*;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

import static appeng.api.networking.ticking.TickRateModulation.SAME;

/**
 * @Author Azazell
 */
public class PartEnergyTerminal extends AIRotatablePart implements ITerminalHost, IConfigManagerHost, IGridTickable, IMEMonitorHandlerReceiver<IAEEnergyStack> {

	private IConfigManager configManager = new ConfigManager(this);
	public List<ContainerEnergyTerminal> listeners = new ArrayList<>();

	public PartEnergyTerminal() {
		super(PartEnum.EnergyTerminal, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT, SecurityPermissions.CRAFT);

		// Register setting for terminal
		// Sort mode (default: name)
		configManager.registerSetting( Settings.SORT_BY, SortOrder.NAME );

		// View mode (default: all)
		configManager.registerSetting( Settings.VIEW_MODE, ViewItems.ALL );

		// Sort direction (default: ascending)
		configManager.registerSetting( Settings.SORT_DIRECTION, SortDir.ASCENDING );
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d position) {
		// Check if terminal is active
		if(isActive()) {
			// Open gui
			AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiTerminalPart, player, getSide(), getPos());

			return true;
		}

		return false;
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes( final IPartCollisionHelper helper ) {
		helper.addBox( 2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D );
		helper.addBox( 4.0D, 4.0D, 13.0D, 12.0D, 12.0D, 14.0D );
		helper.addBox( 5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 13.0D );
	}

	@Override
	public double getIdlePowerUsage() {
		return 0.5D;
	}

	@Override
	public int getLightLevel(){
		// Check if active
		if(isActive())
			return ACTIVE_TERMINAL_LIGHT_LEVEL;
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {}

	@Override
	public float getCableConnectionLength(AECableType cable) {
		return 2;
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(1,1,false,false);
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		return SAME;
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
		// Get node
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);

		// Getting Node
		if (node == null)
			// No inventory provided
			return null;

		// Getting net of node
		IGrid grid = node.getGrid();

		// Storage cache of network
		IStorageGrid storage = grid.getCache(IStorageGrid.class);

		// Get inventory of cache
		return storage.getInventory(channel);
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered())
			if (this.isActive())
				return PartModelEnum.TERMINAL_HAS_CHANNEL;
			else
				return PartModelEnum.TERMINAL_ON;
		return PartModelEnum.TERMINAL_OFF;
	}

	@Override
	public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
		// Ignored
	}

	@Override
	public IConfigManager getConfigManager() {
		return this.configManager;
	}

	@Override
	public boolean isValid(Object verificationToken) {
		return true;
	}

	@Override
	public void postChange(IBaseMonitor<IAEEnergyStack> monitor, Iterable<IAEEnergyStack> change, IActionSource actionSource) {
		for (ContainerEnergyTerminal listener : listeners) {
			NetworkHandler.sendTo(new PacketTerminalUpdate(((IMEMonitor<IAEEnergyStack>)monitor).getStorageList(), this), (EntityPlayerMP) listener.player);
		}
	}

	@Override
	public void onListUpdate() {

	}
}