package AppliedIntegrations.Gui.ServerGUI.SubGui;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

import static java.lang.Float.NaN;

/**
 * @Author Azazell
 */
public enum PortDirections {
    S(1,0),
    N(-1,0),
    E(1,1),
    W(-1,-1),
    U(-1,1),
    D(1,-1),
    // Stands for "Not a direction"
    NaD(0,0);

    public int offsetX, offsetY;

    PortDirections(int x,int y){
        this.offsetX =x;
        this.offsetY =y;
    }


    @Nonnull
    public static PortDirections fromFacing(EnumFacing dir) {
        switch (dir){
            case EAST:
                return PortDirections.E;
            case WEST:
                return PortDirections.W;
            case SOUTH:
                return PortDirections.S;
            case DOWN:
                return PortDirections.D;
            case NORTH:
                return PortDirections.N;
            case UP:
                return PortDirections.U;
        }
        return PortDirections.NaD;
    }
}
