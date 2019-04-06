package AppliedIntegrations.tile.LogicBus;

import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.networking.*;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.IAEMultiBlock;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @Author Azazell
 */
public class TileLogicBusPort extends TileLogicBusSlave implements IAIMultiBlock, IInterfaceHost {
    private boolean isCorner;

    public boolean isSubPort = false;

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL, GridFlags.MULTIBLOCK);
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        // List of sides
        List<EnumFacing> sides = new ArrayList<>();
        // Iterate only over horizontal sides, as only these sides can be connected to cable
        for(EnumFacing side : EnumFacing.HORIZONTALS){
            // Check if tile in this side is not instance of logic bus port
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

    public IGrid getOuterGrid(){
        if(getGridNode() != null)
            return getGridNode().getGrid();
        return null;
    }




    @Override
    public DualityInterface getInterfaceDuality() {
        return hasMaster()? getLogicMaster().getInterfaceDuality() : null;
    }

    @Override
    public EnumSet<EnumFacing> getTargets() {
        return hasMaster()? getLogicMaster().getTargets() : EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public void saveChanges() {
        if(hasMaster()) getLogicMaster().saveChanges();
    }

    @Override
    public int getInstalledUpgrades(Upgrades upgrades) {
        return hasMaster() ? getLogicMaster().getInstalledUpgrades(upgrades) : 0;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public IItemHandler getInventoryByName(String s) {
        return hasMaster() ? getLogicMaster().getInventoryByName(s) : null;
    }

    @Override
    public void provideCrafting(ICraftingProviderHelper iCraftingProviderHelper) {
        if(hasMaster()) getLogicMaster().provideCrafting(iCraftingProviderHelper);
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        return hasMaster() && getLogicMaster().pushPattern(iCraftingPatternDetails, inventoryCrafting);
    }

    @Override
    public boolean isBusy() {
        return hasMaster() && getLogicMaster().isBusy();
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return hasMaster() ? getLogicMaster().getRequestedJobs() : ImmutableSet.of(null);
    }

    @Override
    public IAEItemStack injectCraftedItems(ICraftingLink iCraftingLink, IAEItemStack iaeItemStack, Actionable actionable) {
        return hasMaster() ? getLogicMaster().injectCraftedItems(iCraftingLink, iaeItemStack, actionable) : null;
    }

    @Override
    public void jobStateChange(ICraftingLink iCraftingLink) {
        if(hasMaster()) getLogicMaster().jobStateChange(iCraftingLink);
    }

    @Override
    public IConfigManager getConfigManager() {
        return hasMaster() ? getLogicMaster().getConfigManager() : null;
    }
}
