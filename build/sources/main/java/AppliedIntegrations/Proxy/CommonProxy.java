package AppliedIntegrations.Proxy;

import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.TileEntity.TileEnum;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.grid.EnergyTunnel;
import appeng.api.AEApi;
import appeng.api.movable.IMovableRegistry;
import appeng.api.recipes.IRecipeLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.*;

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

    public void SidedPreInit(){
        ItemEnum.register();
        BlocksEnum.register();
            //PartEnum.registerAEModels();
        TileEnum.register();

        // Register channel **MOST IMPORTANT PART ;) **
        AEApi.instance().storage().registerStorageChannel(IEnergyTunnel.class, new EnergyTunnel());
    }

    public void SidedInit(){

    }

    public void SidedPostInit(){

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
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }
}