package AppliedIntegrations.Parts.IO;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.GuiEnergyIO;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Render.TextureManager;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.data.IAEFluidStack;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.power.IInternalPowerHandler;
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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

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
	public int cableConnectionRenderTo() {
		return 1;
	}

	@Override
	public boolean doWork( final int transferAmount )
	{
		// by some reasons i cannot wrap import bus work into this function
		return false;
	}
	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall ) {
		// Get the world
		World world = this.hostTile.getWorldObj();

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
			// Industrial craft author, how to check if IEnergySource have demanded energy? are you ROFLING?
			if(tile instanceof TileEntityBaseGenerator) {
				TileEntityBaseGenerator source = (TileEntityBaseGenerator) tile;

				int storage = (int)source.power;
				if (storage >= valuedTransfer) {
					this.InjectEnergy(node, new FluidStack(EU, valuedTransfer), true);
					source.drawEnergy(valuedTransfer);
				}
			}else if(tile instanceof TileEntityElectricBlock){
				TileEntityElectricBlock source = (TileEntityElectricBlock) tile;

				int storage = (int)source.energy;
				if (storage >= valuedTransfer) {
					this.InjectEnergy(node, new FluidStack(EU, valuedTransfer), true);
					source.drawEnergy(valuedTransfer);
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
				if (tile instanceof IEnergyHandler) {
					IEnergyHandler energyHandler = (IEnergyHandler) tile;
					int ContainerDiff = energyHandler.extractEnergy(this.getSide(), valuedTransfer, true);
					// Check if facing tile has enough storage, and network can handle this operation

					if (diff == 0) {
						this.InjectEnergy(node, new FluidStack(RF, energyHandler.extractEnergy(this.getSide().getOpposite(), valuedTransfer, false)), true);
					}
				}
			}
			// Enderio capacitor uses custom methods to handle energy extraction
		}
			return TickRateModulation.SAME;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {}
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
		IAEFluidStack notRemoved;
		if (doFill) { // Modulation
			notRemoved = storage.getFluidInventory().injectItems(
					AEApi.instance().storage().createFluidStack(resource),
					Actionable.MODULATE, new MachineSource(this));


		} else {//Simulation
			notRemoved = storage.getFluidInventory().injectItems(
					AEApi.instance().storage().createFluidStack(resource),
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
	public IIcon getBreakingTexture() {
		return null;
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
	@SideOnly( Side.CLIENT )
	public void renderInventory( final IPartRenderHelper rh, final RenderBlocks renderer )
	{
		IIcon side = TextureManager.ENERGY_IMPORT_BUS.getTextures()[3];

		rh.setTexture( side,side,side,TextureManager.ENERGY_IMPORT_BUS.getTexture(), side,side);

		rh.setBounds( 3, 3, 15, 13, 13, 16 );
		rh.renderInventoryBox( renderer );

		rh.setBounds( 4, 4, 14, 12, 12, 15 );
		rh.renderInventoryBox( renderer );

		rh.setBounds( 5, 5, 13, 11, 11, 14 );
		rh.renderInventoryBox( renderer );
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void renderStatic( final int x, final int y, final int z, final IPartRenderHelper rh, final RenderBlocks renderer )
	{
		IIcon side = TextureManager.ENERGY_IMPORT_BUS.getTextures()[3];

		// Render main part
		rh.setTexture( side,side,side,TextureManager.ENERGY_IMPORT_BUS.getTexture(), side,side);
		// From D2P - layer 3
		rh.setBounds(4, 4, 14, 12, 12, 16);
		rh.renderBlock(x, y, z, renderer);

		// layer 2
		rh.setBounds( 5, 5, 13, 11, 11, 14 );
		rh.renderBlock( x, y, z, renderer );
		// layer 1
		if(!isActive())
		{
			rh.setBounds(6, 6, 12, 10, 10, 13);
			rh.renderBlock(x, y, z, renderer);
			rh.setBounds(6, 6, 11, 10, 10, 12);
			rh.renderBlock(x, y, z, renderer);
		}
	}
	@Override
	public boolean onActivate( final EntityPlayer player, final Vec3 position )
	{
		if(this.getHostTile().getWorldObj().isRemote == false) {
			player.openGui(AppliedIntegrations.instance, 4, player.worldObj, hostTile.xCoord, hostTile.yCoord, hostTile.zCoord);
			return true;
		}
		return false;
	}
	@Override
	public Object getServerGuiElement( final EntityPlayer player ) {
		return new ContainerPartEnergyIOBus(this,player);
	}

	@Override
	public void onInventoryChanged() {

	}
}
