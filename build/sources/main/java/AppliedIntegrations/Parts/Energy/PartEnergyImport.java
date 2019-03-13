package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.Storage.CapabilityHelper;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AILog;
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
import static appeng.api.networking.ticking.TickRateModulation.FASTER;

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
	public TickRateModulation doWork(int valuedTransfer, IGridNode node) {
		// Get the world
		World world = this.hostTile.getWorld();

		TileEntity tile = getFacingTile();

		AILog.info("Reached doWork body");
		// Iterate over allowed energy type
		for(EnumCapabilityType energyType : EnumCapabilityType.values){
			AILog.info("Iterating over enumcapability types");
			// Get energy from type
			LiquidAIEnergy energy = energyType.energy;

			if(tile == null && getSide() == null)
				return TickRateModulation.SLOWER;
			CapabilityHelper helper = new CapabilityHelper(tile, getSide());
			// Split value to integer
			int stored = helper.getStored(energy);
			AILog.info("Stored: "+stored);
			// Check if there is energy exists and energy not filtered
			if(stored > 0 && this.getFilteredEnergy() != energy){
				AILog.info("Injecting energy");
				// Find amount of energy that can be injected
				int InjectedAmount = InjectEnergy(new EnergyStack(energy, valuedTransfer), SIMULATE);

				// Inject energy in ME Network
				InjectEnergy(new EnergyStack(energy, InjectedAmount), MODULATE);
				// Remove injected amount from interface storage
				helper.extractEnergy(InjectedAmount, false, energy);
				return FASTER;
			}

		}

		return TickRateModulation.SLOWER;
	}

	private int getMaxTransfer() {
		return 500;
	}

	private LiquidAIEnergy getFilteredEnergy() {
		return null;
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
