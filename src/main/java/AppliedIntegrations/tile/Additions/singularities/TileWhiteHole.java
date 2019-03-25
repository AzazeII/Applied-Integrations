package AppliedIntegrations.tile.Additions.singularities;

import AppliedIntegrations.API.ISingularity;
import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileWhiteHole extends TileEntity implements ISingularity {

    public long mass;
    private IItemList<?> itemList;

    public TileWhiteHole(){
        mass = (long)(Math.random() * 2048);
    }

    public double getHoleRadius() {
        double lightSpeed = 3;
        return Math.max(Math.cbrt(Math.cbrt(2 * 6.7 * mass / Math.pow(lightSpeed, 2))), 0.3);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
        return false;
    }

    @Override
    public void addMass(long l) {
        mass -= l;
    }

    @Override
    public void addStack(IAEStack<?> stack) {

    }

    @Override
    public IItemList<?> getList(Class<?> stackClassOperated) {
        IStorageChannel chan = AEApi.instance().storage().getStorageChannel((Class)stackClassOperated);

        // Check channel
        if(chan == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class))
            // Return list
            return itemList;
        return null;
    }
}
