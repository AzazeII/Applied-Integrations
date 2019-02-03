package AppliedIntegrations.Parts.IO;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.GuiEnergyIO;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.EffectiveSide;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AECableType;
import appeng.me.helpers.MachineSource;
import cofh.redstoneflux.api.IEnergyReceiver;
import ic2.api.energy.tile.IEnergySink;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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
public class PartEnergyExport extends AIOPart {
	public double IDLE_POWER_DRAIN = 0.2;

	private int baseTransfer = 800;

	private final static int MINIMUM_TICKS_PER_OPERATION = 10;

	private final static int MAXIMUM_TICKS_PER_OPERATION = 40;

	private final static int MAXIMUM_TRANSFER_PER_SECOND = 64;

	private final static int MINIMUM_TRANSFER_PER_SECOND = 1;


	protected TileEntity facingEnergyStorage;

	public PartEnergyExport() {
		super(PartEnum.EnergyExportBus, SecurityPermissions.EXTRACT);
	}

	@Override
	public boolean energyTransferAllowed(LiquidAIEnergy Energy) {
		return true;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {

		bch.addBox( 4, 4, 12, 12, 12, 14 );
		bch.addBox( 5, 5, 14, 11, 11, 15 );
		bch.addBox( 6, 6, 15, 10, 10, 16 );
		bch.addBox( 6, 6, 11, 10, 10, 12 );
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
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType )
	{
		// Call super
		super.writeToNBT( data, saveType );

		boolean doSave = ( saveType == PartItemStack.WORLD );
		if( !doSave )
		{
			// Are there any filters?
			for( LiquidAIEnergy energy : this.filteredEnergies )
			{
				if( energy != null )
				{
					// Only save the void state if filters are set.
					doSave = true;
					break;
				}
			}
		}

	}
	@Override
	public Object getServerGuiElement( final EntityPlayer player ) {
		return new ContainerPartEnergyIOBus(this,player);
	}

	@Override
	public void onInventoryChanged() {

	}

	@Override
	public boolean doWork( final int amountToFillContainer ) {
		// Get the world
		World world = this.hostTile.getWorld();

		// Get our location
		int x = this.hostTile.getPos().getX();
		int y = this.hostTile.getPos().getY();
		int z = this.hostTile.getPos().getZ();

		// Get the tile entity part is facing
		TileEntity tile = world.getTileEntity( new BlockPos(x + this.getSide().xOffset, y + this.getSide().yOffset, z + this.getSide().zOffset ));

		int valuedTransfer = 5000;
		if (tile instanceof IStrictEnergyAcceptor) {
			IStrictEnergyAcceptor acceptor = (IStrictEnergyAcceptor) tile;
			int diff = this.ExtractEnergy(new FluidStack(J, valuedTransfer), SIMULATE);
			boolean canReceive = acceptor.canReceiveEnergy(getSide().getFacing().getOpposite());

			// Check if facing tile has enough storage, and network can handle this operation
			if (canReceive && diff != valuedTransfer) {
				this.ExtractEnergy(new FluidStack(J, (int)acceptor.acceptEnergy(getSide().getFacing().getOpposite(), valuedTransfer, false)), MODULATE);

			}
		}else if (tile instanceof IEnergyReceiver && energyTransferAllowed(RF)) {
			// minimum value that receiver can accept
			IEnergyReceiver energyReceiver = (IEnergyReceiver) tile;
			int diff = this.ExtractEnergy(new FluidStack(RF, valuedTransfer), SIMULATE);
			int ContainerDiff = energyReceiver.receiveEnergy(this.getSide().getFacing().getOpposite(), valuedTransfer, true);
			// Check if facing tile has enough storage, and network can handle this operation
			if (ContainerDiff != 0 && diff != valuedTransfer) {
				this.ExtractEnergy(new FluidStack(RF, energyReceiver.receiveEnergy(this.getSide().getFacing().getOpposite(), valuedTransfer, false)), MODULATE);
			}
		} else if (tile instanceof IEnergySink && energyTransferAllowed(EU)) {

			IEnergySink sink = (IEnergySink) tile;
			int diff = this.ExtractEnergy(new FluidStack(EU, valuedTransfer), SIMULATE);
			int ContainerDiff = (int) sink.getDemandedEnergy();
			if (ContainerDiff != 0 && diff != valuedTransfer) {
				sink.injectEnergy(this.getSide().getFacing().getOpposite(), valuedTransfer, 4.0D);
				this.ExtractEnergy(new FluidStack(EU, valuedTransfer), MODULATE);
			}
		}
		return true;
	}
	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos blockPos1) {
		// Ignored client side
		if( EffectiveSide.isClientSide() )
		{
			return;
		}

		// Set that we are not facing a container
		this.facingEnergyStorage = null;

		// Get the tile we are facing
		TileEntity tileEntity = this.getFacingTile();

		// Are we facing a container?
		if( tileEntity instanceof IEnergyReceiver || tileEntity instanceof IEnergySink)
		{
			this.facingEnergyStorage = tileEntity;
		}
	}
	public int ExtractEnergy(FluidStack resource, Actionable mode) {
		IGridNode node = this.getGridNode();
		if(node == null)
			return 0;
		IGrid grid = node.getGrid();
		if (grid == null) {
			AILog.info("Grid cannot be initialized, WTF?");
			return 0;
		}
		IStorageGrid storage = (IStorageGrid)grid.getCache(IStorageGrid.class);
		if ((storage == null) && (node.getGrid().getCache(IStorageGrid.class) == null)) {
			AILog.info("StorageGrid cannot be initialized, WTF?");
			return 0; }
		IAEEnergyStack notRemoved;
		if (mode == MODULATE) {
			notRemoved = (IAEEnergyStack)storage.getInventory(getChannel()).extractItems(
					getChannel().createStack(resource), MODULATE, new MachineSource(this));
		}
		else
		{
			notRemoved = (IAEEnergyStack)storage.getInventory(getChannel()).extractItems(getChannel().createStack(resource), SIMULATE, new MachineSource(this));
		}


		if (notRemoved == null)
			return resource.amount;
		return (int)(resource.amount - notRemoved.getStackSize());
	}
}
