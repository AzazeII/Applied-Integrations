package AppliedIntegrations.Topology;

import AppliedIntegrations.AIConfig;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergyGridProvider;
import appeng.api.util.AEPartLocation;
import appeng.me.cache.EnergyGridCache;
import appeng.me.cache.P2PCache;
import appeng.me.cache.PathGridCache;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TopologyUtils {

    /**
     * Creates Web UI from given parameters
     * @param grid
     *  Grid of node selected
     *
     * @param player
     *  Player, who queried this request
     *
     * @param mode
     *  Current working mode of graph tool
     *
     * @param machine
     *  GridHost clicked on by tool
     */
    public static void createWebUI(IGrid grid, EntityPlayer player, GraphToolMode mode, IGridHost machine) {
        // Switch modes
        switch (mode) {
            // Case all network nodes showing
            case ALL:
                // Graph p2p links
                graphAll(grid, player);
                break;

            // Case all network nodes from line end to controller showing
            case LINE:
                // Graph line nodes
                graphLineNodes(grid, player);
                break;

            // Graph all p2p links
            case P2P_LINKS:
                // Graph p2p links
                graphP2PLinks(grid, player);
                break;

            // Graph sub networks connected to main network
            case SUBNETWORK:
                // Graph sub networks
                graphSubnetworks(grid, player);
                break;

            // Graph only node same(by type) as clicked node
            case NODE_CLICKED:
                // Graph nodes
                graphGiven(grid, player, machine);
                break;

        }
    }

    private static void sendLink(EntityPlayer player) {
        // Create click event
        ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://127.0.0.1:" + AIConfig.webUIPort);

        // Create text message
        TextComponentString message = new TextComponentString(TextFormatting.DARK_AQUA + ( TextFormatting.UNDERLINE + "http://127.0.0.1:" + AIConfig.webUIPort ));

        // Set click event
        message.getStyle().setClickEvent(click);

        // Create message ("localhost: <PORT_IN_CONFIG>"), and send it as link to player)
        player.sendMessage(message);
    }

    private static void graphGiven(IGrid grid, EntityPlayer player, IGridHost machine) {
        // Create node list
        List<IGridNode> nodeList = new ArrayList<>();

        // Iterate for each node in given grid
        for (IGridNode node : grid.getNodes())
            // Check if host of node is equal (by type) to given machine
            if( node.getMachine().getClass() == machine.getClass() )
                // Add node
                nodeList.add(node);
        // Create json from list
        createJSON(nodeList);

        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( Only: " + machine.toString() +" ):"));

        // Send link message
        sendLink(player);
    }

    private static void graphSubnetworks(IGrid grid, EntityPlayer player) {
        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( Sub-networks ):"));

        // Get energy cache
        EnergyGridCache iEnergyGrid = grid.getCache(IEnergyGrid.class);

        // Get energy grid providers
        Collection<IEnergyGridProvider> gridProviders = iEnergyGrid.providers();

        // Create node list
        List<IGridNode> nodeList = new ArrayList<>();

        // Create custom connections list
        List<Pair<IGridNode, IGridNode>> connections = new ArrayList<>();

        // Create for each object
        gridProviders.forEach((iEnergyGridProvider -> {
            // Create tuple pair
            Pair<IGridNode, IGridNode> pair = new Pair<IGridNode, IGridNode>() {
                @Override
                public IGridNode getLeft() {
                    return grid.getPivot();
                }

                @Override
                public IGridNode getRight() {
                    // Check if provider instanceof grid host
                    if(iEnergyGridProvider instanceof IGridHost)
                        // Return node
                        return ((IGridHost) iEnergyGridProvider).getGridNode(AEPartLocation.INTERNAL);
                    // null ;p
                    return null;
                }

                @Override
                public IGridNode setValue(IGridNode value) {
                    return null;
                }
            };

            // Check not null
            if (pair.getRight() != null)
                // Add pair to connections
                connections.add(pair);

            // Check if provider instanceof grid host
            if(iEnergyGridProvider instanceof IGridHost)
                // Add node
                nodeList.add(((IGridHost)iEnergyGridProvider).getGridNode(AEPartLocation.INTERNAL));
        }));

        // Create JSON object
        createSubnetworkJSON(nodeList, connections, grid.getPivot());

        // Send link message
        sendLink(player);
    }

    private static void graphLineNodes(IGrid grid, EntityPlayer player) {
        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( Line ):"));

        // Send link message
        sendLink(player);
    }

    private static void graphAll(IGrid grid, EntityPlayer player) {
        // Pass call to JSON creator
        createJSON(grid.getNodes());

        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( All network ):"));

        // Send link message
        sendLink(player);
    }

    private static void graphP2PLinks(IGrid grid, EntityPlayer player) {
        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( P2P links ):"));

        // Get cache
        P2PCache cache = grid.getCache(P2PCache.class);

        // Create p2p tunnel list
        List<PartP2PTunnel> tunnelList = new LinkedList<>();

        // Iterate for each grid node
        grid.getNodes().forEach((iGridNode -> {
            // Check if machine of node instanceof p2p tunnel
            if(iGridNode.getMachine() instanceof PartP2PTunnel){
                tunnelList.add((PartP2PTunnel)iGridNode.getMachine());
            }
        }));

        // Create pair of connections
        List<Pair<IGridNode, IGridNode>> connections = new LinkedList<>();

        // Iterate for each p2p tunnel
        tunnelList.forEach((partP2PTunnel -> {
            // Iterate for each output
            cache.getOutputs(partP2PTunnel.getFrequency(), partP2PTunnel.getClass()).forEach((tunnelRight) -> {
                // Add new connection pair
                connections.add(new Pair<IGridNode, IGridNode>() {
                    @Override
                    public IGridNode getLeft() {
                        return partP2PTunnel.getGridNode();
                    }

                    @Override
                    public IGridNode getRight() {
                        return tunnelRight.getGridNode();
                    }

                    @Override
                    public IGridNode setValue(IGridNode value) {
                        return null;
                    }
                });
            });
        }));

        // Create node list
        List<IGridNode> nodeList = new LinkedList<>();

        // Create for each lambda of tunnel list
        tunnelList.forEach((partP2PTunnel -> {
            nodeList.add(partP2PTunnel.getGridNode());
        }));

        // Create json object
        createJSONFromConnections(nodeList, connections);

        // Send link message
        sendLink(player);
    }

    private static String toHumanReadableString(String toString){
        // Split string to array
        String[] array = toString.split("\\.");

        // Return last element of array
        return array[array.length-1];
    }

    private static void createJSON(Iterable<IGridNode> nodeList){
        // Create pair list
        List<Pair<IGridNode, IGridNode>> connections = new LinkedList<>();

        // Iterate over all nodes
        for (IGridNode gridNode : nodeList) {
            // Create for_each cycle
            gridNode.getConnections().forEach((iGridConnection -> {
                // Add new pair to connections
                connections.add(new Pair<IGridNode, IGridNode>() {
                    @Override
                    public IGridNode getLeft() {
                        return iGridConnection.a();
                    }

                    @Override
                    public IGridNode getRight() {
                        return iGridConnection.b();
                    }

                    @Override
                    public IGridNode setValue(IGridNode value) {
                        return null;
                    }
                });
            }));
        }

        // Pass to primitive function
        createJSONFromConnections(nodeList, connections);
    }

    private static void createJSONFromConnections(Iterable<IGridNode> nodeList, List<Pair<IGridNode, IGridNode>> connections) {
        // Create node object
        JSONObject network = new JSONObject();
        // Create node array
        JSONArray jsonNodeList = new JSONArray();
        // Create list of connections
        JSONArray aNodeList = new JSONArray(); // (1)
        JSONArray bNodeList = new JSONArray(); // (2)

        // Iterate for each connection
        connections.forEach((iGridNodeIGridNodePair -> {
            // Put left node
            aNodeList.put(toHumanReadableString(iGridNodeIGridNodePair.getLeft().getMachine().toString()));

            // Put right node
            bNodeList.put(toHumanReadableString(iGridNodeIGridNodePair.getRight().getMachine().toString()));
        }));

        // Put created arrays
        network.put("nodes", jsonNodeList); // (1)
        network.put("src", aNodeList); // (2)
        network.put("dest", bNodeList); // (3)

        try {
            // Write file
            Files.write(Paths.get("Network.json"), network.toString().getBytes());
        }catch (IOException e){ e.printStackTrace(); }
    }

    // Same as createJSON(list), but with custom connection list
    private static void createSubnetworkJSON(List<IGridNode> nodeList, List<Pair<IGridNode, IGridNode>> connections, IGridNode mainPivot) {
        // Create node object
        JSONObject network = new JSONObject();
        // Create node array
        JSONArray jsonNodeList = new JSONArray();
        // Create list of connections
        JSONArray aNodeList = new JSONArray(); // (1)
        JSONArray bNodeList = new JSONArray(); // (2)

        // Iterate over all nodes
        for (IGridNode gridNode : nodeList) {
            // Check if grid node equal to pivot
            if(mainPivot.getMachine().toString().equals(gridNode.getMachine().toString()))
                // Mark as main network
                jsonNodeList.put("Selected Network");
            else
                // Convert to readable string
                jsonNodeList.put(toHumanReadableString(gridNode.getMachine().toString()));

            // Create for_each cycle
            connections.forEach((iGridNodePair -> {
                // Fill "A" node list
                aNodeList.put("Selected Network");

                // Fill "B" node list
                bNodeList.put(toHumanReadableString(iGridNodePair.getRight().getMachine().toString()));
            }));
        }

        // Put created arrays
        network.put("nodes", jsonNodeList); // (1)
        network.put("src", aNodeList); // (2)
        network.put("dest", bNodeList); // (3)

        try {
            // Write file
            Files.write(Paths.get("Network.json"), network.toString().getBytes());
        }catch (IOException e){ e.printStackTrace(); }
    }
}
