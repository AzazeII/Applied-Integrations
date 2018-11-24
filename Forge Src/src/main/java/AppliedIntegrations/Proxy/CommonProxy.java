package AppliedIntegrations.Proxy;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlockEnergyInterface;
import AppliedIntegrations.Entities.TileEnum;

import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCasing;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCell;
import AppliedIntegrations.Items.StorageCells.EnergyStorageComponent;
import appeng.api.AEApi;
import appeng.api.movable.IMovableRegistry;
import appeng.api.recipes.IRecipeHandler;
import appeng.api.recipes.IRecipeLoader;
import appeng.block.networking.BlockEnergyCell;
import appeng.core.Api;
import appeng.core.api.definitions.ApiBlocks;
import appeng.items.materials.ItemMultiMaterial;
import appeng.items.materials.MaterialType;
import appeng.items.parts.ItemMultiPart;
import appeng.items.parts.PartType;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.Items.StorageCells.EnergyStorageCell.suffixes;

/**
 * @Author Azazell
 */
public class CommonProxy
{
    private class ExternalRecipeLoader implements IRecipeLoader {

        @Override
        public BufferedReader getFile(String path) throws Exception {
            return new BufferedReader(new FileReader(new File(path)));
        }
    }

    private class InternalRecipeLoader implements IRecipeLoader {

