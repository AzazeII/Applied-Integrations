package AppliedIntegrations.Gui;

import AppliedIntegrations.API.Parts.AIPart;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPartGui {
    int getX();
    int getY();
    int getZ();

    ForgeDirection getSide();
    World getWorld();
}
