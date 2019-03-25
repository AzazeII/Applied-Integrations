package AppliedIntegrations.API;

import appeng.api.util.DimensionalCoord;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Iterator;

/**
 * @Author Azazell
 * Represents coordinates of any AE2 part
 */
@Deprecated
public class AppliedCoord extends DimensionalCoord{

    public EnumFacing side;

    public AppliedCoord(DimensionalCoord coord, EnumFacing side){
        this(coord.getWorld(),coord.x,coord.y,coord.z,side);
    }
    public AppliedCoord(World _w, int _x, int _y, int _z, EnumFacing side) {
        super(_w,_x,_y,_z);
        this.side = side;
    }
    public AppliedCoord(World _w, int _x, int _y, int _z) {
        this(_w, _x, _y, _z, null);
    }

}
