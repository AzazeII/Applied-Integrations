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
	 *
	 * @param grid    grid of node selected
	 * @param player  Player, who queried this request
	 * @param mode    Current working mode of graph tool
	 * @param machine Machine, which queried this request
	 */
	public static void createWebUI(IGrid grid, EntityPlayer player, GraphToolMode mode, IGridHost machine) {
		switch (mode) {
			case ALL:
				graphAll(grid, player);
				break;
            /*case LINE:
                graphLineNodes(grid, player);
                break;*/

			case P2P_LINKS:
				graphP2PLinks(grid, player);
				break;

			case SUBNETWORK:
				graphSubnetworks(grid, player);
				break;

			case NODE_CLICKED:
				graphGiven(grid, player, machine);
				break;
		}
	}

	@Nonnull
	public static JSONObject getInnerObject() {
		return innerObject;
	}

	public static TextComponentString createLink() {
		ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://127.0.0.1:" + AIConfig.webUIPort);
		TextComponentString message = new TextComponentString(TextFormatting.AQUA + (TextFormatting.UNDERLINE + "http://127.0.0.1:" + AIConfig.webUIPort));
		message.getStyle().setClickEvent(click);
		return message;
	}

	private static void graphGiven(IGrid grid, EntityPlayer player, IGridHost machine) {
		List<IGridNode> nodeList = new ArrayList<>();
		for (IGridNode node : grid.getNodes()) {
			if (node.getMachine().getClass() == machine.getClass()) {
				nodeList.add(node);
			}
		}
		innerObject = createJSONFromGridNodes(nodeList);
	}

	private static void graphSubnetworks(IGrid grid, EntityPlayer player) {
		EnergyGridCache iEnergyGrid = grid.getCache(IEnergyGrid.class);
		Collection<IEnergyGridProvider> gridProviders = iEnergyGrid.providers();
		List<IGridNode> nodeList = new ArrayList<>();
		List<Pair<IGridNode, IGridNode>> connections = new ArrayList<>();
		nodeList.add(grid.getPivot());
		gridProviders.forEach((iEnergyGridProvider -> {
			Pair<IGridNode, IGridNode> pair = new Pair<IGridNode, IGridNode>() {
				@Override
				public IGridNode getLeft() {
					return grid.getPivot();
				}

				@Override
				public IGridNode getRight() {
					if (iEnergyGridProvider instanceof IGridHost) {
						return ((IGridHost) iEnergyGridProvider).getGridNode(AEPartLocation.INTERNAL);
					}
					return null;
				}

				@Override
				public IGridNode setValue(IGridNode value) {
					return null;
				}
			};

			if (pair.getRight() != null) {
				connections.add(pair);
			}

			if (iEnergyGridProvider instanceof IGridHost) {
				nodeList.add(((IGridHost) iEnergyGridProvider).getGridNode(AEPartLocation.INTERNAL));
			}
		}));

		createSubnetworkJSON(nodeList, connections, grid);
	}

	private static void graphLineNodes(IGrid grid, EntityPlayer player) {

	}

	private static void graphAll(IGrid grid, EntityPlayer player) {
		innerObject = createJSONFromGridNodes(grid.getNodes());
	}

	private static void graphP2PLinks(IGrid grid, EntityPlayer player) {
		P2PCache cache = grid.getCache(P2PCache.class);
		List<PartP2PTunnel> tunnelList = new LinkedList<>();
		grid.getNodes().forEach((iGridNode -> {
			if (iGridNode.getMachine() instanceof PartP2PTunnel) {
				tunnelList.add((PartP2PTunnel) iGridNode.getMachine());
			}
		}));

		List<Pair<IGridNode, IGridNode>> connections = new LinkedList<>();

		tunnelList.forEach((partP2PTunnel -> {
			cache.getOutputs(partP2PTunnel.getFrequency(), partP2PTunnel.getClass()).forEach((tunnelRight) -> {
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

		List<IGridNode> nodeList = new LinkedList<>();
		tunnelList.forEach((partP2PTunnel -> nodeList.add(partP2PTunnel.getGridNode())));
		innerObject = createJSONFromConnections(nodeList, connections);
	}

	private static String toHumanReadableString(String toString) {
		String[] array = toString.split("\\.");
		return array[array.length - 1];
	}

	private static JSONObject serializeNodeData(IGridNode node) {
		JSONObject temp = new JSONObject();
		temp.put("Active", node.isActive());

		temp.put("X", node.getGridBlock().getLocation().x);
		temp.put("Y", node.getGridBlock().getLocation().y);
		temp.put("Z", node.getGridBlock().getLocation().z);
		temp.put("Hex", node.getGridBlock().getGridColor().mediumVariant);

		for (GridFlags flag : GridFlags.values()) {
			temp.put(flag.name(), node.getGridBlock().getFlags().contains(flag));
		}

		temp.put("Usage", node.getGridBlock().getIdlePowerUsage() + " AE");
		if (node.getMachine() instanceof PartP2PTunnel) {
			PartP2PTunnel<?> partP2PTunnel = (PartP2PTunnel<?>) node.getMachine();
			temp.put("Frequency", partP2PTunnel.getFrequency());
		} else {
			temp.put("Frequency", Short.MAX_VALUE + 1);
		}

		temp.put("Pivot", node.getGrid().getPivot() == node);
		return temp;
	}

	private static JSONObject createJSONFromGridNodes(Iterable<IGridNode> nodeList) {
		List<Pair<IGridNode, IGridNode>> connections = new LinkedList<>();

		for (IGridNode gridNode : nodeList) {
			gridNode.getConnections().forEach((iGridConnection -> {
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

		return createJSONFromConnections(nodeList, connections);
	}

	private static JSONObject createJSONFromConnections(Iterable<IGridNode> nodeList, List<Pair<IGridNode, IGridNode>> connections) {
		JSONObject network = new JSONObject();
		JSONArray jsonNodeList = new JSONArray();
		JSONArray aNodeList = new JSONArray();
		JSONArray bNodeList = new JSONArray();
		List<JSONObject> serializedDataList = new ArrayList<>();

		connections.forEach((iGridNodeIGridNodePair -> {
			aNodeList.put(toHumanReadableString(iGridNodeIGridNodePair.getLeft().getMachine().toString()));
			bNodeList.put(toHumanReadableString(iGridNodeIGridNodePair.getRight().getMachine().toString()));
		}));

		nodeList.forEach((iGridNode -> {
			jsonNodeList.put(toHumanReadableString(iGridNode.getMachine().toString()));
			serializedDataList.add(serializeNodeData(iGridNode));
		}));

		network.put("nodes", jsonNodeList);
		network.put("src", aNodeList);
		network.put("dest", bNodeList);
		network.put("data", serializedDataList);
		network.put("mode", "not_sub_network");

		return network;
	}

	// Same as createJSONFromGridNodes(list), but with custom connection list
	private static void createSubnetworkJSON(List<IGridNode> nodeList, List<Pair<IGridNode, IGridNode>> connections, IGrid mainNet) {
		JSONObject network = new JSONObject();
		JSONArray jsonNodeList = new JSONArray();
		JSONArray aNodeList = new JSONArray();
		JSONArray bNodeList = new JSONArray();

		List<JSONObject> serializedDataList = new ArrayList<>();
		List<JSONObject> serializedGridList = new ArrayList<>();
		for (IGridNode gridNode : nodeList) {
			serializedDataList.add(serializeNodeData(gridNode));
			if (gridNode == mainNet.getPivot()) {
				if (!jsonNodeList.toList().contains("Selected Network")) {
					jsonNodeList.put("Selected Network");
					JSONObject obj = createJSONFromGridNodes(mainNet.getNodes());
					obj.put("iGridProvider", toHumanReadableString(gridNode.getMachine().toString()));
					serializedGridList.add(obj);
				}
			} else {
				jsonNodeList.put(toHumanReadableString(gridNode.getMachine().toString()));

				// All next code used to get outer grid from sub network provider
				Collection<IEnergyGridProvider> providers = ((IEnergyGridProvider) gridNode.getMachine()).providers();
				providers.forEach((iEnergyGridProvider -> {
					if (iEnergyGridProvider != mainNet.getCache(IEnergyGrid.class)) {
						IGrid outerGrid = null;

						try {
							outerGrid = SubnetHelper.getOuterGridOrNull((IGridCache) iEnergyGridProvider);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}

						if (outerGrid != null) {
							JSONObject obj = createJSONFromGridNodes(outerGrid.getNodes());
							obj.put("iGridProvider", toHumanReadableString(gridNode.getMachine().toString()));
							serializedGridList.add(obj);
						}
					}
				}));
			}
		}

		connections.forEach((iGridNodePair -> {
			aNodeList.put("Selected Network");
			bNodeList.put(toHumanReadableString(iGridNodePair.getRight().getMachine().toString()));
		}));

		network.put("nodes", jsonNodeList);
		network.put("src", aNodeList);
		network.put("dest", bNodeList);
		network.put("data", serializedDataList);
		network.put("mode", "sub_network");
		network.put("iGridData", serializedGridList);
		innerObject = network;
	}
}
