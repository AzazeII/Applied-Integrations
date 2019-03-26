package AppliedIntegrations.tile.Additions.singularities;

import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.Blocks.Additions.BlockMEPylon;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import appeng.api.AEApi;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class TileWhiteHole extends TileEntity implements ISingularity, ITickable {

    public long mass;
    private IItemList<?> itemList;
    private TileBlackHole entangledHole = null;

    public TileWhiteHole(){
        mass = (long)(Math.random() * 1024);
    }

    @Override
    public void update() {
        if(!world.isRemote)
            // Try to find ME Pylon near
            checkBlocksNear();
    }

    private void checkBlocksNear() {
        double radius = getHoleRadius();

        // Iterate from -radius to radius x
        for (int x = -(int) radius; x < radius; x++) {
            // Iterate from -radius to radius y
            for (int y = -(int) radius; y < radius; y++) {
                // Iterate from -radius to radius z
                for (int z = -(int) radius; z < radius; z++) {
                    // Create pos
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);

                    // Get block
                    Block b = world.getBlockState(blockPos).getBlock();

                    // Check for ME Pylon
                    if(b instanceof BlockMEPylon){
                        // Get pylon's tile
                        TileMEPylon pylon = (TileMEPylon)world.getTileEntity(blockPos);

                        // Check not null
                        if(pylon != null && !pylon.hasSingularity())
                            // Set singularity to this
                            pylon.operatedTile = this;
                    }
                }
            }
        }

    }

    public double getHoleRadius() {
        double lightSpeed = 3;

        // White hole's mass is opposite of black hole's mass, so when black hole grow, then white hole shrink
        return mass * lightSpeed;
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

    @Override
    @SideOnly(CLIENT)
    public void setMassFromServer(long mass) {
        // Mass of white hole cannot growth at all
        this.mass -= mass;
    }

    @Override
    public long getMass() {
        return mass;
    }

    @Override
    public boolean isEntangled() {
        return entangledHole != null;
    }

    @Override
    public void setEntangledHole(ISingularity t) {
        entangledHole = (TileBlackHole)t;
    }
}
