package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.AIMultiBlockTile;
import appeng.api.AEApi;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class TileLogicBusSlave extends AIMultiBlockTile {
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
        // Check if it's wrench
        if(Platform.isWrench(p, p.getHeldItem(hand), pos)){
            // Destroy tile
            if(p.isSneaking()){
                final List<ItemStack> itemsToDrop = Lists.newArrayList( new ItemStack(world.getBlockState(pos).getBlock() ));
                Platform.spawnDrops( world, pos, itemsToDrop );
                world.setBlockToAir( pos );
                return true;
            // Try to form logic bus
            }else{
                for(EnumFacing facing : EnumFacing.values()){
                    TileEntity candidate = world.getTileEntity(pos.offset(facing));
                    if(candidate instanceof TileLogicBusCore){
                        TileLogicBusCore core = (TileLogicBusCore)candidate;
                        core.tryConstruct(p);
                    }
                }
            }
        }
        return false;
    }

    public boolean tryConstruct() {

    }
}
