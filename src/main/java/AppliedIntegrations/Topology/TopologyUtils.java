package AppliedIntegrations.Topology;

import AppliedIntegrations.AIConfig;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TopologyUtils {

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
                graphNodes(grid, player, machine);
                break;

        }
    }

    private static void sendLink(EntityPlayer player) {
        // Create message ("Localhost: <PORT_IN_CONFIG>"), and send it as link to player)
        player.sendMessage(ForgeHooks.newChatWithLinks("localhost:" + AIConfig.webUIPort));
    }


    private static void graphNodes(IGrid grid, EntityPlayer player, IGridHost machine) {
        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( Only: " + machine.toString() +" ):"));

        // Send link message
        sendLink(player);
    }

    private static void graphSubnetworks(IGrid grid, EntityPlayer player) {
        // Send message to player
        player.sendMessage(new TextComponentString("UI generated ( Sub-networks ):"));

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
        // Create node object
        JSONObject network = new JSONObject();
        // Create node array
        JSONArray jsonNodeList = new JSONArray();
        // Create list of connections
        JSONArray aNodeList = new JSONArray(); // (1)
        JSONArray bNodeList = new JSONArray(); // (2)

        // Iterate over all nodes
        for (IGridNode gridNode : nodeList) {
            // Convert to readable string
            jsonNodeList.put(toHumanReadableString(gridNode.getMachine().toString()));

            // Create for_each cycle
            gridNode.getConnections().forEach((iGridConnection -> {
                // Fill "A" node list
                aNodeList.put(toHumanReadableString(iGridConnection.a().getMachine().toString()));

                // Fill "B" node list
                bNodeList.put(toHumanReadableString(iGridConnection.b().getMachine().toString()));
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
