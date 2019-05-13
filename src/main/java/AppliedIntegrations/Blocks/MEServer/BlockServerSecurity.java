package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import appeng.util.Platform;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * @Author Azazell
 */
public class BlockServerSecurity extends BlockAIRegistrable implements ITileEntityProvider {
    public BlockServerSecurity(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        // Make block invisible, and give all render handling to TESR
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public TileEntity createNewTileEntity(@Nullable World p_149915_1_, int p_149915_2_) {
        return new TileServerSecurity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world,pos,state, p, hand, facing, hitX, hitY, hitZ);

        // Check if player not sneaking
        if (!p.isSneaking()) {
            // Get stack
            ItemStack stack = p.getHeldItem(hand);

            // Get tile
            TileServerSecurity tile = (TileServerSecurity) world.getTileEntity(pos);

            // Check if stack is wrench
            if (Platform.isWrench(p, stack, pos)){
                // Rotate up of tile around
                tile.rotateForward(facing);
            } else {
                // Call only on server
                if (world.isRemote)
                    // Skip client
                    return false;

                // Check if tile is null
                if (world.getTileEntity(pos) != null) {
                    // Check if tile has no master
                    if (tile == null || !tile.hasMaster()) return false;

                    // Open gui
                    AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiServerTerminal, p, INTERNAL, pos);

                    // Request update
                    tile.updateRequested = true;

                    return true;
                }
            }
        }
        return false;
    }

}
