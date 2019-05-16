package AppliedIntegrations.api;

import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public interface IAIWirelessEnergyTerminal extends INetworkEncodable, IAEItemPowerStorage {
	/**
	 * Gets the tag used to store the terminal data.
	 *
	 * @param terminalItemstack
	 * @return
	 */
	@Nonnull
	NBTTagCompound getWETerminalTag(@Nonnull ItemStack terminalItemstack);
}
