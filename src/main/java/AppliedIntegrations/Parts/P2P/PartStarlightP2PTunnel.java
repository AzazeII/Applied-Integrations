package AppliedIntegrations.Parts.P2P;


import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.CrystalTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.base.TileTransmissionBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @Author Azazell
 */
public class PartStarlightP2PTunnel extends AIPartP2PTunnel<PartStarlightP2PTunnel> implements IStarlightTransmission, ILinkableTile{
	//private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_STARLIGHT.getFirstModel());

	private final TileTransmissionBase handler = new TileTransmissionBase() {
		@Nonnull
		@Override
		public String getUnLocalizedDisplayName() {
			return "part.p2p.starlight.name";
		}

		@Nonnull
		@Override
		public IPrismTransmissionNode provideTransmissionNode(BlockPos blockPos) {
			return new CrystalTransmissionNode(blockPos, new CrystalProperties(400, 100, 100));
		}

		@Override
		public void update() {

		}
	};

	public PartStarlightP2PTunnel(ItemStack is) {
		super(is);
	}

	/*@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}*/

	@Override
	public World getLinkWorld() {
		return handler.getLinkWorld();
	}

	@Override
	public BlockPos getLinkPos() {
		return handler.getLinkPos();
	}

	@Nullable
	@Override
	public String getUnLocalizedDisplayName() {
		return handler.getUnLocalizedDisplayName();
	}

	@Override
	public void onLinkCreate(EntityPlayer entityPlayer, BlockPos blockPos) {
		handler.onLinkCreate(entityPlayer, blockPos);
	}

	@Override
	public boolean tryLink(EntityPlayer entityPlayer, BlockPos blockPos) {
		return handler.tryLink(entityPlayer, blockPos);
	}

	@Override
	public boolean tryUnlink(EntityPlayer entityPlayer, BlockPos blockPos) {
		return handler.tryUnlink(entityPlayer, blockPos);
	}

	@Override
	public List<BlockPos> getLinkedPositions() {
		return handler.getLinkedPositions();
	}

	@Nonnull
	@Override
	public BlockPos getTrPos() {
		return handler.getTrPos();
	}

	@Nonnull
	@Override
	public World getTrWorld() {
		return handler.getTrWorld();
	}

	@Nonnull
	@Override
	public IPrismTransmissionNode provideTransmissionNode(BlockPos blockPos) {
		return handler.provideTransmissionNode(blockPos);
	}
}