package AppliedIntegrations.Topology;

import AppliedIntegrations.AIConfig;
import appeng.api.networking.*;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergyGridProvider;
import appeng.api.util.AEPartLocation;
import appeng.me.cache.EnergyGridCache;
import appeng.me.cache.P2PCache;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Azazell
 */
public class TopologyUtils {

    private static JSONObject innerObject = new JSONObject();

    /**
     * Creates WebUI from given parameters
     * @param grid
     *  grid of node selected
     *
     * @param player
     *  Player, who queried this request
     *
     * @param mode
     *  Current working mode of graph tool
     *
     * @param machine
     *  Machine, which queried this request
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
            /*case LINE:
                // Graph line nodes
                graphLineNodes(grid, player);
                break;*/

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

    @Nonnull
    public static JSONObject getInnerObject(){
        // Return inner object
        return innerObject;
    }

    public static TextComponentString createLink() {
        // Create click event
        ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://127.0.0.1:" + AIConfig.webUIPort);

        // Create text message
        TextComponentString message = new TextComponentString(TextFormatting.AQUA + ( TextFormatting.UNDERLINE + "http://127.0.0.1:" + AIConfig.webUIPort ));

        // Set click event
        message.getStyle().setClickEvent(click);

        // Return newly create message
        return message;
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
        innerObject = createJSONFromGridNodes(nodeList);
    }

    private static void graphSubnetworks(IGrid grid, EntityPlayer player) {
        // Get energy cache
        EnergyGridCache iEnergyGrid = grid.getCache(IEnergyGrid.class);

        // Get energy grid providers
        Collection<IEnergyGridProvider> gridProviders = iEnergyGrid.providers();

        // Create node list
        List<IGridNode> nodeList = new ArrayList<>();

        // Create custom connections list
        List<Pair<IGridNode, IGridNode>> connections = new ArrayList<>();

        // Add pivot to list
        nodeList.add(grid.getPivot());

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
        createSubnetworkJSON(nodeList, connections, grid);
    }

    private static void graphLineNodes(IGrid grid, EntityPlayer player) {

    }

    private static void graphAll(IGrid grid, EntityPlayer player) {
        // Pass call to JSON creator
        innerObject = createJSONFromGridNodes(grid.getNodes());
    }

    private static void graphP2PLinks(IGrid grid, EntityPlayer player) {
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
        tunnelList.forEach((partP2PTunnel -> nodeList.add(partP2PTunnel.getGridNode())));

        // Create json object
        innerObject = createJSONFromConnections(nodeList, connections);

    }

    private static String toHumanReadableString(String toString){
        // Split string to array
        String[] array = toString.split("\\.");

        // Return last element of array
        return array[array.length-1];
    }

    private static JSONObject serializeNodeData(IGridNode node){
        // Create temp object
        JSONObject temp = new JSONObject();

        // Put current node state
        temp.put("Active", node.isActive());

        // Write pos
        temp.put("X", node.getGridBlock().getLocation().x); // (1)
        temp.put("Y", node.getGridBlock().getLocation().y); // (2)
        temp.put("Z", node.getGridBlock().getLocation().z); // (3)

        // Write color
        temp.put("Hex", node.getGridBlock().getGridColor().mediumVariant);

        // Iterate over each flag
        for (GridFlags flag : GridFlags.values()){
            // Check if node has this flag
            temp.put(flag.name(), node.getGridBlock().getFlags().contains(flag));
        }

        // Write power usage
        temp.put("Usage", node.getGridBlock().getIdlePowerUsage() + " AE");

        // Check if host is p2p tunnel
        if(node.getMachine() instanceof PartP2PTunnel){
            // Create p2p tunnel pointer
            PartP2PTunnel<?> partP2PTunnel = (PartP2PTunnel<?>)node.getMachine();

            // Write frequency
            temp.put("Frequency", partP2PTunnel.getFrequency());
        }else{
            // Write integer frequency
            temp.put("Frequency", Short.MAX_VALUE+1);
        }

        // Write true, if node is pivot
        temp.put("Pivot", node.getGrid().getPivot() == node);

        return temp;
    }

    private static JSONObject createJSONFromGridNodes(Iterable<IGridNode> nodeList){
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
        return createJSONFromConnections(nodeList, connections);
    }

