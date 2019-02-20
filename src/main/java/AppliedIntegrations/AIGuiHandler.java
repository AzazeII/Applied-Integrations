package AppliedIntegrations;

import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Container.ContainerEnergyStorage;
import AppliedIntegrations.Container.ContainerEnergyTerminal;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Container.Server.ContainerMEServer;
import AppliedIntegrations.Gui.GuiEnergyIO;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyStoragePart;
import AppliedIntegrations.Gui.GuiEnergyTerminalDuality;
import AppliedIntegrations.Parts.Energy.*;
import AppliedIntegrations.tile.TileEnergyInterface;
import akka.japi.Pair;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;
import appeng.tile.networking.TileCableBus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class AIGuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        for(GuiEnum guiEnum : GuiEnum.values()){
            if(guiEnum.ID == ID){
                return guiEnum.GetServerGuiElement(ID, player, world, x, y, z, guiEnum.isPart);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        for(GuiEnum guiEnum : GuiEnum.values()){
            if(guiEnum.ID == ID){
                return guiEnum.GetClientGuiElement(ID, player, world, x, y, z, guiEnum.isPart);
            }
        }
        return null;
    }
}
