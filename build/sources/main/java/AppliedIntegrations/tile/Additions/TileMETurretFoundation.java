package AppliedIntegrations.tile.Additions;

import AppliedIntegrations.Blocks.Additions.BlockBlackHole;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketSingularitySync;
import AppliedIntegrations.Network.Packets.PacketVectorSync;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import AppliedIntegrations.tile.entities.EntityBlackHole;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.*;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEColor;
import appeng.api.util.AEColoredItemDefinition;
import appeng.core.features.ColoredItemDefinition;
import appeng.core.features.ItemDefinition;
import appeng.me.helpers.MachineSource;
import appeng.util.LookDirection;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static java.util.Collections.singletonList;
import static net.minecraft.util.EnumFacing.*;

public class TileMETurretFoundation extends AITile implements ICellContainer, IGridTickable {

    private boolean syncActive = false;

    // All ammo types
    private enum Ammo{
        MatterBall(1, AEApi.instance().definitions().materials().matterBall()),
        PaintBall(1, AEApi.instance().definitions().items().coloredPaintBall()),
        Singularity(25, AEApi.instance().definitions().materials().qESingularity());

        AEColoredItemDefinition coloredDefinition;
        IItemDefinition definition;

        final int cooldown;

        Ammo(int cooldown, IItemDefinition definition) {
            this.cooldown = cooldown;
            this.definition = definition;
        }

        Ammo(int cooldown, AEColoredItemDefinition definition) {
            this.cooldown = cooldown;
            this.coloredDefinition = definition;
        }

        boolean isColored(){
            return coloredDefinition != null;
        }
    }

    // Direction for rendering turret tower
    public BlockPos renderingDirection = new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

    private ItemList storedAmmo = new ItemList();

    private Map<Ammo, TimeHandler> cooldownHandlers = new LinkedHashMap<>();

    public TileMETurretFoundation(){
        // Add cooldown handler for each ammo
        for(Ammo ammo : Ammo.values()){
            // Put new handler
            cooldownHandlers.put(ammo, new TimeHandler());
        }
    }

    @Nonnull
    @Override
    public TickingRequest getTickingRequest(@Nonnull IGridNode iGridNode) {
        return new TickingRequest(5, 10, false, false);
    }


