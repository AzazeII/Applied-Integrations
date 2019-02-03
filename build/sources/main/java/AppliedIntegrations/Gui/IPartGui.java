package AppliedIntegrations.Gui;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IPartGui {

    // ********************************************Getters******************************************** //
    int getX();
    int getY();
    int getZ();

    EnumFacing getSide();
    World getWorld();

    // ********************************************Setters******************************************** //
    void setX(int x);
    void setY(int y);
    void setZ(int z);

    void setSide(EnumFacing side);
    void setWorld(World w);

}
