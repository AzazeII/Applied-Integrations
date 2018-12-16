package AppliedIntegrations.Container.Server;

import AppliedIntegrations.API.AppliedCoord;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Entities.Server.TileServerSecurity;
import AppliedIntegrations.Utils.AILog;
import appeng.api.util.WorldCoord;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;
import java.util.Vector;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

public class ContainerServerPacketTracer extends AIContainer {

    public TileServerCore tile;

    public ContainerServerPacketTracer(TileServerCore instance,EntityPlayer player) {
        super(player);
    }

    @Override
    public boolean onFilterReceive(AIPart part) {
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
    @Override
    public void onContainerClosed( @Nonnull final EntityPlayer player )
    {
       super.onContainerClosed(player);
    }
}
