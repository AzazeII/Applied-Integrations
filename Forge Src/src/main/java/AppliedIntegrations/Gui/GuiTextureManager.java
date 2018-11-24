package AppliedIntegrations.Gui;

import AppliedIntegrations.AppliedIntegrations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
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
