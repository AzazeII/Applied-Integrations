package AppliedIntegrations.api;


import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.util.AEPartLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * @Author Azazell
 */
public interface IEnergyInterface extends IEnergyInterfaceDuality, INetworkManipulator, ISyncHost {
	void initEnergyStorage(LiquidAIEnergy energy, AEPartLocation side);
	int getMaxEnergyStored(AEPartLocation side, LiquidAIEnergy linkedMetric);
	TileEntity getFacingTile(EnumFacing side);
	List<ContainerEnergyInterface> getListeners();

	/**
	 * Setter used by interface dualities to notify owner about new injected energy.
	 */
	void setLastInjectedEnergy(AEPartLocation side, LiquidAIEnergy energy);

	/**
	 * @return Outer grid inventory of this host. Used by AppliedIntegrations.Inventory.Handlers#HandlerEnergyStorageBusInterface
	 */
	default IMEInventory<IAEEnergyStack> getOuterGridInventory() {
		if (getGridNode() == null) {
			return null;
		}

		IGrid grid = getGridNode().getGrid();
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		return storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));
	}

	IGridNode getGridNode();
}
