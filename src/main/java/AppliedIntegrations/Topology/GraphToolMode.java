package AppliedIntegrations.Topology;

public enum GraphToolMode {
    LINE, // Show all nodes line from line end to controller TODO
    ALL, // Show all nodes in network
    NODE_CLICKED, // Show all nodes, same type as node clicked
    SUBNETWORK, // Show only sub-networks connected to main network
    P2P_LINKS // Show all p2p links in network
    //STORAGE_PROVIDERS // Show all nodes, which provide any ME inventory TODO
}
