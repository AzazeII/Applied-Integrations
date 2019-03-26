package AppliedIntegrations.tile.Additions;

import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketVectorSync;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.entities.EntityBlackHole;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.*;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.util.LookDirection;
import appeng.util.Platform;
import appeng.util.item.ItemList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.Collections.singletonList;
import static net.minecraft.util.EnumFacing.*;

public class TileMETurretFoundation extends AITile implements ICellContainer {

    private enum Ammo{
        MatterBall(1),
        PaintBall(1),
        Singularity(25);

        int cooldown;

        Ammo(int cooldown){
            this.cooldown = cooldown;
        }
    }

    // Direction for rendering turret tower
    public BlockPos renderingDirection = new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

    private ItemList storedAmmo = new ItemList();

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.of(DOWN);
    }

    public boolean activate(EnumHand hand, EntityPlayer p) {
        if(hand == EnumHand.MAIN_HAND) {
            // Call only on server
            if(!world.isRemote) {
                // Update only on server
                this.renderingDirection = p.getPosition();

                // Trace shot between this.pos and position, set by player
                RayTraceResult trace = world.rayTraceBlocks(new Vec3d(pos), new Vec3d(renderingDirection), true);

                // Set block
                world.setBlockState(trace.getBlockPos(), BlocksEnum.BlackHole.b.getDefaultState());

                // True result
                return true;
            }
        }
        return false;
    }

    @Override
    public void blinkCell(int i) {

    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {
        if(!getGridNode().isActive())
            return new ArrayList<>();

        if(iStorageChannel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)){
           return singletonList(new IMEInventoryHandler<IAEItemStack>() {

               @Override
               public IAEItemStack injectItems(IAEItemStack input, Actionable actionable, IActionSource iActionSource) {
                   // Check not null
                   if( input == null )
                       return null;

                   // Check stack size
                   if( input.getStackSize() == 0 )
                       return null;

                   // Check can accept
                   if( !canAccept(input) )
                       return input;

                   // Modulate inject
                   if(actionable == Actionable.MODULATE){
                       // Add stack
                       storedAmmo.add(input);
                   }

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
                   return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
               }

               @Override
               public AccessRestriction getAccess() {
                   return AccessRestriction.READ_WRITE;
               }

               @Override
               public boolean isPrioritized(IAEItemStack iaeItemStack) {
                   return false;
               }

               @Override
               public boolean canAccept(IAEItemStack iaeItemStack) {

                   // Get item stack's item
                   Item item = iaeItemStack.getItem();

                   return item == AEApi.instance().definitions().materials().singularity().maybeItem().get() ||
                           item == AEApi.instance().definitions().materials().matterBall().maybeItem().get();
               }

               @Override
               public int getPriority() {
                   // TODO: 2019-03-25 Add priority
                   return 0;
               }

               @Override
               public int getSlot() {
                   // Ignored
                   return 0;
               }

               @Override
               public boolean validForPass(int i) {
                   // Ignored
                   return true;
               }
           });
        }

        return new ArrayList<>();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {

    }
}
