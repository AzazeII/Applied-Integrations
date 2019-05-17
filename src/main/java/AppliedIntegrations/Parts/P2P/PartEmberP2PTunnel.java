package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import teamroots.embers.power.EmberCapabilityProvider;
import teamroots.embers.power.IEmberCapability;
import teamroots.embers.tileentity.TileEntityEmitter;
import teamroots.embers.tileentity.TileEntityReceiver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO: 2019-02-17 Integration with Embers

/**
 * @Author Azazell
 */
public class PartEmberP2PTunnel extends PartP2PTunnel<PartEmberP2PTunnel> implements IEmberIntegrated {
	public PartEmberP2PTunnel(ItemStack is) {
		super(is);
	}

	
}
