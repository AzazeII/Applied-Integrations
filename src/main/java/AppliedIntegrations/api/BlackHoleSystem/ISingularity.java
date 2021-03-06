package AppliedIntegrations.api.BlackHoleSystem;


import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.entities.EntitySingularity;
import appeng.api.config.Actionable;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 * <p>
 * Class, used to mark any singularity/hole
 */
public interface ISingularity extends ISyncHost {
	static ISingularity readFromNBT(NBTTagCompound compound) {
		BlockPos p = BlockPos.fromLong(compound.getLong("#POS"));
		World w = DimensionManager.getWorld(compound.getInteger("#WORLD"));
		return (ISingularity) w.getTileEntity(p);
	}

	static void writeNBTTag(ISingularity operatedTile, NBTTagCompound compound) {
		compound.setLong("#POS", ((TileEntity) operatedTile).getPos().toLong());
		compound.setInteger("#WORLD", ((TileEntity) operatedTile).getWorld().provider.getDimension());
	}

	/**
	 * @param l Add mass to singularity
	 */
	void addMass(long l);

	/**
	 * Store new AE stack in singularity
	 * @param stack to store
	 * @param actionable mode
	 * @return Stack added
	 */
	IAEStack<?> addStack(IAEStack<?> stack, Actionable actionable);

	/**
	 * Get stack list
	 * @param iStorageChannel key channel
	 * @return Stack list from key channel
	 */
	IItemList<?> getList(IStorageChannel iStorageChannel);

	/**
	 * Client-callback function, called by packet mass change
	 * @param mass new mass
	 */
	@SideOnly(CLIENT)
	void setMassFromServer(long mass);

	/**
	 * @return Current mass of singularity
	 */
	long getMass();

	/**
	 * @return Does this singularity has other singularity entangled with this?
	 */
	boolean isEntangled();

	/**
	 *
	 * @param singularity Singularity to link with this
	 */
	void setEntangledHoleEntity(EntitySingularity singularity);

	/**
	 * @param pylon New event listener
	 */
	void addListener(IPylon pylon);
}
