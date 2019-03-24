package AppliedIntegrations.tile.Additions;

import AppliedIntegrations.Blocks.Additions.BlockMETurretTower;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.tile.AITile;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;

import static net.minecraft.util.EnumFacing.*;

public class TileMETurretFoundation extends AITile implements IMEInventory<IAEItemStack> {

    public void invalidate() {
        super.invalidate();

        if(hasWorld() && !world.isRemote)
            // Destroy turret tower
            world.setBlockToAir(pos.offset(UP));
    }

    @Override
    public void update() {
        super.update();

        // Try to set crystal
        if (world.getBlockState(pos.offset(UP)).getBlock() == Blocks.AIR) {
            // Set pylon crystal
            world.setBlockState(pos.offset(UP), BlocksEnum.BTurretTower.b.getDefaultState());
        } else if (world.getBlockState(pos.offset(UP)).getBlock() != BlocksEnum.BTurretTower.b) {
            // Set block to air
            world.setBlockToAir(pos);

            // Create item's entity
            EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(BlocksEnum.BTurretTower.b));

            // Spawn item in world
            world.spawnEntity(item);
        }
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.of(DOWN);
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        return null;
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
        return null;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return null;
    }
}
