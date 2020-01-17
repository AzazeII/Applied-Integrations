package AppliedIntegrations.Topology.WebServer;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Topology.TopologyUtils;

import static spark.Spark.*;

/**
 * @Author Azazell
 */
public class WebManager {
	public static void init() {
		port(AIConfig.webUIPort);
		staticFileLocation("webUI/");
		get("/json", (request, res) -> {
			res.type("application/json");
			return TopologyUtils.getInnerObject();
		});
	}
}
