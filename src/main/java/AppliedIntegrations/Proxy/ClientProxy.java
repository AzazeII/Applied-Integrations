package AppliedIntegrations.Proxy;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Render.TextureManager;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @Author Azazell
 */
public class ClientProxy
        extends CommonProxy
{
    public ClientProxy()
    {
        MinecraftForge.EVENT_BUS.register( this );
    }

    @SubscribeEvent
    public void registerTextures( final TextureStitchEvent.Pre event )
    {
        // Register all block textures
        for( TextureManager texture : TextureManager.ALLVALUES )
        {
            texture.registerTexture( event.map );
        }
    }
    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
        // your packets will not work because you will be getting a client
        // player even when you are on the server! Sounds absurd, but it's true.

        // Solution is to double-check side before returning the player:
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event){
        NetworkHandler.registerClientPacket();
        // instancinate map
        TextureMap map = new TextureMap(1,AppliedIntegrations.modid);
        // register textures of parts
        for( TextureManager texture : TextureManager.ALLVALUES )
        {
            texture.registerTexture( map );
        }
    }

    @Override
    public void init(){

    }

    @Override
    public void postInit(){

    }
}

