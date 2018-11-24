package AppliedIntegrations.Render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static AppliedIntegrations.AppliedIntegrations.modid;

/**
 * @Author Azazell
 */
    @SideOnly(Side.CLIENT)
    public enum TextureManager
    {
        Corners(TextureTypes.Part,new String[]{"corner.medium.variant"}),

        BUS_COLOR (TextureTypes.Part, new String[] { "bus.color.border", "bus.color.light", "bus.color.side" }),

        BUS_BACK (TextureTypes.Part, new String[]{"part.back"}),

        BUS_AESIDEDBACK (TextureTypes.Part,new String[]{"PartMonitorSidesStatus"}),

        ENERGY_IMPORT_BUS(TextureTypes.Part, new String[] { "energy.import.bus.face", "energy.import.bus.overlay",
                "energy.import.bus.blank", "energy.import.bus.side" }),

        ENERGY_STORAGE_BUS(TextureTypes.Part, new String[] { "energy.storage.bus.face", "energy.storage.bus.overlay",
                "energy.storage.bus.side" }),

        ENERGY_EXPORT_BUS(TextureTypes.Part, new String[] { "energy.export.bus.face", "energy.export.bus.overlay",
                "energy.export.bus.blank", "energy.export.bus.side" }),

        ENERGY_TERMINAL(TextureTypes.Part, new String[] { "energy.terminal.overlay.medium", "energy.terminal.overlay.dark",
                "energy.terminal.overlay.light", "energy.terminal.side", "energy.terminal.border","energy.terminal.overlay.inv" }),


        ENERGY_INTERFACE(TextureTypes.Part, new String[] { "energy.interface.front", "energy.interface.back", "energy.interface.side","interface.bus.lights" });

        private enum TextureTypes
        {
            Block,
            Part;
        }

        /**
         * Cache of the enum values
         */
        public static final List<TextureManager> ALLVALUES = Collections.unmodifiableList( Arrays.asList( TextureManager.values() ) );

        private TextureTypes textureType;

        private String[] textureNames;

        private IIcon[] textures;

        private TextureManager(final TextureTypes textureType, final String[] textureNames )
        {
            this.textureType = textureType;
            this.textureNames = textureNames;
            this.textures = new IIcon[this.textureNames.length];
        }

        public IIcon getTexture()
        {
            return this.textures[0];
        }

        public IIcon[] getTextures()
        {
            return this.textures;
        }

        public void registerTexture( final TextureMap textureMap )
        {
            if( textureMap.getTextureType() == 0 )
            {
                String header = modid+":";

                if( this.textureType == TextureTypes.Part )
                {
                    header += "parts/";
                }

                for( int i = 0; i < this.textureNames.length; i++ )
                {
                    this.textures[i] = textureMap.registerIcon( header + this.textureNames[i] );
                }
            }
        }
    }

