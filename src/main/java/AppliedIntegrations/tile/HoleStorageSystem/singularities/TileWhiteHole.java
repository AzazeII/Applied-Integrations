package AppliedIntegrations.tile.HoleStorageSystem.singularities;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.BlackHoleSystem.IPylon;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.EnergyList;
import AppliedIntegrations.grid.Mana.ManaList;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.fluids.util.FluidList;
import appeng.util.item.ItemList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 */
public class TileWhiteHole extends TileEntity implements ISingularity {

	public long mass;

	private TileBlackHole entangledHole = null;

	private List<IPylon> listeners = new ArrayList<>();

	public TileWhiteHole() {

		mass = (long) (Math.random() * 2048);
	}

	public double getHoleRadius() {
		// White hole's mass is opposite of black hole's mass, so when black hole grow, then white hole shrink
		double lightSpeed = 3;
		return Math.max(Math.cbrt(Math.cbrt(2 * 6.7 * mass / Math.pow(lightSpeed, 2))), 0.3);
	}

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {

		return false;
	}

	@Override
	public void invalidate() {

		super.invalidate();

		// Iterate over listeners
		for (IPylon pylon : listeners) {
			// Invalidate singularity
			pylon.setSingularity(null);
		}
	}

	@Override
	public void addMass(long l) {

		mass -= l;
	}

	@Override
	public IAEStack<?> addStack(IAEStack<?> stack, Actionable actionable) {
		// Check not null
		if (stack == null) {
			return null;
		}

		// Check not null
		if (entangledHole == null) {
			return null;
		}

		// Check stack size
		if (stack.getStackSize() <= 0) {
			return null;
		}

		// Item branch
		if (stack instanceof IAEItemStack) {
			// Get pointer value of this stack
			IAEItemStack pStack = entangledHole.storedItems.findPrecise((IAEItemStack) stack);

			// Create copy
			IAEItemStack Return = pStack.copy();

			// Check if stack exists in list
			if (pStack != null) {
				// get stack size
				long size = pStack.getStackSize();

				// Check size greater than 0
				if (size <= 0) {
					return null;
				}

				// Check if size greater then requested
				if (pStack.getStackSize() <= size) {
					// Set stack size to current stack size
					Return.setStackSize(pStack.getStackSize());

					// Modulate extraction
					if (actionable == Actionable.MODULATE)
					// Decrease current stack size
					{
						pStack.setStackSize(0);
					}
				} else {
					// Set stack size to #Var: size
					Return.setStackSize(size);

					// Modulate extraction
					if (actionable == Actionable.MODULATE)
					// Set current stack size to current stack size - #Var: size
					{
						pStack.setStackSize(pStack.getStackSize() - size);
					}
				}
			}

			// Update cell array
			for (IPylon pylon : listeners)
				pylon.postCellInventoryEvent();

			// Return stack
			return Return;
		}

		return null;
	}

	@Override
	public IItemList<?> getList(IStorageChannel chan) {
		// Check channel
		if (chan == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class))
		// If not entangled -> return empty list, else return entangled hole's list
		{
			return !isEntangled() ? new ItemList() : entangledHole.getList(chan);
		}

		if (chan == AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class))
		// If not entangled -> return empty list, else return entangled hole's list
		{
			return !isEntangled() ? new FluidList() : entangledHole.getList(chan);
		}

		if (AIConfig.enableEnergyFeatures && chan == AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class))
		// If not entangled -> return empty list, else return entangled hole's list
		{
			return !isEntangled() ? new EnergyList() : entangledHole.getList(chan);
		}

		if (AIConfig.enableManaFeatures && Loader.isModLoaded("botania") && chan == AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class))
		// If not entangled -> return empty list, else return entangled hole's list
		{
			return !isEntangled() ? new ManaList() : entangledHole.getList(chan);
		}

		return null;
	}

	@Override
	@SideOnly(CLIENT)
	public void setMassFromServer(long mass) {

		this.mass = mass;
	}

	@Override
	public long getMass() {

		return mass;
	}

	@Override
	public boolean isEntangled() {

		return entangledHole != null;
	}

	@Override
	public void setEntangledHole(ISingularity t) {

		AILog.chatLog("Setting entangled singularity to " + t.toString());
		entangledHole = (TileBlackHole) t;

		// Update cell array
		for (IPylon pylon : listeners)
			pylon.postCellInventoryEvent();
	}

	@Override
	public void addListener(IPylon pylon) {

		listeners.add(pylon);
	}
}
