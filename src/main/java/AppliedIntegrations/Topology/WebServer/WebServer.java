package AppliedIntegrations.Topology.WebServer;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @Author Azazell
 */
public class WebServer {
    public WebServer(int port){
        try {
            // Create server
            HttpServer server = HttpServer.create();

            // Bind server to specified port
            server.bind(new InetSocketAddress(port), 0);

            // Add handler to server
            server.createContext("/", new WebHandler());

            // Start web server
            server.start();
        }catch (IOException e){

        }
    }
}
