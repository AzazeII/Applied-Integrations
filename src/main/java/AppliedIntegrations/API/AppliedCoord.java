package AppliedIntegrations.API;

import appeng.api.util.DimensionalCoord;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Iterator;

/**
 * Represent coordinates of any AE2 part
 */
public class AppliedCoord extends DimensionalCoord{

    public ForgeDirection side;


    public AppliedCoord(DimensionalCoord coord, ForgeDirection side){
        this(coord.getWorld(),coord.x,coord.y,coord.z,side);
    }
    public AppliedCoord(World _w, int _x, int _y, int _z, ForgeDirection side) {
        super(_w,_x,_y,_z);
        this.side = side;
    }
    public AppliedCoord(World _w, int _x, int _y, int _z) {
        this(_w, _x, _y, _z,ForgeDirection.UNKNOWN);
    }

}
