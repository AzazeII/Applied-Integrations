package AppliedIntegrations.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BlockAIRegistrable extends BlockContainer {

    public BlockAIRegistrable(String registryName) {
        this(registryName, registryName);
    }

    public BlockAIRegistrable(String registryName, String unlocalizedName) {
        super(Material.IRON, null);
        this.setUnlocalizedName(unlocalizedName);
        this.setRegistryName(registryName);
    }
}
