package AppliedIntegrations.Gui;

import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Container.part.ContainerEnergyStorage;
import AppliedIntegrations.Container.part.ContainerPartEnergyIOBus;
import AppliedIntegrations.Container.tile.ContainerLogicBus;
import AppliedIntegrations.Gui.Part.GuiEnergyIO;
import AppliedIntegrations.Gui.Part.GuiEnergyInterface;
import AppliedIntegrations.Gui.Part.GuiEnergyStoragePart;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.*;
import AppliedIntegrations.Tile.LogicBus.TileLogicBusCore;
import appeng.api.util.AEPartLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class AIGuiHandler implements IGuiHandler {
    public enum GuiEnum {
        GuiInterfacePart,
        GuiInterfaceTile,
        GuiStoragePart,
        GuiServerStorage,
        GuiTerminalPart,
        GuiTerminalSecurity,
        GuiLogicBus,
        GuiIOPart;
    }

    public static void open(GuiEnum gui, EntityPlayer player, AEPartLocation side, BlockPos pos){
        if (player == null)
            throw new IllegalStateException("Null player. Is it server side?");
        if (pos == null)
            throw new IllegalStateException("Null pos. Is it server side?");
        if (gui == null)
            throw new IllegalStateException("How can gui handler open null gui?");
        if (side == null)
            throw new IllegalStateException("Null part location, Is it server side?");

        player.openGui(AppliedIntegrations.instance, concat(gui, side), player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Compress two values in one, using binary operator <<
     *
     * Ex:
     *  gui = GuiInterfaceTile
     *  side = INTERNAL
     *
     *  gui.ordinal = 1
     *  side.ordinal = 7
     *
     *  1.bin = 1
     *  7.bin = 111
     *
     *  1 << 4 = 10000
     *  10000 | 7
     *
     *  10000
     *    111
     *  10111
     *
     *  returns 10111 or 23
     */
    public static int concat(GuiEnum gui, AEPartLocation side){
        return (gui.ordinal() << 3) | side.ordinal();
    }

    /**
     * decode gui from @Link(concat)
     *
     * Ex:
     *  value = 23
     *
     *  23 is 10111 in bin. system
     *  10111 >> 4 = 1
     *  return GuiEnum.values()[1];
     */
    public static GuiEnum getGui(int value){
        return GuiEnum.values()[value >> 3];
    }

    /**
     * decode side from @Link(concat)
     *
     * Ex:
     *  value = 23
     *  23 is 10111 in bin. system
     *  7(number of values in AEPartLocation) is 111 in bin. system
     *
     *  23 & 7 is 10111 & 111
     *
     *  10111
     *    111
     *    111
     * return AEPartLocation.values()[7];
     */
    public static AEPartLocation getSide(int value){
        return AEPartLocation.fromOrdinal(value & 7);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        AEPartLocation side = getSide(ID);
        GuiEnum gui = getGui(ID);

        // Energy interface container
        if(gui == GuiEnum.GuiInterfacePart){
            PartEnergyInterface part = (PartEnergyInterface)Utils.getPartByParams(new BlockPos(x,y,z), side.getFacing(), world);

            return new ContainerEnergyInterface(player, part);
        }else if(gui == GuiEnum.GuiLogicBus){
            // Find Tile candidate for core
            TileEntity maybeCore = world.getTileEntity(new BlockPos(x,y,z));
            // Check if it is logic bus core
            if(maybeCore instanceof TileLogicBusCore){
                return new ContainerLogicBus(player, (TileLogicBusCore)maybeCore);
            }
        }else if(gui == GuiEnum.GuiIOPart){
            AIOPart part = (AIOPart)Utils.getPartByParams(new BlockPos(x,y,z), side.getFacing(), world);

            return new ContainerPartEnergyIOBus(part, player);
        }else if(gui == GuiEnum.GuiStoragePart){
            PartEnergyStorage part = (PartEnergyStorage)Utils.getPartByParams(new BlockPos(x,y,z), side.getFacing(), world);

            return new ContainerEnergyStorage(part, player);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        AEPartLocation side = getSide(ID);
        GuiEnum gui = getGui(ID);

        // Energy interface gui
        if(gui == GuiEnum.GuiInterfacePart){
            PartEnergyInterface part = (PartEnergyInterface)Utils.getPartByParams(new BlockPos(x,y,z), side.getFacing(), world);

            return new GuiEnergyInterface((ContainerEnergyInterface)getServerGuiElement(ID, player, world, x, y, z), part, player);
        }else if(gui == GuiEnum.GuiLogicBus){
            // Find Tile candidate for core
            TileEntity maybeCore = world.getTileEntity(new BlockPos(x,y,z));
            // Check if it is logic bus core
            if(maybeCore instanceof TileLogicBusCore){
                return new GuiLogicBus(player, (TileLogicBusCore)maybeCore, (ContainerLogicBus)
                        getServerGuiElement(ID, player, world, x,y,z));
            }
        }else if(gui == GuiEnum.GuiIOPart){
            return new GuiEnergyIO((ContainerPartEnergyIOBus)getServerGuiElement(ID, player,world, x, y, z),
                    player);
        }else if(gui == GuiEnum.GuiStoragePart){
            PartEnergyStorage part = (PartEnergyStorage)Utils.getPartByParams(new BlockPos(x,y,z), side.getFacing(), world);

            return new GuiEnergyStoragePart((ContainerEnergyStorage)getServerGuiElement(ID, player, world, x, y, z),
                    part, player);
        }

        return null;
    }
}
