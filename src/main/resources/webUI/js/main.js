/**
 * @Author Azazell
 */

var selectedNode;
var stringTranslationMap = new Map();
var categoryMap = new Map();
var networkDataList = ["Active", "Frequency", "Usage"]
var gridFlagDataList = ["CANNOT_CARRY", "CANNOT_CARRY_COMPRESSED", "COMPRESSED_CHANNEL", "DENSE_CAPACITY", "MULTIBLOCK",
    "PREFERRED", "REQUIRE_CHANNEL"
]

var positionDataList = ["X", "Y", "Z"]
var keysArray = ["Network Data", "Grid Flags", "Position"]

categoryMap.set("Network Data", networkDataList);
categoryMap.set("Grid Flags", gridFlagDataList);
categoryMap.set("Position", positionDataList);

// Waits when document will be ready and graphs network
(function($) {
    $(document).ready(function() {
        $.getJSON("json", function(network) {
            graph(network);
        });
    })
})(this.jQuery)

var container = document.getElementById('network');
var data = {
    nodes: nodes,
    edges: edges
};

var options = {};

var network = new vis.Network(container, data, options);
var lastNode;

network.on('click', function(properties) {
    var ids = properties.nodes;
    var clickedNodes = nodes.get(ids);
    if (clickedNodes.length > 0) {
        selectedNode = clickedNodes[0];
        var message = nodeMessages[ids[0]];

        var innerHTML = "<p> Summary for node: " + selectedNode.label + " </p>"
        var keys = [];
        var values = new Map;
        Object.entries(message).forEach(entry => {
            keys.push(entry[0]);
            values.set(entry[0], entry[1]);
        });

        keys.sort();
        if (selectedNode != lastNode) {
            lastNode = selectedNode;

            // Clear all text from tabs
            keys.forEach(function(innerKey, i) {
                keysArray.forEach(function(category, i) {
                    document.getElementById(category).innerHTML = "";
                })
            })
        }

        keysArray.forEach(function(category, i) {
            document.getElementById(category).innerHTML += innerHTML;
        })

        // Sort each map
        keys.forEach(function(innerKey, i) {
            keysArray.forEach(function(category, i) {
                categoryMap.get(category).sort();
            });
        });

        keys.forEach(function(innerKey, i) {
            keysArray.forEach(function(category, i) {
                categoryMap.get(category).forEach(function(key, i) {
                    var skip = false;

                    if (innerKey == key) {
                        if (key == "Frequency") {
                            if (values.get(key) <= 32767 && values.get(key) >= -32768) {
                                document.getElementById(category).style.height = "180px";

                                if (document.getElementById(category).innerHTML.indexOf("<h3> " + key + " : " + values.get(key) + " </h3> ") !== -1)
                                    document.getElementById(category).innerHTML += "<h3> " + key + " : " + values.get(key) + " </h3> "
                            } else {
                                skip = true;
                            }
                        }

                        if (!skip)
                            document.getElementById(category).innerHTML += "<h3> " + key + " : " + values.get(key) + " </h3> "
                    }
                });
            });
        });
    }
});

// Is user currently viewing subnetwork?
var showingSubnet = false;

network.on('doubleClick', function(properties) {
    var ids = properties.nodes;
    var clickedNodes = nodes.get(ids);
    if (clickedNodes.length > 0) {
        if (mode != "sub_network")
            return;

        selectedNode = clickedNodes[0];
        //var subNodes = network.data.nodes = new vis.DataSet();
        sub_networkList.forEach(function(network, i) {
            if (network.iGridProvider == selectedNode.hashLabel) {
                nodes.forEach(function(node, i) {
                    nodes.remove(node);
                })

                graph(network);

                showingSubnet = true;
            }
        })

    } else if (showingSubnet) {
        // Clear current canvas
        nodes.forEach(function(node, i) {
            nodes.remove(node);
        });

        edges.forEach(function(edge, i) {
            edges.remove(edge);
        });

        // Draw json file again
        jQuery(document).ready(function() {
            $.getJSON("json", function(network) {
                graph(network);
            });
        })

        showingSubnet = false;
    }
});