/**
 * @Author Azazell
 */

// Currently selected node
var selectedNode;

// Create name translation map
var stringTranslationMap = new Map();

// Map of all categories
var categoryMap = new Map();

// Category of network data
var networkDataList = ["Active", "Frequency", "Usage"]

// Category of grid flags
var gridFlagDataList = ["CANNOT_CARRY", "CANNOT_CARRY_COMPRESSED", "COMPRESSED_CHANNEL", "DENSE_CAPACITY", "MULTIBLOCK",
    "PREFERRED", "REQUIRE_CHANNEL"
]

// Category of position
var positionDataList = ["X", "Y", "Z"]

// Set key array
var keysArray = ["Network Data", "Grid Flags", "Position"]

// Set category map entries
categoryMap.set("Network Data", networkDataList); // (1)
categoryMap.set("Grid Flags", gridFlagDataList); // (2)
categoryMap.set("Position", positionDataList); // (3)

// jQuery function
(function($) {
    // Wait for ready state
    $(document).ready(function() {
        // Get network // Create network manager
        $.getJSON("json", function(network) {
            // Pass call to function
            graph(network);
        });
    })
})(this.jQuery)

// create a network
var container = document.getElementById('network');

var data = {
    nodes: nodes,
    edges: edges
};

var options = {};

// Create network
var network = new vis.Network(container, data, options);

// Last node clicked
var lastNode;

// Create click event
network.on('click', function(properties) {
    // Get node ids
    var ids = properties.nodes;

    // Get clicked nodes
    var clickedNodes = nodes.get(ids);

    // Check if size greater than 0
    if (clickedNodes.length > 0) {
        // Write 1st node
        selectedNode = clickedNodes[0];

        // Get node message
        var message = nodeMessages[ids[0]];

        // Create html tag
        var innerHTML = "<p> Summary for node: " + selectedNode.label + " </p>"

        // List of keys
        var keys = [];

        // List of values
        var values = new Map;

        // Iterate for each element of array
        Object.entries(message).forEach(entry => {
            // Get key
            keys.push(entry[0]);

            // Get val
            values.set(entry[0], entry[1]);
        });

        // Sort keys
        keys.sort();

        // Check if selected node has changed
        if (selectedNode != lastNode) {
            // Update last node
            lastNode = selectedNode;

            // Clear all text from tabs
            // Iterate for each key
            keys.forEach(function(innerKey, i) {
                // Iterate for each category key
                keysArray.forEach(function(category, i) {
                    // Clear text
                    document.getElementById(category).innerHTML = "";
                })
            })
        }

        // Iterate for each category key
        keysArray.forEach(function(category, i) {
            // Add text to element
            document.getElementById(category).innerHTML += innerHTML;
        })

        // Sort each map
        // Iterate for each key
        keys.forEach(function(innerKey, i) {
            // Iterate for each category key
            keysArray.forEach(function(category, i) {
                categoryMap.get(category).sort();
            });
        });

        // Iterate for each key
        keys.forEach(function(innerKey, i) {
            // Iterate for each category key
            keysArray.forEach(function(category, i) {
                // Iterate for each list of category
                categoryMap.get(category).forEach(function(key, i) {
                    // Should current cycle not be written?
                    var skip = false;

                    // Check if keys are equal
                    if (innerKey == key) {
                        // Check if key is frequency
                        if (key == "Frequency") {
                            // Check if frequency coded correctly
                            if (values.get(key) <= 32767 && values.get(key) >= -32768) {
                                // Update height of element
                                document.getElementById(category).style.height = "180px";

                                // Check if inner html not already contains this entry
                                if (document.getElementById(category).innerHTML.indexOf("<h3> " + key + " : " + values.get(key) + " </h3> ") !== -1)
                                // Add text to element
                                    document.getElementById(category).innerHTML += "<h3> " + key + " : " + values.get(key) + " </h3> "
                            } else {
                                skip = true;
                            }
                        }

                        // Check if state not intended to skip
                        if (!skip)
                        // Add text to element
                            document.getElementById(category).innerHTML += "<h3> " + key + " : " + values.get(key) + " </h3> "
                    }
                });
            });
        });
    }
});

// Is user currently viewing subnetwork?
var showingSubnet = false;

// Create double click event
network.on('doubleClick', function(properties) {
    // Get node ids
    var ids = properties.nodes;

    // Get clicked nodes
    var clickedNodes = nodes.get(ids);

    // Check if size greater than 0
    if (clickedNodes.length > 0) {
        // Check if mode is sub network mode
        if (mode != "sub_network")
            return;

        // Write 1st node
        selectedNode = clickedNodes[0];

        // Create new node set
        //var subNodes = network.data.nodes = new vis.DataSet();

        // Iterate for each sub network
        sub_networkList.forEach(function(network, i) {
            // Check if provider of network equal to clicked node
            if (network.iGridProvider == selectedNode.hashLabel) {
                // Iterate for each node in data set
                nodes.forEach(function(node, i) {
                    // remove node
                    nodes.remove(node);
                })

                // graph sub network
                graph(network);

                // toggle var
                showingSubnet = true;
            }
        })

        // Check if player viewing subnetwork
    } else if (showingSubnet) {
        // Clear current canvas
        // Iterate for each node in data set
        nodes.forEach(function(node, i) {
            // remove node
            nodes.remove(node);
        });

        // Iterate for each edge in data set
        edges.forEach(function(edge, i) {
            // remove edge
            edges.remove(edge);
        });

        // Draw json file again
        // jQuery function
        // Wait for ready state
        jQuery(document).ready(function() {
            // Get network // Create network manager
            $.getJSON("json", function(network) {
                // Pass call to function
                graph(network);
            });
        })

        // Trigger var
        showingSubnet = false;
    }
});