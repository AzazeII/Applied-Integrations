package AppliedIntegrations.tile.Server.Render;

import AppliedIntegrations.Client.AITileRenderer;
import AppliedIntegrations.tile.Server.TileServerRib;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class ServerRibRenderer extends AITileRenderer<TileServerRib> {
    @Override
    public void render(TileServerRib te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        // Pass call to super()
        prepareMatrix(x, y, z);

        pushMatrix(x, y, z);
    }
}
