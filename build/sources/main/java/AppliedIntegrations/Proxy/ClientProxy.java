package AppliedIntegrations.Proxy;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
        // your packets will not work because you will be getting a client
        // player even when you are on the server! Sounds absurd, but it's true.

        // Solution is to double-check side before returning the player:
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void SidedInit() {
        ItemEnum.registerModels();

        BlocksEnum.registerModels();
        BlocksEnum.registerItemModels();
    }

    @Override
    public void SidedPostInit(){

    }
}

