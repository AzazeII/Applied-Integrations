package AppliedIntegrations.Topology;

/**
 * @Author Azazell
 */
public enum GraphToolMode {
	ALL, // Show all nodes in network
	NODE_CLICKED, // Show all nodes, with same type as node clicked
	SUBNETWORK, // Show only sub-networks connected to main network
	P2P_LINKS // Show all p2p links in network


	// Commented to further discussions about adding:
	//LINE, // Show all nodes line from line end to controller TODO
	//STORAGE_PROVIDERS // Show all nodes, which provide any ME inventory TODO
}
