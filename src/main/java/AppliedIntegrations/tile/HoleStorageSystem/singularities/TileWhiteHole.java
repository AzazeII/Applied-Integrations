package AppliedIntegrations.tile.HoleStorageSystem.singularities;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketSingularitiesEntangle;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.BlackHoleSystem.IPylon;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.EnergyList;
import AppliedIntegrations.grid.Mana.ManaList;
import AppliedIntegrations.tile.entities.EntitySingularity;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEPartLocation;
import appeng.fluids.util.FluidList;
import appeng.util.Platform;
import appeng.util.item.ItemList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
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
public class TileWhiteHole extends TileEntity implements ISingularity, ITickable {
	public long mass;

	public TileBlackHole entangledHole;
	private EntitySingularity entangledHoleEntity = null;

	private List<IPylon> listeners = new ArrayList<>();
	public boolean notifyClientAboutSingularitiesEntangle;

	public TileWhiteHole() {
		mass = (long) (Math.random() * 2048);
	}

	public double getHoleRadius() {
		// White hole's mass is opposite of black hole's mass, so when black hole grows, white hole shrinks
		double lightSpeed = 3;
		return Math.max(Math.cbrt(Math.cbrt(2 * 6.7 * mass / Math.pow(lightSpeed, 2))), 0.3);
	}

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		for (IPylon pylon : listeners) {
			pylon.setSingularity(null);
		}
	}

	@Override
	public void update() {
		if (Platform.isClient()) {
			return;
		}

		// This notification will be performed in next tick after holes linking
		if (notifyClientAboutSingularitiesEntangle) {
			NetworkHandler.sendToAll(new PacketSingularitiesEntangle(entangledHole, this));
			notifyClientAboutSingularitiesEntangle = false;
		}

		if (entangledHoleEntity != null && entangledHoleEntity.isDead && entangledHoleEntity.getBornSingularity() != null && entangledHole == null) {
			// If entity is dead, then it's already transformed into tile, so we can find linked black hole
			entangledHole = (TileBlackHole) entangledHoleEntity.getBornSingularity();
			entangledHoleEntity = null;
			notifyClientAboutSingularitiesEntangle = true;
		}
	}

	@Override
	public void addMass(long l) {
		mass -= l;
	}

	@Override
	public IAEStack<?> addStack(IAEStack<?> stack, Actionable actionable) {
		if (stack == null) {
			return null;
		}

		if (entangledHole == null) {
			return null;
		}

		if (stack.getStackSize() <= 0) {
			return null;
		}

		if (stack instanceof IAEItemStack) {
			IAEItemStack pStack = entangledHole.storedItems.findPrecise((IAEItemStack) stack);
			IAEItemStack addStackReturn = pStack.copy();
			long size = pStack.getStackSize();
			if (size <= 0) {
				return null;
			}

			if (pStack.getStackSize() <= size) {
				addStackReturn.setStackSize(pStack.getStackSize());

				// Modulate extraction
				if (actionable == Actionable.MODULATE) {
					pStack.setStackSize(0);
				}
			} else {
				addStackReturn.setStackSize(size);

				// Modulate extraction
				if (actionable == Actionable.MODULATE) {
					pStack.setStackSize(pStack.getStackSize() - size);
				}
			}

			for (IPylon pylon : listeners) {
				pylon.postCellInventoryEvent();
			}

			return addStackReturn;
		}

		return null;
	}

	@Override
	public IItemList<?> getList(IStorageChannel chan) {
		if (chan == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
			return !isEntangled() ? new ItemList() : entangledHole.getList(chan);
		}

		if (chan == AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class)) {
			return !isEntangled() ? new FluidList() : entangledHole.getList(chan);
		}

		if (AIConfig.enableEnergyFeatures && chan == AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class)) {
			return !isEntangled() ? new EnergyList() : entangledHole.getList(chan);
		}

		if (AIConfig.enableManaFeatures && Loader.isModLoaded("botania") && chan == AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class)) {
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
	public void setEntangledHoleEntity(EntitySingularity singularity) {
		AILog.chatLog("Setting entangled singularity to " + singularity.toString());
		entangledHoleEntity = singularity;

		for (IPylon pylon : listeners) {
			pylon.postCellInventoryEvent();
		}
	}

	@Override
	public void addListener(IPylon pylon) {
		listeners.add(pylon);
	}

	@Override
	public BlockPos getHostPos() {
		return pos;
	}

	@Override
	public World getHostWorld() {
		return world;
	}

	@Override
	public AEPartLocation getHostSide() {
		return AEPartLocation.INTERNAL;
	}
}