    /*
        Simulation:
        Let's say "#" is turret, and "-->" is vector of it's ray. B is block
        # will wait for not AIR B on -->, and then shoot at -->

        it will look like something like this
        i = 0) #     B
        i = 1) #->   B
        i = 2) #-->  B
        i = 3) #---> B
        i = 5) #---->B
     */
    public void shoot(Ammo ammo){
        // Check if i colored
        if(ammo.isColored()){
            // TODO: 2019-03-28 Shoot colored paint balls
        // else
        }else{
            // Check if active
            if(!gridNode.isActive())
                return;

            // Inner handler
            IMEInventoryHandler<IAEItemStack> handler = (IMEInventoryHandler<IAEItemStack>) getCellArray(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)).get(0);

            if (!ammo.isColored()) {
                // Simulate extraction
                IAEItemStack extracted = handler.extractItems(AEItemStack.fromItemStack(new ItemStack(ammo.definition.maybeItem().get())), Actionable.SIMULATE, new MachineSource(this));

                // Check not null
                if(extracted == null)
                    return;

                // Get vector from given block pos
                Vec3d vec = new Vec3d(renderingDirection);

                // Subtract this.pos from vector's pos
                vec.subtract(new Vec3d(this.getPos()));

                // Normalize vector (make it's length be equal to 1)
                vec.normalize();

                // Current block on current vector
                Block b = world.getBlockState(new BlockPos(vec)).getBlock();

                // Iterate on 50 blocks forward
                for (int i = 0; i < 50; i++) {
                    if (!(b instanceof BlockAir)) {
                        // Break, if block is not air, and place singularity
                        if (ammo == Ammo.Singularity)
                            // Create black hole
                            world.setBlockState(new BlockPos(vec), BlocksEnum.BlackHole.b.getDefaultState());
                        // break
                        break;
                    } else {
                        // Normalize vector
                        vec.normalize();

                        // Multiply vector by i + 1
                        vec = new Vec3d(vec.x * i + 1, vec.y * i + 1, vec.z * i + 1);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int ticksSinceLastCall) {
        // Call only on server
        if(!world.isRemote) {
            // Check if node was active
            if (!syncActive && getGridNode().isActive()) {
                // Node wasn't active, but now it is active
                // Fire new cell array update event!
                postCellEvent(new MENetworkCellArrayUpdate());
                // Update sync
                syncActive = true;
            } else if (syncActive && !getGridNode().isActive()) {
                // Node was active, but now it not
                // Fire new cell array update event!
                postCellEvent(new MENetworkCellArrayUpdate());
                // Update sync
                syncActive = false;
            }

            // Iterate over ammo
            for(Ammo ammo : Ammo.values()){
                // Pass ammo to shoot function
                shoot(ammo);
            }
        }
        return TickRateModulation.SLOWER;
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.of(DOWN);
    }

    public boolean activate(EnumHand hand, EntityPlayer p) {
        // Only call when player clicking with right hand
        if(hand == EnumHand.MAIN_HAND) {
            // Call only on server
            if(!world.isRemote) {
                if(p.isSneaking()) {
                    // Update only on server
                    this.renderingDirection = p.getPosition();

                    // Notify client
                    NetworkHandler.sendToAllInRange(new PacketVectorSync(this.renderingDirection, this.getPos()),
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));

                    // True result
                    return true;
                }else{
                    // Shoot ammo
                    shoot(Ammo.Singularity);

                    return true;
                }
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
                   // Get pointer value of this stack
                   IAEItemStack pStack = storedAmmo.findPrecise(iaeItemStack);

                   // Check not null
                   if(pStack != null) {
                       // Create copy
                       IAEItemStack Return = pStack.copy();

                       // get stack size
                       long size = pStack.getStackSize();

                       // Check size greater than 0
                       if (size <= 0) {
                           return null;
                       }

                       // Check if size greater then requested
                       if (pStack.getStackSize() <= size) {
                           // Set stack size to current stack size
                           Return.setStackSize(pStack.getStackSize());

                           // Modulate extraction
                           if (actionable == Actionable.MODULATE)
                               // Decrease current stack size
                               pStack.setStackSize(0);
                       } else {
                           // Set stack size to #Var: size
                           Return.setStackSize(size);

                           // Modulate extraction
                           if (actionable == Actionable.MODULATE)
                               // Set current stack size to current stack size - #Var: size
                               pStack.setStackSize(pStack.getStackSize() - size);
                       }

                       // Update array
                       postCellEvent(new MENetworkCellArrayUpdate());

                       // Return stack
                       return Return;
                   }

                   // Nothing extracted
                   return null;
               }

               @Override
               public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
                   // Add all stack from stored ammo
                   for(IAEItemStack stack : storedAmmo)
                       iItemList.add(stack);

                   return iItemList;
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
                   // Iterate over ammo values
                   for (Ammo ammo : Ammo.values()){
                       // Check if ammo colored
                       if(ammo.isColored()){
                           // Iterate over al colors
                           for(AEColor color : AEColor.values()) {
                               // Return true if stacks are same
                               return ammo.coloredDefinition.sameAs(color, iaeItemStack.getDefinition());
                           }
                       }

                       // Return true if stacks are same
                       return ammo.definition.isSameAs(iaeItemStack.getDefinition());
                   }

                   // False
                   return false;
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
        // Check if inventory not null
        if (iCellInventory != null)
            // Persist inventory
            iCellInventory.persist();
        // Mark dirty
        world.markChunkDirty(pos, this);
    }
}
