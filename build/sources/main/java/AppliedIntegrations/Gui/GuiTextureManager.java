package AppliedIntegrations.Gui;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public enum GuiTextureManager
{
    ENERGY_STORAGE_BUS("energy.storage.bus"),
    ENERGY_TERMINAL("energy.terminal"),
    ENERGY_IO_BUS("energy.io.bus"),
    PRIORITY ("priority");

    private ResourceLocation texture;

    private GuiTextureManager( final String textureName )
    {
        // Create the resource location
        this.texture = new ResourceLocation( AppliedIntegrations.modid, "textures/gui/" + textureName + ".png" );
    }

    public ResourceLocation getTexture()
    {
        return this.texture;
    }

}
