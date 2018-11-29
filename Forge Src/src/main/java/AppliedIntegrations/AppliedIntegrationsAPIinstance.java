package AppliedIntegrations;

import AppliedIntegrations.API.*;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
/**
 * @Author Azazell
 */
public class AppliedIntegrationsAPIinstance implements AppliedIntegrationsAPI {

	@Override
	public void addEnergyToShowBlacklist(Class<? extends LiquidAIEnergy> clazz) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEnergyToShowBlacklist(LiquidAIEnergy Energy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEnergyToStorageBlacklist(Class<? extends LiquidAIEnergy> clazz) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEnergyToStorageBlacklist(LiquidAIEnergy Energy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBlockDefinition blocks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canEnergySeeInTerminal(LiquidAIEnergy Energy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canStoreEnergy(LiquidAIEnergy Energy) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack openWirelessEnergyTerminal(EntityPlayer player, ItemStack stack, World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack openWirelessTerminal(EntityPlayer player, ItemStack stack, World world, int x, int y, int z,
			Long key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerFuelBurnTime(LiquidAIEnergy fuel, int burnTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEnergyStack(IEnergyStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnergyStack(FluidStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnergy(LiquidAIEnergy Energy) {
		// TODO Auto-generated method stub
		if(Energy instanceof LiquidAIEnergy)
			return true;
		return false;
	}

}
