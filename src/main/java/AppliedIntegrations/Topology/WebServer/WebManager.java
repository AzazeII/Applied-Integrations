package AppliedIntegrations.Topology.WebServer;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Topology.TopologyUtils;

import static spark.Spark.*;

public class WebManager {
	public static void init() {
		// Bind port
		port(AIConfig.webUIPort);

		// Bind static file
		staticFileLocation("webUI/");

		// Create json get method
		get("/json", (request, res) -> {
			// Set type to json application
			res.type("application/json");

			// Return data from Topology Utils
			return TopologyUtils.getInnerObject();
		});
	}
}
