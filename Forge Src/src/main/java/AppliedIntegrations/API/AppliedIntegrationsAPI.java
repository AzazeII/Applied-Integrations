package AppliedIntegrations.API;

import appeng.api.definitions.IBlockDefinition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
/**
 * @Author Azazell
 */
public interface AppliedIntegrationsAPI {
	public void addEnergyToShowBlacklist(Class<? extends LiquidAIEnergy> clazz);

	public void addEnergyToShowBlacklist(LiquidAIEnergy Energy);

	public void addEnergyToStorageBlacklist(Class<? extends LiquidAIEnergy> clazz);

	public void addEnergyToStorageBlacklist(LiquidAIEnergy Energy);

	public IBlockDefinition blocks();

	public boolean canEnergySeeInTerminal(LiquidAIEnergy Energy);

	public boolean canStoreEnergy(LiquidAIEnergy Energy);

	public String getVersion();

	public ItemStack openWirelessEnergyTerminal(EntityPlayer player, ItemStack stack, World world);

	@Deprecated
	public ItemStack openWirelessTerminal(EntityPlayer player, ItemStack stack, World world, int x, int y, int z, Long key);
	
	public void registerFuelBurnTime(LiquidAIEnergy fuel, int burnTime);

	public boolean isEnergyStack(IEnergyStack stack);

	public boolean isEnergyStack(FluidStack stack);

	public boolean isEnergy(LiquidAIEnergy Energy);
}