    private static JSONObject createJSONFromConnections(Iterable<IGridNode> nodeList, List<Pair<IGridNode, IGridNode>> connections) {
        // Create node object
        JSONObject network = new JSONObject();
        // Create node array
        JSONArray jsonNodeList = new JSONArray();
        // Create list of connections
        JSONArray aNodeList = new JSONArray(); // (1)
        JSONArray bNodeList = new JSONArray(); // (2)

        // list of serialized node data
        List<JSONObject> serializedDataList = new ArrayList<>();

        // Iterate for each connection
        connections.forEach((iGridNodeIGridNodePair -> {
            // Put left node
            aNodeList.put(toHumanReadableString(iGridNodeIGridNodePair.getLeft().getMachine().toString()));

            // Put right node
            bNodeList.put(toHumanReadableString(iGridNodeIGridNodePair.getRight().getMachine().toString()));
        }));

        // Iterate for each node
        nodeList.forEach((iGridNode -> {
            // Add node to list
            jsonNodeList.put(toHumanReadableString(iGridNode.getMachine().toString()));

            // Serialize data of node
            serializedDataList.add(serializeNodeData(iGridNode));
        }));

        // Put created arrays
        network.put("nodes", jsonNodeList); // (1)
        network.put("src", aNodeList); // (2)
        network.put("dest", bNodeList); // (3)
        network.put("data", serializedDataList); // (4)
        network.put("mode", "not_sub_network"); // (5)

        // Change inner json
        return network;
    }

    // Same as createJSONFromGridNodes(list), but with custom connection list
    private static void createSubnetworkJSON(List<IGridNode> nodeList, List<Pair<IGridNode, IGridNode>> connections, IGrid mainNet) {
        // Create node object
        JSONObject network = new JSONObject();
        // Create node array
        JSONArray jsonNodeList = new JSONArray();
        // Create list of connections
        JSONArray aNodeList = new JSONArray(); // (1)
        JSONArray bNodeList = new JSONArray(); // (2)

        // list of serialized node data
        List<JSONObject> serializedDataList = new ArrayList<>();

        // list of serialized grids
        List<JSONObject> serializedGridList = new ArrayList<>();

        // Iterate over all nodes
        for (IGridNode gridNode : nodeList) {
            // Serialize data of node
            serializedDataList.add(serializeNodeData(gridNode));

            // Check if grid node equal to pivot of main net
            if(gridNode == mainNet.getPivot()) {
                // Check if node list not already contains main network
                if(!jsonNodeList.toList().contains("Selected Network")) {
                    // Mark as main network
                    jsonNodeList.put("Selected Network");

                    // Get JSON object
                    JSONObject obj = createJSONFromGridNodes(mainNet.getNodes());

                    // Write grid node string(only has used)
                    obj.put("iGridProvider", toHumanReadableString(gridNode.getMachine().toString()));

                    // Serialize outer grid
                    serializedGridList.add(obj);
                }
            } else {
                // Convert to readable string
                jsonNodeList.put(toHumanReadableString(gridNode.getMachine().toString()));

                // All next code used to get outer grid from sub network provider
                // Get list of energy cache providers
                Collection<IEnergyGridProvider> providers = ((IEnergyGridProvider)gridNode.getMachine()).providers();

                // Iterate for each provider
                providers.forEach((iEnergyGridProvider -> {
                    // Check if energy provider not from inner grid
                    if(iEnergyGridProvider != mainNet.getCache(IEnergyGrid.class)) {
                        // Pass function call to subnet helper
                        IGrid outerGrid = null;

                        // Surround with try/catch
                        try {
                            // Pass call to subnet helper
                            outerGrid = SubnetHelper.getOuterGridOrNull((IGridCache) iEnergyGridProvider);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        // Check not null
                        if (outerGrid != null) {
                            // Get JSON object
                            JSONObject obj = createJSONFromGridNodes(outerGrid.getNodes());

                            // Write grid node string(only has used)
                            obj.put("iGridProvider", toHumanReadableString(gridNode.getMachine().toString()));

                            // Serialize outer grid
                            serializedGridList.add(obj);
                        }
                    }
                }));
            }
        }

        // Create for_each cycle
        connections.forEach((iGridNodePair -> {
            // Fill "A" node list
            aNodeList.put("Selected Network");

            // Fill "B" node list
            bNodeList.put(toHumanReadableString(iGridNodePair.getRight().getMachine().toString()));
        }));

        // Put created arrays
        network.put("nodes", jsonNodeList); // (1)
        network.put("src", aNodeList); // (2)
        network.put("dest", bNodeList); // (3)
        network.put("data", serializedDataList); // (4)
        network.put("mode", "sub_network"); // (5)
        network.put("iGridData", serializedGridList); // (6)

        // Change inner json
        innerObject = network;
    }
}
