package AppliedIntegrations.Gui;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPartGui {

    // ********************************************Getters******************************************** //
    int getX();
    int getY();
    int getZ();

    ForgeDirection getSide();
    World getWorld();

    // ********************************************Setters******************************************** //
    void setX(int x);
    void setY(int y);
    void setZ(int z);

    void setSide(ForgeDirection side);
    void setWorld(World w);

}
