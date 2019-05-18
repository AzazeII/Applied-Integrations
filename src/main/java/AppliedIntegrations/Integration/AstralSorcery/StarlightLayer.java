package AppliedIntegrations.Integration.AstralSorcery;


import appeng.api.parts.IPart;
import appeng.api.parts.LayerBase;
import appeng.api.util.AEPartLocation;
import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class StarlightLayer extends LayerBase implements ILinkableTile, IStarlightTransmission {
	@Override
	public World getLinkWorld() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((ILinkableTile) part).getLinkWorld();
			}
		}

		return Minecraft.getMinecraft().world;
	}

	@Override
	public BlockPos getLinkPos() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((ILinkableTile) part).getLinkPos();
			}
		}

		return new BlockPos(0, -1, 0);
	}

	@Nullable
	@Override
	public String getUnLocalizedDisplayName() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((ILinkableTile) part).getUnLocalizedDisplayName();
			}
		}

		return "";
	}

	@Override
	public void onLinkCreate(EntityPlayer entityPlayer, BlockPos blockPos) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				((ILinkableTile) part).onLinkCreate(entityPlayer, blockPos);
			}
		}
	}

	@Override
	public boolean tryLink(EntityPlayer entityPlayer, BlockPos blockPos) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((ILinkableTile) part).tryLink(entityPlayer, blockPos);
			}
		}

		return false;
	}

	@Override
	public boolean tryUnlink(EntityPlayer entityPlayer, BlockPos blockPos) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((ILinkableTile) part).tryUnlink(entityPlayer, blockPos);
			}
		}

		return false;
	}

	@Override
	public List<BlockPos> getLinkedPositions() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((ILinkableTile) part).getLinkedPositions();
			}
		}

		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public BlockPos getTrPos() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((IStarlightTransmission) part).getTrPos();
			}
		}

		return new BlockPos(0, -1, 0);
	}

	@Nonnull
	@Override
	public World getTrWorld() {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((IStarlightTransmission) part).getTrWorld();
			}
		}

		return Minecraft.getMinecraft().world;
	}

	@Nonnull
	@Override
	public IPrismTransmissionNode provideTransmissionNode(BlockPos blockPos) {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			IPart part = this.getPart(side);
			if (part instanceof ILinkableTile) {
				return ((IStarlightTransmission) part).provideTransmissionNode(blockPos);
			}
		}

		return new IPrismTransmissionNode() {
			@Override
			public BlockPos getPos() {
				return null;
			}

			@Override
			public boolean notifyUnlink(World world, BlockPos blockPos) {
				return false;
			}

			@Override
			public void notifyLink(World world, BlockPos blockPos) {

			}

			@Override
			public void notifySourceLink(World world, BlockPos blockPos) {

			}

			@Override
			public void notifySourceUnlink(World world, BlockPos blockPos) {

			}

			@Override
			public boolean notifyBlockChange(World world, BlockPos blockPos) {
				return false;
			}

			@Override
			public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler worldNetworkHandler) {
				return null;
			}

			@Override
			public List<BlockPos> getSources() {
				return null;
			}

			@Override
			public TransmissionClassRegistry.TransmissionProvider getProvider() {
				return null;
			}

			@Override
			public void readFromNBT(NBTTagCompound nbtTagCompound) {

			}

			@Override
			public void writeToNBT(NBTTagCompound nbtTagCompound) {

			}
		};
	}
}
