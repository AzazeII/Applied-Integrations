package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;

import cofh.redstoneflux.api.IEnergyProvider;
import crazypants.enderio.base.power.ILegacyPoweredTile;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.block.wiring.TileEntityElectricBlock;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;

/**
 * @Author Azazell
 */
public class PartEnergyImport extends AIOPart
{

	public PartEnergyImport()
	{
		super( PartEnum.EnergyImportBus, SecurityPermissions.INJECT );
	}

	@Override
	public boolean energyTransferAllowed(LiquidAIEnergy energy) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos pos, Random r) {}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox( 6, 6, 11, 10, 10, 13 );
		bch.addBox( 5, 5, 13, 11, 11, 14 );
		bch.addBox( 4, 4, 14, 12, 12, 16 );
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
	public TickRateModulation doWork(int valuedTransfer, IGridNode node) {
		// Get the world
		World world = this.hostTile.getWorld();

		TileEntity tile = getFacingTile();

		// basic rf handler
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage energyHandler = (IStrictEnergyStorage) tile;
			int diff = this.InjectEnergy( new EnergyStack(J, valuedTransfer), SIMULATE) - valuedTransfer;
			// Try to extract energy
			int ContainerStorage = (int)energyHandler.getEnergy();
			// Check if facing tile has enough storage, and network can handle this operation
			if (ContainerStorage >= valuedTransfer && diff == 0) {
				this.InjectEnergy(new EnergyStack(J, valuedTransfer), MODULATE);
				energyHandler.setEnergy(energyHandler.getEnergy() - valuedTransfer);
				return TickRateModulation.FASTER;
			}
		}else if (tile instanceof IEnergySource && energyTransferAllowed(EU)) {
			// check if network have enough storage space
			int diff = this.InjectEnergy(new EnergyStack(EU, valuedTransfer), SIMULATE) - valuedTransfer;
			if(tile instanceof TileEntityElectricBlock){
				TileEntityElectricBlock source = (TileEntityElectricBlock) tile;

				int storage = (int)source.energy.getEnergy();
				if (storage >= valuedTransfer) {
					this.InjectEnergy(new EnergyStack(EU, valuedTransfer), MODULATE);
					source.addEnergy(valuedTransfer);
					return TickRateModulation.FASTER;
				}
			}

		} else  if (energyTransferAllowed(RF)) {
			int diff = this.InjectEnergy(new EnergyStack(RF, valuedTransfer), SIMULATE) - valuedTransfer;

			// EIO
			if (diff == 0) {
				if (tile instanceof ILegacyPoweredTile) {
					ILegacyPoweredTile abstractEntity = (ILegacyPoweredTile) tile;
					if(abstractEntity.getEnergyStored() > valuedTransfer) {
						abstractEntity.setEnergyStored(abstractEntity.getEnergyStored() - valuedTransfer);
						InjectEnergy(new EnergyStack(RF, valuedTransfer), MODULATE);
						return TickRateModulation.FASTER;
					}
				}
				// main case
				if (tile instanceof IEnergyProvider) {
					IEnergyProvider energyHandler = (IEnergyProvider) tile;
					int ContainerDiff = energyHandler.extractEnergy(this.getSide().getFacing(), valuedTransfer, true);
					// Check if facing tile has enough storage, and network can handle this operation

					if (diff == 0) {
						this.InjectEnergy(new EnergyStack(RF, energyHandler.extractEnergy(this.getSide().getFacing().getOpposite(), valuedTransfer, false)), MODULATE);
						return TickRateModulation.FASTER;
					}
				}
			}
			// Enderio capacitor uses custom methods to handle energy extraction
		}
		return TickRateModulation.SLOWER;
	}

	@Override
	public void onInventoryChanged() {

	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered())
			if (this.isActive())
				return PartModelEnum.IMPORT_HAS_CHANNEL;
			else
				return PartModelEnum.IMPORT_ON;
		return PartModelEnum.IMPORT_OFF;
	}
}
