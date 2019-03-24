package AppliedIntegrations.tile.Additions.storage;

import AppliedIntegrations.Blocks.Additions.BlockMEPylon;
import AppliedIntegrations.Blocks.Additions.BlockSingularity;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.grid.EnergyList;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.Additions.TimeHandler;
import AppliedIntegrations.tile.Additions.storage.SingularityInventoryHandler;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.*;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.block.networking.BlockCableBus;
import appeng.fluids.util.FluidList;
import appeng.util.item.ItemList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TileSingularity extends TileEntity implements ITickable {

    private final double gravitationalConst = 6.7;
    public long mass = (int)Math.ceil(Math.random() * 1024);
    private int MAX_BLOCKS_DESTROYED_PER_OPERATION = 4;

    private TimeHandler handler = new TimeHandler();

    @Override
    public void update() {
        if(!world.isRemote)
            // Modulate gravity
            modulateBlockGravity();
        modulateLivingGravity();
    }

    // returns forge-friendly representation of gravitation radius
    private AxisAlignedBB getAxisAABB(int modulateRadius) {
        // Square with x1,y1,z1 as negative modulate radius and coords
        // x2, y2, z2 as positive modulate radius and coords
        return new AxisAlignedBB(getPos().getX() - modulateRadius,
                                 getPos().getY() - modulateRadius,
                                 getPos().getZ() - modulateRadius,
                                 getPos().getX() + modulateRadius,
                                 getPos().getY() + modulateRadius,
                                 getPos().getZ() + modulateRadius);
    }

    // Destroys entity if it crossed getBlackHoleRadius()
    private boolean destroyWithCondition(Entity entity) {
        // Get position vector
        Vec3d pos = entity.getPositionVector();

        // Check if pos matches conditions
        if(pos.distanceTo(new Vec3d(getPos())) <= getBlackHoleRadius()) {
            // Kill entity
            entity.setDead();
            // Remove from world
            world.removeEntity(entity);
            // Increment hole's mass
            mass += 100;
            // Mark for update
            markDirty();
            // Notify client
            notifyClient();
            // return true, as entity was destroyed
            return true;
        }
        return false;
    }


    // Returns gravity force between two vectors, assuming posB mass is 1
    private double getGravityForceBetween(Vec3d posA, Vec3d posB){
        double radiusSquare = Math.pow(posA.squareDistanceTo(posB), 2);

        return (gravitationalConst * mass * 0.1 / radiusSquare);
    }

    // Pull all living creatures(entities) in range
    private void modulateLivingGravity() {
        int modulateRadius = (int)Math.ceil(getBlackHoleRadius()) * 2;

        // List of all entities in radius
        List<Entity> entitiesInRadius = world.getEntitiesWithinAABB(Entity.class, getAxisAABB(modulateRadius));

        for(Entity entity : entitiesInRadius){
            // Do not pull player
            if(entity instanceof EntityPlayer)
                continue;

            // Destroy entity, if it crossed radius
            if(destroyWithCondition(entity))
                continue;

            // Pos of our entity in vector representation
            Vec3d vectorPos = entity.getPositionVector();

            // Vector of gravitation; Normalized, means vector represents only direction, as length is not important
            // for us
            Vec3d gravitationDirection = new Vec3d(pos).subtract(vectorPos).normalize();

            // Get force of gravitation
            double force = getGravityForceBetween(new Vec3d(pos), vectorPos);

            // Multiply all directions by gravity force
            entity.addVelocity(gravitationDirection.x * force,
                               gravitationDirection.y * force,
                               gravitationDirection.z * force);
        }
    }

    // Pull all players in range ( yes, player is entity, but player exist on both sides, if
    // pull it on client, game will sync automatic )
    public void modulatePlayerGravity(){
        // Get player
        EntityPlayer p = Minecraft.getMinecraft().player;
        // Get gravitation range (much bigger than normal radius)
        double range = getBlackHoleRadius() * getBlackHoleRadius();
        // Get pos of this tile
        Vec3d blockPos = new Vec3d(getPos());
        // Get vector of entities' position
        Vec3d entityPos = p.getPositionVector();

        // Don't pull creative players
        if(p.isCreative())
            return;

        // Check if player is near black hole
        if (entityPos.squareDistanceTo(blockPos) < range) {
            pullEntity(p);
        }
    }

    // Pull all blocks in range
    private void modulateBlockGravity() {
        // Check if time since last modulation passed
        if(handler.hasTimePassed(world, 1)) {
            // Get block destruction range
            double range = getBlackHoleRadius() * 2;

            List<BlockPos> toBreak = new ArrayList<>();

            // Stage #1: Create "iteration square" and find block break candidates for next break stage
            // Iterate from -range to range x
            for (int x = -(int) range; x < range; x++) {
                // Iterate from -range to range y
                for (int y = -(int) range; y < range; y++) {
                    // Iterate from -range to range z
                    for (int z = -(int) range; z < range; z++) {
                        // Get pos
                        BlockPos blockPos = new BlockPos(pos.getX() + x,
                                pos.getY() + y,
                                pos.getZ() + z);

                        // Exclude this.pos
                        if (x == pos.getX() && pos.getY() == y && pos.getZ() == z)
                            continue;

                        // Get block at pos
                        Block b = world.getBlockState(blockPos).getBlock();

                        // Check if block equals this
                        if (b == world.getBlockState(pos).getBlock())
                            continue;

                        // Check if block is air
                        if (b instanceof BlockAir)
                            continue;

                        // Check if block is unbreakable
                        if (b.getBlockHardness(b.getDefaultState(), world, pos) == -1)
                            continue;

                        // Check if block is singularity
                        if (b instanceof BlockSingularity)
                            continue;

                        // Check if block is ME pylon
                        if (b instanceof BlockMEPylon) {
                            // Provided this, as singularity to pylon
                            TileMEPylon pylon = (TileMEPylon)world.getTileEntity(blockPos);

                            // Check if pylon not already operating black/white hole
                            if(pylon != null && !pylon.hasSingularity()) {
                                AILog.info("Setting operated tile of " + pos + " to this");
                                // Set tile to this
                                pylon.operatedTile = this;
                            }

                            // Update node info
                            pylon.updateNodeData();

                            continue;
                        }

                        // Also ignore Cable bus block, as it causes crash
                        if (b instanceof BlockCableBus)
                            continue;

                        // Add candidate
                        toBreak.add(blockPos);
                    }
                }
            }

            /**
             * // Stage #2: Compare all block positions
            Comparator<BlockPos> posComparator = (o1, o2) -> {
                // Get vector representation of positions
                Vec3d vectorA = new Vec3d(o1);
                Vec3d vectorB = new Vec3d(o2);

                // Get vector representation of pos of black hole center
                Vec3d vectorCenter = new Vec3d(pos);

                // Compare them by range to black hole center
                if(vectorA.distanceTo(vectorCenter) > vectorB.distanceTo(vectorCenter))
                    return -1;
                else if(vectorA.distanceTo(vectorA) < vectorB.distanceTo(vectorCenter))
                    return 1;
                return 0;
            };*/

            // Stage #2: Iterate over all block candidates and find blocks which can be destructed

            // Count destroyed blocks
            int counter = 0;

            for (BlockPos b : toBreak) {
                // Vector representation of blockpos
                Vec3d positionVectorA = new Vec3d(b);
                // Vector representation of this block pos
                Vec3d positionVectorB = new Vec3d(pos);

                // Check if pos not present this
                if (b.equals(pos))
                    continue;

                // Check if range to this block is less or equal to break range
                if (positionVectorA.distanceTo(positionVectorB) > range)
                    // Skip this pos
                    continue;

                // Increase counter
                counter++;

                // Don't destroy more block then: MAX_BLOCKS_DESTROYED_PER_OPERATION
                if (counter == MAX_BLOCKS_DESTROYED_PER_OPERATION)
                    // Skip
                    return;

                // Create falling block
                createItemBlock(b, world.getBlockState(b).getBlock());
            }
        }
    }

    // Used during mass increment/decrement
    private void notifyClient() {
        //NetworkHandler.sendToAll(new PacketSingularityChange(mass, this));
    }

    // Receives data from server on client-side of this class
    public void receiveClientData(int mass) {
        // Receive mass
        this.mass = mass;
    }

    private void createItemBlock(BlockPos pos, Block b) {
        // Tell block about breaking
        b.breakBlock(world, pos, b.getDefaultState());
        // Destroy normal block
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        // Create item block itself
        ItemStack stack = b.getPickBlock(b.getDefaultState(), null, world, pos, null);
        // Create entity to spawn
        EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ());
        // Change itemStack
        entityItem.setItem(stack);
        // Spawn entity
        world.spawnEntity(entityItem);
    }

    private void pullEntity(Entity e) {
        // Get position vector of this.getPos()
        Vec3d pos = new Vec3d(getPos());
        // Get vector direction for entity pulling
        Vec3d dir = pos.subtract(e.getPositionVector()).normalize();
        // Gravitational force
        double force = getGravityForceBetween(pos, dir);
        // Add velocity
        e.addVelocity(force * dir.x, force * dir.y, force * dir.z);

        // Check if entity is player
        if(e instanceof EntityPlayer)
            // Mark to update
            ((EntityPlayer)e).velocityChanged = true;
    }

    // ~~Called when radius is growing too much~~
    private void destroySingularity() {
        // Set block to air
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    public double getBlackHoleRadius() {
        // Real life formula 2GM/c^2 is suitable for getting radius of black hole, if just return mass,
        // then radius will grow instantly. But double cube root is also nice ;3
        double lightSpeed = 3;
        return Math.max(Math.cbrt(Math.cbrt(2 * gravitationalConst * mass / Math.pow(lightSpeed, 2))), 0.3);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
        if(hand == EnumHand.MAIN_HAND) {
            if (!world.isRemote) {
                AILog.chatLog("Mass: " + this.mass);
            }
        }
        return true;
    }

}
