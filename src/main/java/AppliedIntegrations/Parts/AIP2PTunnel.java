package AppliedIntegrations.Parts;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.client.texture.CableBusTextures;
import com.google.common.base.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
@cpw.mods.fml.common.Optional.InterfaceList(value = {
        @cpw.mods.fml.common.Optional.Interface(iface = "com.cout970.magneticraft.api.heat.IHeatTile", modid = "Magneticraft",striprefs = true),
        @cpw.mods.fml.common.Optional.Interface(iface = "com.cout970.magneticraft.api.heat.prefab.*",modid = "Magneticraft",striprefs = true)
})
public class AIP2PTunnel extends AIPart {
    public AIP2PTunnel(PartEnum associatedPart, SecurityPermissions... interactionPermissions) {
        super(associatedPart, interactionPermissions);
    }

    @Override
    protected AIGridNodeInventory getUpgradeInventory() {
        return null;
    }

    @Override
    public int cableConnectionRenderTo() {
        return 1;
    }

    protected IIcon getTypeTexture()
    {
        final Optional<Block> maybeBlock = AEApi.instance().definitions().blocks().quartz().maybeBlock();
        if( maybeBlock.isPresent() )
        {
            return maybeBlock.get().getIcon( 0, 0 );
        }
        else
        {
            return Blocks.quartz_block.getIcon( 0, 0 );
        }
    }
    @Override
    public void getBoxes( final IPartCollisionHelper bch )
    {
        bch.addBox( 5, 5, 12, 11, 11, 13 );
        bch.addBox( 3, 3, 13, 13, 13, 14 );
        bch.addBox( 2, 2, 14, 14, 14, 16 );
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void renderInventory( final IPartRenderHelper rh, final RenderBlocks renderer )
    {
        rh.setTexture( this.getTypeTexture() );

        rh.setBounds( 2, 2, 14, 14, 14, 16 );
        rh.renderInventoryBox( renderer );

        rh.setTexture( CableBusTextures.PartTunnelSides.getIcon(), CableBusTextures.PartTunnelSides.getIcon(), CableBusTextures.BlockP2PTunnel2.getIcon(),CableBusTextures.BlockP2PTunnel2.getIcon(), CableBusTextures.PartTunnelSides.getIcon(), CableBusTextures.PartTunnelSides.getIcon() );

        rh.setBounds( 2, 2, 14, 14, 14, 16 );
        rh.renderInventoryBox( renderer );
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void renderStatic( final int x, final int y, final int z, final IPartRenderHelper rh, final RenderBlocks renderer )
    {
        rh.setTexture( this.getTypeTexture() );

        rh.setBounds( 2, 2, 14, 14, 14, 16 );
        rh.renderBlock( x, y, z, renderer );

        rh.setTexture( CableBusTextures.PartTunnelSides.getIcon(), CableBusTextures.PartTunnelSides.getIcon(), CableBusTextures.BlockP2PTunnel2.getIcon(),CableBusTextures.BlockP2PTunnel2.getIcon(), CableBusTextures.PartTunnelSides.getIcon(), CableBusTextures.PartTunnelSides.getIcon() );

        rh.setBounds( 2, 2, 14, 14, 14, 16 );
        rh.renderBlock( x, y, z, renderer );

        rh.setBounds( 3, 3, 13, 13, 13, 14 );
        rh.renderBlock( x, y, z, renderer );

        rh.setTexture( CableBusTextures.BlockP2PTunnel3.getIcon() );

        rh.setBounds( 6, 5, 12, 10, 11, 13 );
        rh.renderBlock( x, y, z, renderer );

        rh.setBounds( 5, 6, 12, 11, 10, 13 );
        rh.renderBlock( x, y, z, renderer );
    }

    @Override
    public IIcon getBreakingTexture() {
        return null;
    }

    @Override
    public double getIdlePowerUsage() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
