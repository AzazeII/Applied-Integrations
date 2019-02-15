package AppliedIntegrations.Proxy;

import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Entities.TileEnum;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Parts.PartEnum;
import appeng.api.AEApi;
import appeng.api.movable.IMovableRegistry;
import appeng.api.recipes.IRecipeLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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