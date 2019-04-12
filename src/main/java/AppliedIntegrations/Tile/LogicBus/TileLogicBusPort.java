package AppliedIntegrations.Tile.LogicBus;

import AppliedIntegrations.Tile.IAIMultiBlock;
import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.networking.*;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

/**
 * @Author Azazell
 */
public class TileLogicBusPort extends TileLogicBusSlave implements IAIMultiBlock {
    private boolean isCorner;

    public boolean isSubPort = false;

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL, GridFlags.MULTIBLOCK);
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        // list of sides
        List<EnumFacing> sides = new ArrayList<>();
        // Iterate only over horizontal sides, as only these sides can be connected to cable
        for(EnumFacing side : EnumFacing.HORIZONTALS){
            // Check if Tile in this side is not instance of logic bus port
            if(!(world.getTileEntity(pos.offset(side)) instanceof TileLogicBusPort)){
                sides.add(side);
            }
        }

        // Temp set
        EnumSet<EnumFacing> temp = EnumSet.noneOf(EnumFacing.class);
        // Add sides
        temp.addAll(sides);
        return temp;
    }

    @Override
    public void tryConstruct(EntityPlayer p) {

    }
}
