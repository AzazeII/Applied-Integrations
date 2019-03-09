package AppliedIntegrations.Parts.Botania;

import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class PartManaStorageBus extends PartEnergyStorage {
    public PartManaStorageBus() {
        super(PartEnum.ManaStorage, SecurityPermissions.INJECT, SecurityPermissions.EXTRACT);
    }

    @Override
    public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d position) {return false;}

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int TicksSinceLastCall )
    {
        // Keep chugging along
        return TickRateModulation.SAME;
    }

    @Nonnull
    @Override
    public IPartModel getStaticModels() {
        if (this.isPowered())
            if (this.isActive())
                return PartModelEnum.MANA_STORAGE_BUS_HAS_CHANNEL;
            else
                return PartModelEnum.MANA_STORAGE_BUS_ON;
        return PartModelEnum.MANA_STORAGE_BUS_OFF;
    }
}
