package AppliedIntegrations.Parts.IO;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.GuiEnergyIO;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AECableType;
import appeng.me.helpers.MachineSource;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.wiring.TileEntityChargepadCESU;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;

/**
 * @Author Azazell
 */
public class PartEnergyImport extends AIOPart
{
	public double IDLE_POWER_DRAIN = 0.2;
	private boolean EUloaded = false;

	public PartEnergyImport()
	{
		super( PartEnum.EnergyImportBus, SecurityPermissions.INJECT );
	}

	@Override
	public boolean energyTransferAllowed(LiquidAIEnergy energy) {
		return true;
	}

	@Override
	public boolean doWork( final int transferAmount )
	{
		// by some reasons i cannot wrap import bus work into this function
		return false;
	}
	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall ) {
		super.tickingRequest(node,ticksSinceLastCall);
		// Get the world
		World world = this.hostTile.getWorld();

		TileEntity tile = getFacingTile();

		int valuedTransfer = 5000;
		// basic rf handler
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage energyHandler = (IStrictEnergyStorage) tile;
			int diff = this.InjectEnergy(node, new FluidStack(J, valuedTransfer), false) - valuedTransfer;
			// Try to extract energy
			int ContainerStorage = (int)energyHandler.getEnergy();
			// Check if facing tile has enough storage, and network can handle this operation
			if (ContainerStorage >= valuedTransfer && diff == 0) {
				this.InjectEnergy(node,new FluidStack(J, valuedTransfer), true);
				energyHandler.setEnergy(energyHandler.getEnergy() - valuedTransfer);
			}
		}else if (tile instanceof IEnergySource && energyTransferAllowed(EU)) {
			// check if network have enough storage space
			int diff = this.InjectEnergy(node, new FluidStack(EU, valuedTransfer), false) - valuedTransfer;
			if(tile instanceof TileEntityElectricBlock){
				TileEntityElectricBlock source = (TileEntityElectricBlock) tile;

				int storage = (int)source.energy.getEnergy();
				if (storage >= valuedTransfer) {
					this.InjectEnergy(node, new FluidStack(EU, valuedTransfer), true);
					source.addEnergy(valuedTransfer);
				}
			}

		} else  if (energyTransferAllowed(RF)) {
			int diff = this.InjectEnergy(node, new FluidStack(RF, valuedTransfer), false) - valuedTransfer;

			// EIO
			if (diff == 0) {
				if (tile instanceof AbstractPoweredMachineEntity) {
					AbstractPoweredMachineEntity abstractEntity = (AbstractPoweredMachineEntity) tile;
					if(abstractEntity.getEnergyStored() > valuedTransfer) {
						abstractEntity.setEnergyStored(abstractEntity.getEnergyStored() - valuedTransfer);
						InjectEnergy(node, new FluidStack(RF, valuedTransfer), true);
					}
				} else if (tile instanceof TileCapBank) {
					TileCapBank handler = (TileCapBank) tile;
					if(handler.getEnergyStored() > valuedTransfer) {
						handler.addEnergy(-valuedTransfer);
						InjectEnergy(node, new FluidStack(RF, valuedTransfer), true);
					}
				}
				// main case
				if (tile instanceof IEnergyProvider) {
					IEnergyProvider energyHandler = (IEnergyProvider) tile;
					int ContainerDiff = energyHandler.extractEnergy(this.getSide().getFacing(), valuedTransfer, true);
					// Check if facing tile has enough storage, and network can handle this operation

					if (diff == 0) {
						this.InjectEnergy(node, new FluidStack(RF, energyHandler.extractEnergy(this.getSide().getFacing().getOpposite(), valuedTransfer, false)), true);
					}
				}
			}
			// Enderio capacitor uses custom methods to handle energy extraction
		}
			return TickRateModulation.SAME;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos pos, Random r) {}
	public int InjectEnergy(IGridNode node,FluidStack resource, boolean doFill) {
		IGrid grid = node.getGrid(); // check grid node
		if (grid == null) {
			AILog.info("Grid cannot be initialized, WTF?");
			return 0;
		}
		IStorageGrid storage = grid.getCache(IStorageGrid.class); // check storage gridnode
		if (storage == null && this.node.getGrid().getCache(IStorageGrid.class) == null) {
			AILog.info("StorageGrid cannot be initialized, WTF?");
			return 0;
		}
		IAEEnergyStack notRemoved;
		if (doFill) { // Modulation
			notRemoved = storage.getInventory(getChannel()).injectItems(
					getChannel().createStack(resource),
					Actionable.MODULATE, new MachineSource(this));


		} else {//Simulation
			notRemoved = storage.getInventory(getChannel()).injectItems(
					getChannel().createStack(resource),
					Actionable.SIMULATE, new MachineSource(this));

		}
		if (notRemoved == null)
			return resource.amount;
		return (int) (resource.amount - notRemoved.getStackSize());
	}
	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox( 6, 6, 11, 10, 10, 13 );
		bch.addBox( 5, 5, 13, 11, 11, 14 );
		bch.addBox( 4, 4, 14, 12, 12, 16 );
	}

	@Override
	public double getIdlePowerUsage() {
		return this.IDLE_POWER_DRAIN;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {


	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 0;
	}

	@Override
	public Object getServerGuiElement( final EntityPlayer player ) {
		return new ContainerPartEnergyIOBus(this,player);
	}

	@Override
	public void onInventoryChanged() {

	}
}
