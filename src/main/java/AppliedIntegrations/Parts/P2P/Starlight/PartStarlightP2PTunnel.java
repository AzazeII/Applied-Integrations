package AppliedIntegrations.Parts.P2P.Starlight;


import AppliedIntegrations.Parts.P2P.AIP2PModels;
import AppliedIntegrations.Parts.P2P.AIPartP2PTunnel;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.parts.IPartModel;
import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

// TODO: 2019-02-17 Integrations with Astral sorcery

/**
 * @Author Azazell
 */
public class PartStarlightP2PTunnel extends AIPartP2PTunnel<PartStarlightP2PTunnel> implements IStarlightTransmission, ILinkableTile {
	private abstract class StarlightLinkable implements ILinkableTile, IStarlightTransmission {
		private List<BlockPos> positions = new LinkedList<>();

		@Override
		public World getLinkWorld() {
			return PartStarlightP2PTunnel.this.getHost().getTile().getWorld();
		}

		@Override
		public BlockPos getLinkPos() {
			return PartStarlightP2PTunnel.this.getHost().getTile().getPos();
		}

		@Nullable
		@Override
		public String getUnLocalizedDisplayName() {
			return "part_p2p_starlight";
		}

		@Override
		public void onLinkCreate(EntityPlayer entityPlayer, BlockPos other) {
			// Check if pos of other tile equal to our
			if(other.equals(getLinkPos())) return;

			// Create link, check if link created
			if(TransmissionNetworkHelper.createTransmissionLink(this, other)) {
				// Check if positions not contains other
				if(!this.positions.contains(other)) {
					// Add other to positions
					this.positions.add(other);

					// Notify chunk about host update
					getHost().markForUpdate();
				}
			}
		}

		@Override
		public boolean tryLink(EntityPlayer entityPlayer, BlockPos other) {
			// Check if pos of other not equal to our pos and we can create new link
			return !other.equals(getLinkPos()) && TransmissionNetworkHelper.canCreateTransmissionLink(this, other);
		}

		@Override
		public boolean tryUnlink(EntityPlayer entityPlayer, BlockPos other) {
			// Check if pos of other tile equal to our
			if(other.equals(getLinkPos())) return false;

			// Check if this has link with other
			if(TransmissionNetworkHelper.hasTransmissionLink(this, other)) {
				// Remove link
				TransmissionNetworkHelper.removeTransmissionLink(this, other);

				// Notify chunk about host update
				getHost().markForUpdate();

				// Removed other to positions
				this.positions.remove(other);

				return true;
			}
			return false;
		}

		@Override
		public List<BlockPos> getLinkedPositions() {
			return positions;
		}
	}

	private class InputStarlightHandler extends StarlightLinkable implements IStarlightTransmission {
		@Nonnull
		@Override
		public BlockPos getTrPos() {
			return getLinkPos();
		}

		@Nonnull
		@Override
		public World getTrWorld() {
			return getLinkWorld();
		}

		@Nonnull
		@Override
		public IPrismTransmissionNode provideTransmissionNode(BlockPos outerPos) {
			return new InputStarlightNode(outerPos);
		}
	}

	private class OutputStarlightHandler extends StarlightLinkable implements IStarlightTransmission {
		@Nonnull
		@Override
		public BlockPos getTrPos() {
			return getLinkPos();
		}

		@Nonnull
		@Override
		public World getTrWorld() {
			return getLinkWorld();
		}

		@Nonnull
		@Override
		public IPrismTransmissionNode provideTransmissionNode(BlockPos outerPos) {
			return new OutputStarlightNode(outerPos);
		}
	}

	private InputStarlightHandler inputHandler = new InputStarlightHandler();
	private OutputStarlightHandler outputHandler = new OutputStarlightHandler();

	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_STARLIGHT.getFirstModel());

	public PartStarlightP2PTunnel(ItemStack is) {
		super(is);
	}

	private StarlightLinkable getHandler() {
		// Check if part is output
		if (isOutput())
			return outputHandler;
		else
			return inputHandler;
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}

	@Override
	public World getLinkWorld() {
		// Pass call to handler
		return getHandler().getLinkWorld();
	}

	@Override
	public BlockPos getLinkPos() {
		// Pass call to handler
		return getHandler().getLinkPos();
	}

	@Nullable
	@Override
	public String getUnLocalizedDisplayName() {
		// Pass call to handler
		return getHandler().getUnLocalizedDisplayName();
	}

	@Override
	public void onLinkCreate(EntityPlayer entityPlayer, BlockPos blockPos) {
		// Pass call to handler
		getHandler().onLinkCreate(entityPlayer, blockPos);
	}

	@Override
	public boolean tryLink(EntityPlayer entityPlayer, BlockPos blockPos) {
		// Pass call to handler
		return getHandler().tryLink(entityPlayer, blockPos);
	}

	@Override
	public boolean tryUnlink(EntityPlayer entityPlayer, BlockPos blockPos) {
		// Pass call to handler
		return getHandler().tryUnlink(entityPlayer, blockPos);
	}

	@Override
	public List<BlockPos> getLinkedPositions() {
		// Pass call to handler
		return getHandler().getLinkedPositions();
	}

	@Nonnull
	@Override
	public BlockPos getTrPos() {
		// Pass call to handler
		return getHandler().getTrPos();
	}

	@Nonnull
	@Override
	public World getTrWorld() {
		// Pass call to handler
		return getHandler().getTrWorld();
	}

	@Nonnull
	@Override
	public IPrismTransmissionNode provideTransmissionNode(BlockPos blockPos) {
		// Pass call to handler
		return getHandler().provideTransmissionNode(blockPos);
	}
}