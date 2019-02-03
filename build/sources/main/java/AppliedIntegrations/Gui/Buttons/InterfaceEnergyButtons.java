package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.AppliedIntegrations;
import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import java.util.regex.Pattern;

public class InterfaceEnergyButtons extends GuiButton implements ITooltip
{
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile( "\\n", Pattern.LITERAL );
    private final int iconIdxOn;
    private final int iconIdxOff;

    private final String displayName;
    private final String displayHint;

    private boolean isActive;

    public InterfaceEnergyButtons( final int x, final int y, final int on, final int off, final String displayName, final String displayHint )
    {
        super( 0, 0, 16, "" );
        this.iconIdxOn = on;
        this.iconIdxOff = off;
        this.displayName = displayName;
        this.displayHint = displayHint;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
    }

    public void setState( final boolean isOn )
    {
        this.isActive = isOn;
    }

    //@Override
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3 )
    {
        if( this.visible )
        {
            GL11.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
            par1Minecraft.renderEngine.bindTexture( new ResourceLocation(AppliedIntegrations.modid,"textures/guis/states.png" ));

            final int uv_y = (int) Math.floor( 1 / 16 );
            final int uv_x = 1 - uv_y * 16;

            this.drawTexturedModalRect( this.x, this.y, 0, 0, 16, 16 );
            this.drawTexturedModalRect( this.x, this.y, uv_x * 16, uv_y * 16, 16, 16 );
            this.mouseDragged( par1Minecraft, par2, par3 );
        }
    }
    @Override
    public String getMessage()
    {
        if( this.displayName != null )
        {
            String name = I18n.translateToLocal( this.displayName );
            String value = I18n.translateToLocal( this.displayHint );

            if( name == null || name.isEmpty() )
            {
                name = this.displayName;
            }
            if( value == null || value.isEmpty() )
            {
                value = this.displayHint;
            }

            value = PATTERN_NEW_LINE.matcher( value ).replaceAll( "\n" );
            final StringBuilder sb = new StringBuilder( value );

            int i = sb.lastIndexOf( "\n" );
            if( i <= 0 )
            {
                i = 0;
            }
            while( i + 30 < sb.length() && ( i = sb.lastIndexOf( " ", i + 30 ) ) != -1 )
            {
                sb.replace( i, i + 1, "\n" );
            }

            return name + '\n' + sb;
        }
        return null;
    }

    @Override
    public int xPos()
    {
        return this.x;
    }

    @Override
    public int yPos()
    {
        return this.y;
    }

    @Override
    public int getWidth()
    {
        return 16;
    }

    @Override
    public int getHeight()
    {
        return 16;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }
}