        @Override
        public BufferedReader getFile(String path) throws Exception {
            InputStream resourceAsStream = getClass().getResourceAsStream("/assets/appliedintegrations/recipes/" + path);
            InputStreamReader reader = new InputStreamReader(resourceAsStream, "UTF-8");
            return new BufferedReader(reader);
        }
    }
    /**
     * Adds tile entities to the AppEng2 SpatialIO whitelist
     */
    public void registerSpatialIOMovables()
    {
        IMovableRegistry movableRegistry = AEApi.instance().registries().movable();
        for( TileEnum tile : TileEnum.values() )
        {
            movableRegistry.whiteListTileEntity( tile.getTileClass() );
        }
    }
    /**
     * Adds recipes for all machines
     */
    public void addRecipes() {
        for (int i = 0; i < 8; i++) {
            GameRegistry.addShapelessRecipe(new ItemStack(ItemEnum.ENEGYSTORAGE.getItem(), 1, i), new Object[]{new ItemStack(ItemEnum.ENERGYSTORAGECASING.getItem(), 1, 0), new
                    ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, i)});
        }
        // Recipe Interface part <--> Interface block
        GameRegistry.addShapelessRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 4), new Object[]{new ItemStack(AppliedIntegrations.EInterface)});
        GameRegistry.addShapelessRecipe(new ItemStack(AppliedIntegrations.EInterface), new Object[]{new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 4)});
        // Annihilation core
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem(), 2, 0), new Object[]{"QWE", "RRR", "   ", 'Q', new ItemStack(
                Items.quartz, 1, 0), 'W', new ItemStack(MaterialType.FluixDust.getItemInstance(), 1, 8), 'E', new ItemStack(MaterialType.LogicProcessor.getItemInstance(), 1, 22), 'R', new ItemStack(Items.redstone)});
        // Formation core
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem(), 2, 0), new Object[]{"QWE", "RRR", "   ", 'Q', new ItemStack(
                MaterialType.CertusQuartzCrystal.getItemInstance()), 'W', new ItemStack(MaterialType.FluixDust.getItemInstance(), 1, 8), 'E',
                new ItemStack(MaterialType.LogicProcessor.getItemInstance(), 1, 22), 'R', new ItemStack(Items.redstone)});
        // Block interface
        GameRegistry.addShapedRecipe(new ItemStack(AppliedIntegrations.EInterface), new Object[]{"IRI", "FEA", "IRI", 'I', new ItemStack(Items.iron_ingot), 'R', new ItemStack(Items.redstone), 'F',
                new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem()), 'A', new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem()), 'E',
                new ItemStack(Api.INSTANCE.blocks().blockEnergyCell.block())});
        // Energy Storage bus (with part)
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 1), new Object[]{"ISP", "   ", "   ", 'I', new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 4),
                'S', new ItemStack(Blocks.sticky_piston), 'P', new ItemStack(Blocks.piston)});
        // Energy Storage bus (with tile)
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 1), new Object[]{"ISP", "   ", "   ", 'I', new ItemStack(AppliedIntegrations.EInterface),
                'S', new ItemStack(Blocks.sticky_piston), 'P', new ItemStack(Blocks.piston)});
        // Import Bus
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 0), new Object[]{" A ", "ISI", "   ", 'A', new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem())
                , 'I', new ItemStack(Items.iron_ingot), 'S', new ItemStack(Blocks.sticky_piston)});
        // Export Bus
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(), 1, 2), new Object[]{"IFI", " S ", "   ", 'F', new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem())
                , 'I', new ItemStack(Items.iron_ingot), 'S', new ItemStack(Blocks.piston)});

        Block EnergyCell = Api.INSTANCE.blocks().blockEnergyCell.block();
        ItemStack LogicProc = new ItemStack(MaterialType.LogicProcessor.getItemInstance(), 1, 22);
        ItemStack EngProc = new ItemStack(MaterialType.EngProcessor.getItemInstance(), 1, 24);
        ItemStack stackRedstone = new ItemStack(Items.redstone);
        ItemStack stackGlowStone = new ItemStack(Items.glowstone_dust);
        // cell components{
        // 1k cell component
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1), new Object[]{"rEr", "ElE", "rEr", 'r', stackRedstone, 'E'
                , new ItemStack(EnergyCell), 'l', LogicProc});
        // 4k cell component
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, 1), new Object[]{"rLr", "cGc", "rcr", 'r', stackRedstone, 'c',
                new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1), 'L', LogicProc, 'G', Api.INSTANCE.blocks().blockQuartzGlass.block()});
        // 16k cell component
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, 2), new Object[]{"rLr", "cGc", "rcr", 'r', stackGlowStone, 'c',
                new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, 1), 'L', EngProc, 'G', Api.INSTANCE.blocks().blockQuartzGlass.block()});
        // 64k cell component
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, 3), new Object[]{"rLr", "cGc", "rcr", 'r', stackGlowStone, 'c',
                new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, 2), 'L', EngProc, 'G', Api.INSTANCE.blocks().blockQuartzGlass.block()});
        // all Others components
        for (int i = 3; i < suffixes.length - 1; i++) {
            GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, i), new Object[]{"rLr", "cGc", "rcr", 'r', stackGlowStone, 'c',
                    new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, i - 1), 'L', EngProc, 'G', LogicProc});
        }
        //}
        // Storage casing
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENERGYSTORAGECASING.getItem()), new Object[]{"gRg", "R R", "EEE", 'g', Api.INSTANCE.blocks().blockQuartzGlass.block(),
                'R', new ItemStack(ItemMultiMaterial.instance, 1, 2), 'E', new ItemStack(EnergyCell)});
        // Add recipes for cells
        for (int i = 0; i < suffixes.length - 1; i++) {
            GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ENEGYSTORAGE.getItem(), 1, i), new Object[]{"gRg", "RcR", "EEE", 'g', Api.INSTANCE.blocks().blockQuartzGlass.block(),
                    'R', new ItemStack(ItemMultiMaterial.instance, 1, 2), 'c',
                    new ItemStack(ItemEnum.ENERGYSTORAGECOMPONENT.getItem(), 1, i), 'E', new ItemStack(EnergyCell)});
        }
        // Add recipe for wireless terminal
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.ITEMENERGYWIRELESSTERMINAL.getItem(),1),new Object[]{"r  ","t  ","d  ",'t',
                new ItemStack(ItemEnum.PARTITEM.getItem(),1,3),'d',new ItemStack(Api.INSTANCE.blocks().blockEnergyCellDense.item()),'r',
        new ItemStack(ItemMultiMaterial.instance,1,41)});
        // Add recipe for terminal
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(),1,3),new Object[]{
           "MFA","P  ","   ",'P',LogicProc,'F',new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem()),'A',new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem()),'M',
                new ItemStack(ItemMultiPart.instance,1,180)
        });
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(),1,3),new Object[]{
                "MFA","P  ","   ",'P',LogicProc,'F',new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem()),'A',new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem()),'M',
                new ItemStack(ItemMultiPart.instance,1,200)
        });
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(),1,3),new Object[]{
                "MFA","P  ","   ",'P',LogicProc,'F',new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem()),'A',new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem()),'M',
                new ItemStack(ItemMultiPart.instance,1,160)
        });
        GameRegistry.addShapelessRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(),1,5), new Object[]{new ItemStack(ItemMultiPart.instance,1,280),
                new ItemStack(ItemMultiPart.instance,1,180),EnergyCell});
        GameRegistry.addShapelessRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(),1,5), new Object[]{new ItemStack(ItemMultiPart.instance,1,280),
                new ItemStack(ItemMultiPart.instance,1,160),EnergyCell});
        GameRegistry.addShapelessRecipe(new ItemStack(ItemEnum.PARTITEM.getItem(),1,5), new Object[]{new ItemStack(ItemMultiPart.instance,1,280),
                new ItemStack(ItemMultiPart.instance,1,200),EnergyCell});
        // Chaotic manipulator
        GameRegistry.addShapedRecipe(new ItemStack(ItemEnum.CHAOSMANIPULATOR.getItem()), new Object[]{" FI", " MA", "L  ", 'L', new ItemStack(Items.iron_ingot),'F',
        new ItemStack(ItemEnum.ENERGYFORMATIONCORE.getItem()),'A',new ItemStack(ItemEnum.ENERGYANNIHILATIONCORE.getItem()),'I', AppliedIntegrations.EInterface, 'M', Api.INSTANCE.items()
        .itemEntropyManipulator.item()});
    }
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}