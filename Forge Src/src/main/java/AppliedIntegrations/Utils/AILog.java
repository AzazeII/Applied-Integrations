package AppliedIntegrations.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AILog
{
    public static final Logger log = LogManager.getLogger( "Applied Integrations" );

    public static void debug( final String format, final Object ... data )
    {
        log.debug( String.format( format, data ) );
    }

    public static void error( final Throwable e, final String format, final Object ... data )
    {
        log.error( String.format( format, data ), e );
    }
    public static void info( final String message, Object... params)
    {
        log.info(message, params );
    }
    public static void chatLog(final String message){
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
    }
}
