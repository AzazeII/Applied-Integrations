package AppliedIntegrations.Gui;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerAIPriority;
import AppliedIntegrations.Container.part.*;
import AppliedIntegrations.Container.tile.ContainerLogicBus;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerTerminal;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Gui.MultiController.GuiMultiControllerCore;
import AppliedIntegrations.Gui.MultiController.GuiMultiControllerTerminal;
import AppliedIntegrations.Gui.Part.*;
import AppliedIntegrations.Gui.Part.Interaction.GuiInteractionBus;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.PartEnergyFormation;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static AppliedIntegrations.Gui.AIGuiHandler.GuiEnum.*;

/**
 * @Author Azazell
 */
public class AIGuiHandler implements IGuiHandler {
	public enum GuiEnum {
		GuiInterface,
		GuiStoragePart,
		GuiInteraction,
		GuiServerStorage,
		GuiTerminalPart,
		GuiServerTerminal,
		GuiLogicBus,
		GuiAIPriority,
		GuiIOPart,
		GuiFormationPlane
	}

	public static void open(@Nonnull GuiEnum gui, @Nonnull EntityPlayer player, @Nonnull AEPartLocation side, @Nonnull BlockPos pos) {
		// Ignore on client
		if( Platform.isClient() ) {
			return;
		}

		player.openGui(AppliedIntegrations.instance, concat(gui, side), player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Compress two values in one, using binary operator <<
	 * <p>
	 * Ex:
	 * gui = GuiInterfaceTile
	 * side = INTERNAL
	 * <p>
	 * gui.ordinal = 1
	 * side.ordinal = 7
	 * <p>
	 * 1.bin = 1
	 * 7.bin = 111
	 * <p>
	 * 1 << 4 = 10000
	 * 10000 | 7
	 * <p>
	 * 10000
	 * 111
	 * 10111
	 * <p>
	 * returns 10111 or 23
	 */
	public static int concat(GuiEnum gui, AEPartLocation side) {
		return (gui.ordinal() << 3) | side.ordinal();
	}

	/**
	 * decode gui from @Link(concat)
	 * <p>
	 * Ex:
	 * value = 23
	 * <p>
	 * 23 is 10111 in bin. system
	 * 10111 >> 4 = 1
	 * return GuiEnum.values()[1];
	 */
	private static GuiEnum getGui(int value) {

		return GuiEnum.values()[value >> 3];
	}

	/**
	 * decode side from @Link(concat)
	 * <p>
	 * Ex:
	 * value = 23
	 * 23 is 10111 in bin. system
	 * 7(number of values in AEPartLocation) is 111 in bin. system
	 * <p>
	 * 23 & 7 is 10111 & 111
	 * <p>
	 * 10111
	 * 111
	 * 111
	 * return AEPartLocation.values()[7];
	 */
	private static AEPartLocation getSide(int value) {

		return AEPartLocation.fromOrdinal(value & 7);
	}

	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		AEPartLocation side = getSide(ID);
		GuiEnum gui = getGui(ID);

		if (gui == GuiEnum.GuiInterface) {
			ISyncHost host = Utils.getSyncHostByParams(new BlockPos(x, y, z), side, world);

			return new ContainerEnergyInterface(player, (IEnergyInterface) host);
		} else if (gui == GuiEnum.GuiLogicBus) {
			TileEntity maybeCore = world.getTileEntity(new BlockPos(x, y, z));
			if (maybeCore instanceof TileLogicBusCore) {
				return new ContainerLogicBus(player, (TileLogicBusCore) maybeCore);
			}
		} else if (gui == GuiEnum.GuiIOPart) {
			AIOPart part = (AIOPart) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new ContainerPartEnergyIOBus(part, player);
		} else if (gui == GuiEnum.GuiStoragePart) {
			PartEnergyStorage part = (PartEnergyStorage) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new ContainerEnergyStorage(part, player);
		} else if (gui == GuiTerminalPart) {
			PartEnergyTerminal part = (PartEnergyTerminal) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new ContainerEnergyTerminal(part, player);
		} else if (gui == GuiAIPriority) {
			IPriorityHostExtended host = (IPriorityHostExtended) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new ContainerAIPriority(player.inventory, host);
		} else if (gui == GuiServerTerminal) {
			TileMultiControllerTerminal terminal = (TileMultiControllerTerminal) Utils.getTileByParams(new BlockPos(x, y, z), world);

			return new ContainerMultiControllerTerminal(terminal, player);
		} else if (gui == GuiServerStorage) {
			TileMultiControllerCore core = (TileMultiControllerCore) Utils.getTileByParams(new BlockPos(x, y, z), world);

			return new ContainerMultiControllerCore(player, core);
		} else if (gui == GuiInteraction) {
			PartInteraction interaction = (PartInteraction) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new ContainerInteractionBus(player, interaction);
		} else if (gui == GuiFormationPlane) {
			PartEnergyFormation plane = (PartEnergyFormation) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new ContainerEnergyFormation(plane, player);
		}

		return null;
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		AEPartLocation side = getSide(ID);
		GuiEnum gui = getGui(ID);

		// Energy interface gui
		if (gui == GuiEnum.GuiInterface) {
			return new GuiEnergyInterface((ContainerEnergyInterface) getServerGuiElement(ID, player, world, x, y, z), player);
		} else if (gui == GuiEnum.GuiLogicBus) {
			TileEntity maybeCore = world.getTileEntity(new BlockPos(x, y, z));
			if (maybeCore instanceof TileLogicBusCore) {
				return new GuiLogicBus(player, (TileLogicBusCore) maybeCore, (ContainerLogicBus) getServerGuiElement(ID, player, world, x, y, z));
			}
		} else if (gui == GuiEnum.GuiIOPart) {
			return new GuiEnergyIO((ContainerPartEnergyIOBus) getServerGuiElement(ID, player, world, x, y, z), player);
		} else if (gui == GuiEnum.GuiStoragePart) {
			PartEnergyStorage part = (PartEnergyStorage) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new GuiEnergyStoragePart((ContainerEnergyStorage) getServerGuiElement(ID, player, world, x, y, z), part, player);
		} else if (gui == GuiTerminalPart) {
			PartEnergyTerminal part = (PartEnergyTerminal) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new GuiEnergyTerminalDuality((ContainerEnergyTerminal) getServerGuiElement(ID, player, world, x, y, z), part, player);
		} else if (gui == GuiAIPriority) {
			IPriorityHostExtended host = (IPriorityHostExtended) Utils.getPartByParams(new BlockPos(x, y, z), side.getFacing(), world);

			return new GuiPriorityAI(player.inventory, host);
		} else if (gui == GuiServerTerminal) {
			return new GuiMultiControllerTerminal((ContainerMultiControllerTerminal) getServerGuiElement(ID, player, world, x, y, z), player);
		} else if (gui == GuiServerStorage) {
			return new GuiMultiControllerCore((ContainerMultiControllerCore) getServerGuiElement(ID, player, world, x, y, z), player);
		} else if (gui == GuiInteraction) {
			return new GuiInteractionBus((ContainerInteractionBus) getServerGuiElement(ID, player, world, x, y, z), player);
		}else if (gui == GuiFormationPlane) {
			return new GuiEnergyFormation((ContainerEnergyFormation) getServerGuiElement(ID, player, world, x, y, z), player);
		}

		return null;
	}
}
