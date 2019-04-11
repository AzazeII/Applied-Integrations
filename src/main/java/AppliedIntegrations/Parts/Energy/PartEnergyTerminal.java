package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Grid.ICraftingIssuerHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.SortMode;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketTerminalChange;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.ViewItems;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static appeng.api.networking.ticking.TickRateModulation.IDLE;
import static appeng.api.networking.ticking.TickRateModulation.SAME;

/**
 * @Author Azazell
 */
public class PartEnergyTerminal extends AIRotatablePart implements IStackWatcherHost,ICraftingIssuerHost,IGridTickable {
	public PartEnergyTerminal() {
		super(PartEnum.EnergyTerminal, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT, SecurityPermissions.CRAFT);
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d position) {

		if(isActive()) {
			AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiTerminalPart, player, getSide(), getPos());
		}

		return true;
	}

	@Override
	public ItemStack getIcon() {
		return null;
	}

	@Override
	public void launchGUI(EntityPlayer player) {

	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes( final IPartCollisionHelper helper )
	{
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

	@Override
	public void updateWatcher(IStackWatcher newWatcher) {

	}

	@Override
	public void onStackChange(IItemList<?> o, IAEStack<?> fullStack, IAEStack<?> diffStack, IActionSource src, IStorageChannel<?> chan) {

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
		// Getting Node
		if (getGridNode(AEPartLocation.INTERNAL) == null)
			return null;
		// Getting net of node
		IGrid grid = getGridNode(AEPartLocation.INTERNAL).getGrid();
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
	public IConfigManager getConfigManager() {
		return null;
	}
}