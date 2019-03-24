package AppliedIntegrations.tile.Additions.storage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileWhiteHole extends TileEntity {

    public double mass;

    public TileWhiteHole(){
        mass = Math.random() * 2048;
    }

    public double getHoleRadius() {
        double lightSpeed = 3;
        return Math.max(Math.cbrt(Math.cbrt(2 * 6.7 * mass / Math.pow(lightSpeed, 2))), 0.3);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
        return false;
    }
}
