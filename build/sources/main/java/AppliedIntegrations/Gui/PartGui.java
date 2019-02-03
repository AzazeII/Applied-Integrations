package AppliedIntegrations.Gui;

import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class PartGui extends AIBaseGui implements IPartGui{

    protected int x,y,z;
    protected EnumFacing dir;
    protected World w;

    public PartGui(Container container) {
        super(container);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public EnumFacing getSide() {
        return dir;
    }

    public World getWorld() {
        return w;
    }

    // ********************************************Setters******************************************** //
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setSide(EnumFacing side) {
        this.dir = side;
    }

    public void setWorld(World w) {
        this.w = w;
    }
}
