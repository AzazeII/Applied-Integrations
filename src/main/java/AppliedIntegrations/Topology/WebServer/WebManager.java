package AppliedIntegrations.Topology.WebServer;

import AppliedIntegrations.AIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static AppliedIntegrations.Topology.TopologyUtils.sendLink;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class WebManager {

    // Web server initialization
    private Server server;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) throws Exception{
        if(server == null){
            // Create new server
            server = new Server(AIConfig.webUIPort);

            // Set server handler
            server.setHandler(new WebHandler());

            // Start server
            server.start();

            // Notify player
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
                    TextFormatting.AQUA + "[Applied Integrations Web Server]" + WHITE
                    + " Starting web server at: "
            ));

            // Send link to player
            sendLink(Minecraft.getMinecraft().player);
        }
    }

    private class WebHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // Declare response encoding and types
            response.setContentType("text/html; charset=utf-8");

            // Declare response status code
            response.setStatus(HttpServletResponse.SC_OK);

            // Write back response
            response.getWriter().println("<h1>Hello World</h1>");

            // Inform jetty that this request has now been handled
            baseRequest.setHandled(true);
        }
    }
}
